package com.shelfsag.spacer.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelfsag.spacer.domain.repository.PlanRepository
import com.shelfsag.spacer.domain.repository.SettingsRepository
import com.shelfsag.spacer.domain.usecase.CalculateSagUseCase
import com.shelfsag.spacer.domain.usecase.FormatMeasureUseCase
import com.shelfsag.spacer.domain.usecase.RateSagUseCase
import com.shelfsag.spacer.domain.usecase.SuggestBracketsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameOverViewModel(
    private val planId: String?,
    private val planRepository: PlanRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val calculateSag = CalculateSagUseCase()
    private val rateSag = RateSagUseCase()
    private val suggestBrackets = SuggestBracketsUseCase()
    private val formatter = FormatMeasureUseCase()

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = settingsRepository.settings()
            val plan = planId?.let { planRepository.plan(it) } ?: return@launch
            val span = plan.widestSpanMm
            val sag = calculateSag(
                spanMm = span,
                depthMm = plan.depthMm.toDouble(),
                thicknessMm = plan.thicknessMm.toDouble(),
                modulusMpa = plan.material.modulusMpa,
                kgPerMetre = plan.kgPerMetre
            )
            val maxSpan = suggestBrackets.maxSpan(
                depthMm = plan.depthMm.toDouble(),
                thicknessMm = plan.thicknessMm.toDouble(),
                modulusMpa = plan.material.modulusMpa,
                kgPerMetre = plan.kgPerMetre,
                limitK = settings.sagLimitK,
                hardLimitMm = settings.hardLimitMm
            )
            val brackets = if (plan.bracketPositions.isEmpty()) {
                suggestBrackets.positions(plan.lengthMm.toDouble(), maxSpan).size
            } else {
                plan.bracketPositions.size
            }
            _state.update {
                it.copy(
                    loading = false,
                    planName = plan.name,
                    room = plan.room,
                    sagText = formatter.sag(sag, settings.units),
                    sagFraction = (sag / 12.0).coerceIn(0.04, 1.0).toFloat(),
                    rating = rateSag(sag, span, settings.sagLimitK, settings.hardLimitMm),
                    ratioK = settings.sagLimitK,
                    maxSpanText = formatter.length(maxSpan, settings.units),
                    bracketCount = brackets,
                    load = plan.load,
                    units = settings.units
                )
            }
        }
    }
}
