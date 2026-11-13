package com.shelfsag.spacer.domain.model

import com.shelfsag.spacer.core.config.GameConfig

data class ShelfPlan(
    val id: String,
    val name: String,
    val room: String,
    val materialId: String = ShelfMaterial.OAK.id,
    val thicknessMm: Int = GameConfig.DEFAULT_THICKNESS_MM,
    val depthMm: Int = GameConfig.DEFAULT_DEPTH_MM,
    val lengthMm: Int = GameConfig.DEFAULT_LENGTH_MM,
    val loadId: String = ShelfLoad.PAPERBACKS.id,
    val customKg: Double = GameConfig.DEFAULT_CUSTOM_KG,
    val bracketPositions: List<Double> = emptyList(),
    val studSpacingMm: Double = GameConfig.STUD_SPACING_MM,
    val boughtItemIds: Set<String> = emptySet(),
    val updatedAt: Long = 0L
) {
    val material: ShelfMaterial get() = ShelfMaterial.fromId(materialId)
    val load: ShelfLoad get() = ShelfLoad.fromId(loadId)

    val kgPerMetre: Double
        get() = if (load == ShelfLoad.CUSTOM) {
            val metres = lengthMm / 1000.0
            if (metres <= 0.0) 0.0 else customKg / metres
        } else {
            load.kgPerMetre
        }

    val widestSpanMm: Double
        get() {
            val sorted = bracketPositions.sorted()
            if (sorted.size < 2) return lengthMm.toDouble()
            var widest = 0.0
            for (i in 1 until sorted.size) {
                val gap = sorted[i] - sorted[i - 1]
                if (gap > widest) widest = gap
            }
            return if (widest <= 0.0) lengthMm.toDouble() else widest
        }
}
