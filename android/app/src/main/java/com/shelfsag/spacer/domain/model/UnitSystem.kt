package com.shelfsag.spacer.domain.model

enum class UnitSystem {
    METRIC,
    IMPERIAL;

    companion object {
        fun fromId(id: String): UnitSystem = entries.firstOrNull { it.name == id } ?: METRIC
    }
}
