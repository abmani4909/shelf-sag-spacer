package com.shelfsag.spacer.presentation.cutlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelfsag.spacer.domain.model.ShelfPlan
import com.shelfsag.spacer.domain.model.ShelfSettings
import com.shelfsag.spacer.domain.repository.PlanRepository
import com.shelfsag.spacer.domain.repository.SettingsRepository
import com.shelfsag.spacer.domain.usecase.BuildCutListUseCase
import com.shelfsag.spacer.domain.usecase.CalculateSagUseCase
import com.shelfsag.spacer.domain.usecase.FormatMeasureUseCase
import com.shelfsag.spacer.domain.usecase.RateSagUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface CutListEvent {
    data object Saved : CutListEvent
}

class CutListViewModel(
    private val planId: String?,
    private val planRepository: PlanRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val buildCutList = BuildCutListUseCase()
    private val calculateSag = CalculateSagUseCase()
    private val rateSag = RateSagUseCase()
    private val formatter = FormatMeasureUseCase()

    private val _state = MutableStateFlow(CutListUiState())
    val state: StateFlow<CutListUiState> = _state.asStateFlow()

    private val eventChannel = Channel<CutListEvent>(Channel.BUFFERED)
    val events: Flow<CutListEvent> = eventChannel.receiveAsFlow()

    private var settings = ShelfSettings()
    private var plan: ShelfPlan? = null

    init {
        viewModelScope.launch {
            settings = settingsRepository.settings()
            plan = planId?.let { planRepository.plan(it) }
            recompute()
        }
    }

    fun toggleBought(itemId: String) {
        val current = plan ?: return
        val bought = current.boughtItemIds.toMutableSet()
        if (!bought.add(itemId)) bought.remove(itemId)
        plan = current.copy(boughtItemIds = bought)
        recompute()
    }

    fun savePlan() {
        val current = plan ?: return
        viewModelScope.launch {
            planRepository.save(current)
            eventChannel.send(CutListEvent.Saved)
        }
    }

    private fun recompute() {
        val current = plan ?: return
        val brackets = if (current.bracketPositions.isEmpty()) 2 else current.bracketPositions.size
        val items = buildCutList(current, brackets)
        val span = current.widestSpanMm
        val sag = calculateSag(
            spanMm = span,
            depthMm = current.depthMm.toDouble(),
            thicknessMm = current.thicknessMm.toDouble(),
            modulusMpa = current.material.modulusMpa,
            kgPerMetre = current.kgPerMetre
        )
        _state.value = _state.value.copy(
            loading = false,
            planName = current.name,
            room = current.room,
            rows = items.map { item ->
                CutListRowState(
                    id = item.id,
                    section = item.section,
                    kind = item.kind,
                    material = item.material,
                    quantity = item.quantity,
                    lengthText = formatter.length(item.lengthMm, settings.units),
                    depthText = formatter.length(item.depthMm, settings.units),
                    thicknessText = formatter.length(item.thicknessMm, settings.units),
                    armText = formatter.length(item.armMm, settings.units),
                    buyLengthText = formatter.length(item.buyLengthMm, settings.units),
                    bought = current.boughtItemIds.contains(item.id)
                )
            },
            boardMetresText = formatter.metres(current.lengthMm.toDouble()),
            bracketCount = brackets,
            sagText = formatter.sag(sag, settings.units),
            rating = rateSag(sag, span, settings.sagLimitK, settings.hardLimitMm),
            units = settings.units
        )
    }
}
