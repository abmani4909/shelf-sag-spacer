package com.shelfsag.spacer.domain.model

enum class ShelfLoad(val id: String, val kgPerMetre: Double) {
    DECOR("DECOR", 6.0),
    PAPERBACKS("PAPERBACKS", 18.0),
    LARGE_BOOKS("LARGE_BOOKS", 32.0),
    RECORDS("RECORDS", 45.0),
    TOOLS("TOOLS", 55.0),
    CUSTOM("CUSTOM", 0.0);

    companion object {
        fun fromId(id: String): ShelfLoad = entries.firstOrNull { it.id == id } ?: PAPERBACKS
    }
}
