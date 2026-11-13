package com.shelfsag.spacer.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelfsag.spacer.domain.model.SagRating
import com.shelfsag.spacer.domain.repository.PlanRepository
import com.shelfsag.spacer.domain.repository.SettingsRepository
import com.shelfsag.spacer.domain.usecase.CalculateSagUseCase
import com.shelfsag.spacer.domain.usecase.FormatMeasureUseCase
import com.shelfsag.spacer.domain.usecase.RateSagUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val planRepository: PlanRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val calculateSag = CalculateSagUseCase()
    private val rateSag = RateSagUseCase()
    private val formatter = FormatMeasureUseCase()

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val settings = settingsRepository.settings()
            val plans = planRepository.plans()
            val summaries = plans.map { plan ->
                val span = plan.widestSpanMm
                val sag = calculateSag(
                    spanMm = span,
                    depthMm = plan.depthMm.toDouble(),
                    thicknessMm = plan.thicknessMm.toDouble(),
                    modulusMpa = plan.material.modulusMpa,
                    kgPerMetre = plan.kgPerMetre
                )
                PlanSummary(
                    id = plan.id,
                    name = plan.name,
                    room = plan.room,
                    material = plan.material,
                    lengthText = formatter.length(plan.lengthMm.toDouble(), settings.units),
                    depthText = formatter.length(plan.depthMm.toDouble(), settings.units),
                    thicknessText = formatter.length(plan.thicknessMm.toDouble(), settings.units),
                    sagText = formatter.sag(sag, settings.units),
                    rating = rateSag(sag, span, settings.sagLimitK, settings.hardLimitMm)
                )
            }
            val groups = summaries
                .groupBy { it.room }
                .map { entry -> RoomGroup(entry.key, entry.value) }
                .sortedBy { it.room }
            _state.update {
                it.copy(
                    loading = false,
                    groups = groups,
                    planCount = summaries.size,
                    roomCount = groups.size,
                    safeCount = summaries.count { plan -> plan.rating == SagRating.INVISIBLE },
                    units = settings.units
                )
            }
        }
    }

    fun askDelete(plan: PlanSummary) {
        _state.update { it.copy(pendingDeleteId = plan.id, pendingDeleteName = plan.name) }
    }

    fun dismissDelete() {
        _state.update { it.copy(pendingDeleteId = null, pendingDeleteName = "") }
    }

    fun confirmDelete() {
        val target = _state.value.pendingDeleteId ?: return
        viewModelScope.launch {
            planRepository.delete(target)
            _state.update { it.copy(pendingDeleteId = null, pendingDeleteName = "") }
            refresh()
        }
    }
}
