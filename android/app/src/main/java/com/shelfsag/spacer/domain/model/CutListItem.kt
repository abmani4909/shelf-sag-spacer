package com.shelfsag.spacer.domain.model

enum class CutListSection {
    BOARDS,
    HARDWARE,
    FASTENERS
}

enum class CutListKind {
    BOARD,
    BRACKET,
    WALL_SCREW,
    SHELF_SCREW,
    WALL_PLUG
}

data class CutListItem(
    val id: String,
    val section: CutListSection,
    val kind: CutListKind,
    val quantity: Int,
    val material: ShelfMaterial,
    val lengthMm: Double = 0.0,
    val depthMm: Double = 0.0,
    val thicknessMm: Double = 0.0,
    val armMm: Double = 0.0,
    val buyLengthMm: Double = 0.0
)
