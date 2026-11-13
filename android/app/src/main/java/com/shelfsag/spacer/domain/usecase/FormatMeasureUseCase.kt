package com.shelfsag.spacer.domain.usecase

import com.shelfsag.spacer.domain.model.UnitSystem
import kotlin.math.abs
import kotlin.math.roundToInt

class FormatMeasureUseCase {

    fun length(mm: Double, units: UnitSystem): String =
        if (units == UnitSystem.METRIC) mm.roundToInt().toString() else oneDecimal(mm / 25.4)

    fun sag(mm: Double, units: UnitSystem): String =
        if (units == UnitSystem.METRIC) oneDecimal(mm) else sixteenths(mm / 25.4)

    fun load(kgPerMetre: Double, units: UnitSystem): String =
        if (units == UnitSystem.METRIC) oneDecimal(kgPerMetre) else oneDecimal(kgPerMetre * 0.672)

    fun metres(mm: Double): String = oneDecimal(mm / 1000.0)

    fun oneDecimal(value: Double): String {
        val tenths = (value * 10.0).roundToInt()
        return (tenths / 10.0).toString()
    }

    private fun sixteenths(inches: Double): String {
        val total = (abs(inches) * 16.0).roundToInt()
        val whole = total / 16
        var numerator = total % 16
        if (numerator == 0) return whole.toString()
        var denominator = 16
        while (numerator % 2 == 0 && denominator % 2 == 0) {
            numerator /= 2
            denominator /= 2
        }
        val fraction = numerator.toString() + "/" + denominator.toString()
        return if (whole == 0) fraction else whole.toString() + " " + fraction
    }
}
