package com.shelfsag.spacer.core.di

import android.content.Context
import com.shelfsag.spacer.data.local.ShelfPreferences
import com.shelfsag.spacer.data.repository.PlanRepositoryImpl
import com.shelfsag.spacer.data.repository.SettingsRepositoryImpl
import com.shelfsag.spacer.domain.repository.PlanRepository
import com.shelfsag.spacer.domain.repository.SettingsRepository

object ServiceLocator {

    private var plans: PlanRepository? = null
    private var settings: SettingsRepository? = null

    fun init(context: Context) {
        if (plans != null && settings != null) return
        val preferences = ShelfPreferences(context.applicationContext)
        plans = PlanRepositoryImpl(preferences)
        settings = SettingsRepositoryImpl(preferences)
    }

    val planRepository: PlanRepository
        get() = requireNotNull(plans) { "ServiceLocator not initialised" }

    val settingsRepository: SettingsRepository
        get() = requireNotNull(settings) { "ServiceLocator not initialised" }
}
