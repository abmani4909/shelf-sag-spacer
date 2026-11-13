package com.shelfsag.spacer.domain.model

import kotlin.math.roundToInt

enum class ShelfMaterial(val id: String, val modulusMpa: Double) {
    PINE("PINE", 9000.0),
    OAK("OAK", 12500.0),
    PLYWOOD("PLYWOOD", 8500.0),
    MDF("MDF", 3600.0),
    CHIPBOARD("CHIPBOARD", 2400.0),
    GLASS("GLASS", 70000.0);

    val modulusGpaText: String
        get() {
            val tenths = (modulusMpa / 100.0).roundToInt()
            return (tenths / 10.0).toString()
        }

    companion object {
        fun fromId(id: String): ShelfMaterial = entries.firstOrNull { it.id == id } ?: OAK
    }
}
