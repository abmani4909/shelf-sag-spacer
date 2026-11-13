package com.shelfsag.spacer.presentation.menu

import com.shelfsag.spacer.domain.model.SagRating
import com.shelfsag.spacer.domain.model.ShelfMaterial
import com.shelfsag.spacer.domain.model.UnitSystem

data class PlanSummary(
    val id: String,
    val name: String,
    val room: String,
    val material: ShelfMaterial,
    val lengthText: String,
    val depthText: String,
    val thicknessText: String,
    val sagText: String,
    val rating: SagRating
)

data class RoomGroup(
    val room: String,
    val plans: List<PlanSummary>
)

data class MenuUiState(
    val loading: Boolean = true,
    val groups: List<RoomGroup> = emptyList(),
    val planCount: Int = 0,
    val roomCount: Int = 0,
    val safeCount: Int = 0,
    val units: UnitSystem = UnitSystem.METRIC,
    val pendingDeleteId: String? = null,
    val pendingDeleteName: String = ""
)
