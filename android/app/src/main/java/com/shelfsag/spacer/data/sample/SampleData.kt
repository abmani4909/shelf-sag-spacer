package com.shelfsag.spacer.data.sample

import com.shelfsag.spacer.domain.model.ShelfLoad
import com.shelfsag.spacer.domain.model.ShelfMaterial
import com.shelfsag.spacer.domain.model.ShelfPlan

object SampleData {

    fun starterPlans(): List<ShelfPlan> = listOf(
        ShelfPlan(
            id = "sample-paperback-run",
            name = "Paperback Run",
            room = "Living Room",
            materialId = ShelfMaterial.OAK.id,
            thicknessMm = 18,
            depthMm = 250,
            lengthMm = 1200,
            loadId = ShelfLoad.PAPERBACKS.id,
            updatedAt = 3L
        ),
        ShelfPlan(
            id = "sample-atlas-shelf",
            name = "Atlas Shelf",
            room = "Study",
            materialId = ShelfMaterial.PLYWOOD.id,
            thicknessMm = 18,
            depthMm = 300,
            lengthMm = 1500,
            loadId = ShelfLoad.LARGE_BOOKS.id,
            updatedAt = 2L
        ),
        ShelfPlan(
            id = "sample-tool-ledge",
            name = "Tool Ledge",
            room = "Garage",
            materialId = ShelfMaterial.PINE.id,
            thicknessMm = 25,
            depthMm = 300,
            lengthMm = 1000,
            loadId = ShelfLoad.TOOLS.id,
            updatedAt = 1L
        )
    )

    fun newPlanName(existing: Int): String = "Shelf " + (existing + 1)

    const val DEFAULT_ROOM = "Workshop"
}
