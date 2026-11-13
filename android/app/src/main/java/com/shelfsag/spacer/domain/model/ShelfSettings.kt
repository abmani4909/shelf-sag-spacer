package com.shelfsag.spacer.domain.model

import com.shelfsag.spacer.core.config.GameConfig

data class ShelfSettings(
    val units: UnitSystem = UnitSystem.METRIC,
    val sagLimitK: Int = GameConfig.DEFAULT_SAG_LIMIT_K,
    val hardLimitMm: Double = GameConfig.DEFAULT_HARD_LIMIT_MM,
    val studSpacingMm: Double = GameConfig.STUD_SPACING_MM
)
