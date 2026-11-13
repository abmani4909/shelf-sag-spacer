package com.shelfsag.spacer.domain.usecase

import com.shelfsag.spacer.core.config.GameConfig
import kotlin.math.pow

class CalculateSagUseCase {

    operator fun invoke(
        spanMm: Double,
        depthMm: Double,
        thicknessMm: Double,
        modulusMpa: Double,
        kgPerMetre: Double
    ): Double {
        if (spanMm <= 0.0 || depthMm <= 0.0 || thicknessMm <= 0.0 || modulusMpa <= 0.0) return 0.0
        if (kgPerMetre <= 0.0) return 0.0
        val load = kgPerMetre * GameConfig.GRAVITY / 1000.0
        val inertia = depthMm * thicknessMm.pow(3) / 12.0
        if (inertia <= 0.0) return 0.0
        return 5.0 * load * spanMm.pow(4) / (384.0 * modulusMpa * inertia)
    }
}
