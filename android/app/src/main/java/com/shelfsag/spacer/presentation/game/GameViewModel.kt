package com.shelfsag.spacer.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelfsag.spacer.core.config.GameConfig
import com.shelfsag.spacer.data.sample.SampleData
import com.shelfsag.spacer.domain.model.ShelfLoad
import com.shelfsag.spacer.domain.model.ShelfMaterial
import com.shelfsag.spacer.domain.model.ShelfPlan
import com.shelfsag.spacer.domain.model.ShelfSettings
import com.shelfsag.spacer.domain.repository.PlanRepository
import com.shelfsag.spacer.domain.repository.SettingsRepository
import com.shelfsag.spacer.domain.usecase.CalculateSagUseCase
import com.shelfsag.spacer.domain.usecase.FormatMeasureUseCase
import com.shelfsag.spacer.domain.usecase.RateSagUseCase
import com.shelfsag.spacer.domain.usecase.SuggestBracketsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface GameEvent {
    data class Estimated(val planId: String) : GameEvent
}

class GameViewModel(
    private val planId: String?,
    private val planRepository: PlanRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val calculateSag = CalculateSagUseCase()
    private val rateSag = RateSagUseCase()
    private val suggestBrackets = SuggestBracketsUseCase()
    private val formatter = FormatMeasureUseCase()

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var settings = ShelfSettings()
    private var plan = ShelfPlan(id = "", name = "", room = SampleData.DEFAULT_ROOM)

    init {
        viewModelScope.launch {
            settings = settingsRepository.settings()
            val existing = planId?.let { planRepository.plan(it) }
            plan = existing ?: ShelfPlan(
                id = "plan-" + System.currentTimeMillis(),
                name = SampleData.newPlanName(planRepository.plans().size),
                room = SampleData.DEFAULT_ROOM,
                studSpacingMm = settings.studSpacingMm
            )
            recompute()
        }
    }

    fun selectMaterial(material: ShelfMaterial) {
        plan = plan.copy(materialId = material.id)
        recompute()
    }

    fun selectThickness(thicknessMm: Int) {
        plan = plan.copy(thicknessMm = thicknessMm)
        recompute()
    }

    fun selectLoad(load: ShelfLoad) {
        plan = plan.copy(loadId = load.id)
        recompute()
    }

    fun setCustomKg(input: String) {
        val cleaned = input.filter { it.isDigit() || it == '.' }.take(6)
        val kilograms = cleaned.toDoubleOrNull() ?: 0.0
        plan = plan.copy(loadId = ShelfLoad.CUSTOM.id, customKg = kilograms)
        recompute(customInput = cleaned)
    }

    fun changeLength(stepCount: Int) {
        val next = (plan.lengthMm + stepCount * GameConfig.LENGTH_STEP_MM)
            .coerceIn(GameConfig.LENGTH_MIN_MM, GameConfig.LENGTH_MAX_MM)
        plan = plan.copy(lengthMm = next)
        recompute()
    }

    fun changeDepth(stepCount: Int) {
        val next = (plan.depthMm + stepCount * GameConfig.DEPTH_STEP_MM)
            .coerceIn(GameConfig.DEPTH_MIN_MM, GameConfig.DEPTH_MAX_MM)
        plan = plan.copy(depthMm = next)
        recompute()
    }

    fun openMaterialDialog() {
        _state.value = _state.value.copy(showMaterialDialog = true, showLoadDialog = false)
    }

    fun openLoadDialog() {
        _state.value = _state.value.copy(showLoadDialog = true, showMaterialDialog = false)
    }

    fun closeDialogs() {
        _state.value = _state.value.copy(showMaterialDialog = false, showLoadDialog = false)
    }

    fun estimate() {
        viewModelScope.launch {
            val maxSpan = currentMaxSpan()
            val positions = suggestBrackets.positions(plan.lengthMm.toDouble(), maxSpan)
            val saved = planRepository.save(
                plan.copy(
                    bracketPositions = positions,
                    studSpacingMm = settings.studSpacingMm
                )
            )
            plan = saved
            eventChannel.send(GameEvent.Estimated(saved.id))
        }
    }

    private fun currentMaxSpan(): Double = suggestBrackets.maxSpan(
        depthMm = plan.depthMm.toDouble(),
        thicknessMm = plan.thicknessMm.toDouble(),
        modulusMpa = plan.material.modulusMpa,
        kgPerMetre = plan.kgPerMetre,
        limitK = settings.sagLimitK,
        hardLimitMm = settings.hardLimitMm
    )

    private fun recompute(customInput: String? = null) {
        val span = plan.lengthMm.toDouble()
        val sag = calculateSag(
            spanMm = span,
            depthMm = plan.depthMm.toDouble(),
            thicknessMm = plan.thicknessMm.toDouble(),
            modulusMpa = plan.material.modulusMpa,
            kgPerMetre = plan.kgPerMetre
        )
        val maxSpan = currentMaxSpan()
        val current = _state.value
        _state.value = current.copy(
            loading = false,
            planName = plan.name,
            room = plan.room,
            material = plan.material,
            thicknessMm = plan.thicknessMm,
            load = plan.load,
            customKgInput = customInput ?: current.customKgInput,
            lengthMm = plan.lengthMm,
            depthMm = plan.depthMm,
            units = settings.units,
            limitK = settings.sagLimitK,
            sagText = formatter.sag(sag, settings.units),
            sagFraction = (sag / 12.0).coerceIn(0.04, 1.0).toFloat(),
            rating = rateSag(sag, span, settings.sagLimitK, settings.hardLimitMm),
            spanText = formatter.length(span, settings.units),
            maxSpanText = formatter.length(maxSpan, settings.units),
            lengthText = formatter.length(plan.lengthMm.toDouble(), settings.units),
            depthText = formatter.length(plan.depthMm.toDouble(), settings.units),
            thicknessText = formatter.length(plan.thicknessMm.toDouble(), settings.units),
            loadValueText = formatter.load(plan.kgPerMetre, settings.units),
            thicknessLabels = GameConfig.THICKNESS_OPTIONS.map {
                formatter.length(it.toDouble(), settings.units)
            },
            loadValues = ShelfLoad.entries.map { entry ->
                val perMetre = if (entry == ShelfLoad.CUSTOM) plan.kgPerMetre else entry.kgPerMetre
                formatter.load(perMetre, settings.units)
            }
        )
    }
}
