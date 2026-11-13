package com.shelfsag.spacer.presentation.settings

import com.shelfsag.spacer.core.config.GameConfig
import com.shelfsag.spacer.domain.model.UnitSystem

data class SettingsUiState(
    val loading: Boolean = true,
    val units: UnitSystem = UnitSystem.METRIC,
    val sagLimitK: Int = GameConfig.DEFAULT_SAG_LIMIT_K,
    val hardLimitMm: Double = GameConfig.DEFAULT_HARD_LIMIT_MM,
    val hardLimitText: String = "",
    val studSpacingMm: Double = GameConfig.STUD_SPACING_MM
) {
    val canLowerHardLimit: Boolean get() = hardLimitMm > GameConfig.HARD_LIMIT_MIN_MM
    val canRaiseHardLimit: Boolean get() = hardLimitMm < GameConfig.HARD_LIMIT_MAX_MM
}
