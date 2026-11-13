package com.shelfsag.spacer.presentation.cutlist

import com.shelfsag.spacer.domain.model.CutListKind
import com.shelfsag.spacer.domain.model.CutListSection
import com.shelfsag.spacer.domain.model.SagRating
import com.shelfsag.spacer.domain.model.ShelfMaterial
import com.shelfsag.spacer.domain.model.UnitSystem

data class CutListRowState(
    val id: String,
    val section: CutListSection,
    val kind: CutListKind,
    val material: ShelfMaterial,
    val quantity: Int,
    val lengthText: String,
    val depthText: String,
    val thicknessText: String,
    val armText: String,
    val buyLengthText: String,
    val bought: Boolean
)

data class CutListUiState(
    val loading: Boolean = true,
    val planName: String = "",
    val room: String = "",
    val rows: List<CutListRowState> = emptyList(),
    val boardMetresText: String = "",
    val bracketCount: Int = 0,
    val sagText: String = "",
    val rating: SagRating = SagRating.INVISIBLE,
    val units: UnitSystem = UnitSystem.METRIC
)
