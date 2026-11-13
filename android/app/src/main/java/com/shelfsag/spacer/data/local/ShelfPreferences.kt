package com.shelfsag.spacer.data.local

import android.content.Context
import android.content.SharedPreferences

class ShelfPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun readPlans(): String = prefs.getString(KEY_PLANS, "") ?: ""

    fun writePlans(payload: String) {
        prefs.edit().putString(KEY_PLANS, payload).apply()
    }

    fun isSeeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun readUnits(): String = prefs.getString(KEY_UNITS, "") ?: ""

    fun readSagLimit(fallback: Int): Int = prefs.getInt(KEY_SAG_LIMIT, fallback)

    fun readHardLimit(fallback: Double): Double =
        prefs.getFloat(KEY_HARD_LIMIT, fallback.toFloat()).toDouble()

    fun readStudSpacing(fallback: Double): Double =
        prefs.getFloat(KEY_STUD_SPACING, fallback.toFloat()).toDouble()

    fun writeSettings(units: String, sagLimit: Int, hardLimit: Double, studSpacing: Double) {
        prefs.edit()
            .putString(KEY_UNITS, units)
            .putInt(KEY_SAG_LIMIT, sagLimit)
            .putFloat(KEY_HARD_LIMIT, hardLimit.toFloat())
            .putFloat(KEY_STUD_SPACING, studSpacing.toFloat())
            .apply()
    }

    private companion object {
        const val FILE_NAME = "shelf_sag_spacer"
        const val KEY_PLANS = "plans"
        const val KEY_SEEDED = "seeded"
        const val KEY_UNITS = "units"
        const val KEY_SAG_LIMIT = "sag_limit_k"
        const val KEY_HARD_LIMIT = "hard_limit_mm"
        const val KEY_STUD_SPACING = "stud_spacing_mm"
    }
}
