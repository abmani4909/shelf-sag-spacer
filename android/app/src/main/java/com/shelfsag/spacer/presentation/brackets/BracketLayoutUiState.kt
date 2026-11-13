package com.shelfsag.spacer.presentation.brackets

import com.shelfsag.spacer.domain.model.SagRating
import com.shelfsag.spacer.domain.model.UnitSystem

data class BracketLayoutUiState(
    val loading: Boolean = true,
    val planName: String = "",
    val room: String = "",
    val lengthMm: Double = 1200.0,
    val positions: List<Double> = emptyList(),
    val studMarks: List<Double> = emptyList(),
    val studSpacingMm: Double = 406.0,
    val studLabels: List<String> = emptyList(),
    val snappedIndex: Int = -1,
    val widestGapText: String = "",
    val spacingText: String = "",
    val onStudCount: Int = 0,
    val overLimit: Boolean = false,
    val overByText: String = "",
    val sagText: String = "",
    val rating: SagRating = SagRating.INVISIBLE,
    val units: UnitSystem = UnitSystem.METRIC
) {
    val bracketCount: Int get() = positions.size
}
