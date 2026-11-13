package com.shelfsag.spacer.presentation.brackets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelfsag.spacer.core.config.GameConfig
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
import kotlin.math.abs
import kotlin.math.max

sealed interface BracketEvent {
    data object Snapped : BracketEvent
}

class BracketLayoutViewModel(
    private val planId: String?,
    private val planRepository: PlanRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val calculateSag = CalculateSagUseCase()
    private val rateSag = RateSagUseCase()
    private val suggestBrackets = SuggestBracketsUseCase()
    private val formatter = FormatMeasureUseCase()

    private val _state = MutableStateFlow(BracketLayoutUiState())
    val state: StateFlow<BracketLayoutUiState> = _state.asStateFlow()

    private val eventChannel = Channel<BracketEvent>(Channel.BUFFERED)
    val events: Flow<BracketEvent> = eventChannel.receiveAsFlow()

    private var settings = ShelfSettings()
    private var plan: ShelfPlan? = null
    private var lastSnapped = -1

    init {
        viewModelScope.launch {
            settings = settingsRepository.settings()
            val loaded = planId?.let { planRepository.plan(it) } ?: return@launch
            val positions = loaded.bracketPositions.ifEmpty {
                suggestBrackets.positions(loaded.lengthMm.toDouble(), maxSpanFor(loaded))
            }
            plan = loaded.copy(bracketPositions = positions)
            recompute()
        }
    }

    fun changeBracketCount(delta: Int) {
        val current = plan ?: return
        val next = (current.bracketPositions.size + delta)
            .coerceIn(GameConfig.MIN_BRACKETS, GameConfig.MAX_BRACKETS)
        plan = current.copy(
            bracketPositions = suggestBrackets.evenPositions(current.lengthMm.toDouble(), next)
        )
        persistAndRecompute()
    }

    fun evenSpacing() {
        val current = plan ?: return
        plan = current.copy(
            bracketPositions = suggestBrackets.evenPositions(
                current.lengthMm.toDouble(),
                current.bracketPositions.size
            )
        )
        persistAndRecompute()
    }

    fun setStudSpacing(spacingMm: Double) {
        val current = plan ?: return
        plan = current.copy(studSpacingMm = spacingMm)
        persistAndRecompute()
    }

    fun dragBracket(index: Int, deltaMm: Double) {
        val current = plan ?: return
        val positions = current.bracketPositions.sorted().toMutableList()
        if (index !in positions.indices) return
        val length = current.lengthMm.toDouble()
        var next = (positions[index] + deltaMm).coerceIn(0.0, length)
        var snappedTo = -1
        val marks = suggestBrackets.studMarks(length, current.studSpacingMm)
        marks.forEachIndexed { markIndex, mark ->
            if (abs(next - mark) <= GameConfig.STUD_SNAP_MM) {
                next = mark
                snappedTo = markIndex
            }
        }
        positions[index] = next
        plan = current.copy(bracketPositions = positions)
        if (snappedTo >= 0 && snappedTo != lastSnapped) {
            lastSnapped = snappedTo
            viewModelScope.launch { eventChannel.send(BracketEvent.Snapped) }
        } else if (snappedTo < 0) {
            lastSnapped = -1
        }
        _state.value = _state.value.copy(snappedIndex = snappedTo)
        recompute()
    }

    fun commit() {
        val target = plan ?: return
        viewModelScope.launch { planRepository.save(target) }
    }

    private fun maxSpanFor(target: ShelfPlan): Double = suggestBrackets.maxSpan(
        depthMm = target.depthMm.toDouble(),
        thicknessMm = target.thicknessMm.toDouble(),
        modulusMpa = target.material.modulusMpa,
        kgPerMetre = target.kgPerMetre,
        limitK = settings.sagLimitK,
        hardLimitMm = settings.hardLimitMm
    )

    private fun persistAndRecompute() {
        recompute()
        val target = plan ?: return
        viewModelScope.launch { planRepository.save(target) }
    }

    private fun recompute() {
        val current = plan ?: return
        val sorted = current.bracketPositions.sorted()
        val length = current.lengthMm.toDouble()
        val widest = current.widestSpanMm
        val maxSpan = maxSpanFor(current)
        val sag = calculateSag(
            spanMm = widest,
            depthMm = current.depthMm.toDouble(),
            thicknessMm = current.thicknessMm.toDouble(),
            modulusMpa = current.material.modulusMpa,
            kgPerMetre = current.kgPerMetre
        )
        val marks = suggestBrackets.studMarks(length, current.studSpacingMm)
        val onStuds = sorted.count { position ->
            marks.any { abs(it - position) <= 1.0 }
        }
        val spacing = if (sorted.size >= 2) {
            (sorted.last() - sorted.first()) / max(sorted.size - 1, 1)
        } else {
            length
        }
        val over = widest - maxSpan
        _state.value = _state.value.copy(
            loading = false,
            planName = current.name,
            room = current.room,
            lengthMm = length,
            positions = sorted,
            studMarks = marks,
            studSpacingMm = current.studSpacingMm,
            studLabels = marks.map { formatter.length(it, settings.units) },
            widestGapText = formatter.length(widest, settings.units),
            spacingText = formatter.length(spacing, settings.units),
            onStudCount = onStuds,
            overLimit = over > 0.0,
            overByText = formatter.length(max(over, 0.0), settings.units),
            sagText = formatter.sag(sag, settings.units),
            rating = rateSag(sag, widest, settings.sagLimitK, settings.hardLimitMm),
            units = settings.units
        )
    }
}
