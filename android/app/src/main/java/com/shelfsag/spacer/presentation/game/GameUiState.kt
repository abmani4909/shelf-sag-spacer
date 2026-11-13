package com.shelfsag.spacer.presentation.game

import com.shelfsag.spacer.domain.model.SagRating
import com.shelfsag.spacer.domain.model.ShelfLoad
import com.shelfsag.spacer.domain.model.ShelfMaterial
import com.shelfsag.spacer.domain.model.UnitSystem

data class GameUiState(
    val loading: Boolean = true,
    val planName: String = "",
    val room: String = "",
    val material: ShelfMaterial = ShelfMaterial.OAK,
    val thicknessMm: Int = 18,
    val load: ShelfLoad = ShelfLoad.PAPERBACKS,
    val customKgInput: String = "",
    val lengthMm: Int = 1200,
    val depthMm: Int = 250,
    val units: UnitSystem = UnitSystem.METRIC,
    val limitK: Int = 360,
    val sagText: String = "",
    val sagFraction: Float = 0f,
    val rating: SagRating = SagRating.INVISIBLE,
    val spanText: String = "",
    val maxSpanText: String = "",
    val lengthText: String = "",
    val depthText: String = "",
    val thicknessText: String = "",
    val loadValueText: String = "",
    val thicknessLabels: List<String> = emptyList(),
    val loadValues: List<String> = emptyList(),
    val showMaterialDialog: Boolean = false,
    val showLoadDialog: Boolean = false
) {
    val canShorten: Boolean get() = lengthMm > 300
    val canLengthen: Boolean get() = lengthMm < 2400
    val canNarrow: Boolean get() = depthMm > 100
    val canWiden: Boolean get() = depthMm < 500
}
