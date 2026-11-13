package com.shelfsag.spacer.core.ui

import com.shelfsag.spacer.R
import com.shelfsag.spacer.domain.model.SagRating
import com.shelfsag.spacer.domain.model.ShelfLoad
import com.shelfsag.spacer.domain.model.ShelfMaterial
import com.shelfsag.spacer.domain.model.UnitSystem

fun materialNameRes(material: ShelfMaterial): Int = when (material) {
    ShelfMaterial.PINE -> R.string.material_pine
    ShelfMaterial.OAK -> R.string.material_oak
    ShelfMaterial.PLYWOOD -> R.string.material_plywood
    ShelfMaterial.MDF -> R.string.material_mdf
    ShelfMaterial.CHIPBOARD -> R.string.material_chipboard
    ShelfMaterial.GLASS -> R.string.material_glass
}

fun materialNoteRes(material: ShelfMaterial): Int = when (material) {
    ShelfMaterial.PINE -> R.string.material_note_pine
    ShelfMaterial.OAK -> R.string.material_note_oak
    ShelfMaterial.PLYWOOD -> R.string.material_note_plywood
    ShelfMaterial.MDF -> R.string.material_note_mdf
    ShelfMaterial.CHIPBOARD -> R.string.material_note_chipboard
    ShelfMaterial.GLASS -> R.string.material_note_glass
}

fun loadNameRes(load: ShelfLoad): Int = when (load) {
    ShelfLoad.DECOR -> R.string.load_decor
    ShelfLoad.PAPERBACKS -> R.string.load_paperbacks
    ShelfLoad.LARGE_BOOKS -> R.string.load_large_books
    ShelfLoad.RECORDS -> R.string.load_records
    ShelfLoad.TOOLS -> R.string.load_tools
    ShelfLoad.CUSTOM -> R.string.load_custom
}

fun ratingLabelRes(rating: SagRating): Int = when (rating) {
    SagRating.INVISIBLE -> R.string.rating_invisible
    SagRating.NOTICEABLE -> R.string.rating_noticeable
    SagRating.SAGGING -> R.string.rating_sagging
}

fun ratingVerdictRes(rating: SagRating): Int = when (rating) {
    SagRating.INVISIBLE -> R.string.verdict_invisible
    SagRating.NOTICEABLE -> R.string.verdict_noticeable
    SagRating.SAGGING -> R.string.verdict_sagging
}

fun lengthUnitRes(units: UnitSystem): Int =
    if (units == UnitSystem.METRIC) R.string.value_mm else R.string.value_in

fun loadUnitRes(units: UnitSystem): Int =
    if (units == UnitSystem.METRIC) R.string.value_kg_m else R.string.value_lb_ft
