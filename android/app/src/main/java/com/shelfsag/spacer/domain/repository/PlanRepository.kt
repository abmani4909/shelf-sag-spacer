package com.shelfsag.spacer.domain.repository

import com.shelfsag.spacer.domain.model.ShelfPlan

interface PlanRepository {
    suspend fun plans(): List<ShelfPlan>

    suspend fun plan(id: String): ShelfPlan?

    suspend fun save(plan: ShelfPlan): ShelfPlan

    suspend fun delete(id: String)
}
