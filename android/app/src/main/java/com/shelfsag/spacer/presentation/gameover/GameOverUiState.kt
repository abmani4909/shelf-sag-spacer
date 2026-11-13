package com.shelfsag.spacer.presentation.gameover

import com.shelfsag.spacer.domain.model.SagRating
import com.shelfsag.spacer.domain.model.ShelfLoad
import com.shelfsag.spacer.domain.model.UnitSystem

data class GameOverUiState(
    val loading: Boolean = true,
    val planName: String = "",
    val room: String = "",
    val sagText: String = "",
    val sagFraction: Float = 0f,
    val rating: SagRating = SagRating.INVISIBLE,
    val ratioK: Int = 360,
    val maxSpanText: String = "",
    val bracketCount: Int = 2,
    val load: ShelfLoad = ShelfLoad.PAPERBACKS,
    val units: UnitSystem = UnitSystem.METRIC
)
