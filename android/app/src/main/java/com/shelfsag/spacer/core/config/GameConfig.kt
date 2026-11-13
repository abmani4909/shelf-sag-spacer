package com.shelfsag.spacer.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val GRAVITY = 9.81
    const val DEFAULT_SAG_LIMIT_K = 360
    const val DEFAULT_HARD_LIMIT_MM = 5.0
    const val HARD_LIMIT_MIN_MM = 2.0
    const val HARD_LIMIT_MAX_MM = 10.0
    const val HARD_LIMIT_STEP_MM = 0.5

    const val STUD_SPACING_MM = 406.0
    const val STUD_SPACING_WIDE_MM = 610.0
    const val STUD_SPACING_OFF_MM = 0.0
    const val STUD_SNAP_MM = 20.0

    const val LENGTH_MIN_MM = 300
    const val LENGTH_MAX_MM = 2400
    const val LENGTH_STEP_MM = 50
    const val DEPTH_MIN_MM = 100
    const val DEPTH_MAX_MM = 500
    const val DEPTH_STEP_MM = 10

    const val MIN_BRACKETS = 2
    const val MAX_BRACKETS = 8

    const val DEFAULT_LENGTH_MM = 1200
    const val DEFAULT_DEPTH_MM = 250
    const val DEFAULT_THICKNESS_MM = 18
    const val DEFAULT_CUSTOM_KG = 20.0

    const val TRIM_MARGIN = 1.1
    const val BRACKET_ARM_GAP_MM = 10.0

    val THICKNESS_OPTIONS = listOf(12, 15, 18, 22, 25, 32)
    val SAG_LIMIT_OPTIONS = listOf(360, 240, 180)
}
