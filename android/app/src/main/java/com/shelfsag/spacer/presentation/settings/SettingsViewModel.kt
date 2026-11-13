package com.shelfsag.spacer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelfsag.spacer.core.config.GameConfig
import com.shelfsag.spacer.domain.model.ShelfSettings
import com.shelfsag.spacer.domain.model.UnitSystem
import com.shelfsag.spacer.domain.repository.SettingsRepository
import com.shelfsag.spacer.domain.usecase.FormatMeasureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val formatter = FormatMeasureUseCase()

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private var settings = ShelfSettings()

    init {
        viewModelScope.launch {
            settings = settingsRepository.settings()
            publish()
        }
    }

    fun selectUnits(units: UnitSystem) {
        settings = settings.copy(units = units)
        persist()
    }

    fun selectSagLimit(limitK: Int) {
        settings = settings.copy(sagLimitK = limitK)
        persist()
    }

    fun changeHardLimit(stepCount: Int) {
        val next = (settings.hardLimitMm + stepCount * GameConfig.HARD_LIMIT_STEP_MM)
            .coerceIn(GameConfig.HARD_LIMIT_MIN_MM, GameConfig.HARD_LIMIT_MAX_MM)
        settings = settings.copy(hardLimitMm = next)
        persist()
    }

    fun selectStudSpacing(spacingMm: Double) {
        settings = settings.copy(studSpacingMm = spacingMm)
        persist()
    }

    private fun persist() {
        publish()
        viewModelScope.launch { settingsRepository.update(settings) }
    }

    private fun publish() {
        _state.value = SettingsUiState(
            loading = false,
            units = settings.units,
            sagLimitK = settings.sagLimitK,
            hardLimitMm = settings.hardLimitMm,
            hardLimitText = formatter.oneDecimal(settings.hardLimitMm),
            studSpacingMm = settings.studSpacingMm
        )
    }
}
