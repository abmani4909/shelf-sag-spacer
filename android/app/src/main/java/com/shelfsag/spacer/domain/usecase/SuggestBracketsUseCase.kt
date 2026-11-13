package com.shelfsag.spacer.domain.usecase

import com.shelfsag.spacer.core.config.GameConfig
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class SuggestBracketsUseCase {

    fun maxSpan(
        depthMm: Double,
        thicknessMm: Double,
        modulusMpa: Double,
        kgPerMetre: Double,
        limitK: Int,
        hardLimitMm: Double
    ): Double {
        val ceiling = GameConfig.LENGTH_MAX_MM.toDouble()
        if (kgPerMetre <= 0.0 || depthMm <= 0.0 || thicknessMm <= 0.0) return ceiling
        val load = kgPerMetre * GameConfig.GRAVITY / 1000.0
        val inertia = depthMm * thicknessMm.pow(3) / 12.0
        val stiffness = 384.0 * modulusMpa * inertia
        val byRatio = (stiffness / (5.0 * load * max(limitK, 1))).pow(1.0 / 3.0)
        val byHard = (stiffness * hardLimitMm / (5.0 * load)).pow(0.25)
        return min(min(byRatio, byHard), ceiling)
    }

    fun positions(lengthMm: Double, maxSpanMm: Double): List<Double> {
        if (lengthMm <= 0.0) return emptyList()
        val inset = min(75.0, lengthMm * 0.08)
        val usable = max(lengthMm - 2.0 * inset, 1.0)
        val rawGaps = ceil(usable / max(maxSpanMm, 1.0)).toInt()
        val count = (max(rawGaps, 1) + 1).coerceIn(GameConfig.MIN_BRACKETS, GameConfig.MAX_BRACKETS)
        val gaps = count - 1
        val spacing = usable / gaps
        return List(count) { inset + it * spacing }
    }

    fun evenPositions(lengthMm: Double, count: Int): List<Double> {
        val safeCount = count.coerceIn(GameConfig.MIN_BRACKETS, GameConfig.MAX_BRACKETS)
        val inset = min(75.0, lengthMm * 0.08)
        val usable = max(lengthMm - 2.0 * inset, 1.0)
        val spacing = usable / (safeCount - 1)
        return List(safeCount) { inset + it * spacing }
    }

    fun studMarks(lengthMm: Double, studSpacingMm: Double): List<Double> {
        if (studSpacingMm <= 0.0) return emptyList()
        val marks = mutableListOf<Double>()
        var position = studSpacingMm
        while (position < lengthMm) {
            marks.add(position)
            position += studSpacingMm
        }
        return marks
    }
}
