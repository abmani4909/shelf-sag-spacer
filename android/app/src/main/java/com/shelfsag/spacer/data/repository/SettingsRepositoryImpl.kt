package com.shelfsag.spacer.data.repository

import com.shelfsag.spacer.core.config.GameConfig
import com.shelfsag.spacer.data.local.ShelfPreferences
import com.shelfsag.spacer.domain.model.ShelfSettings
import com.shelfsag.spacer.domain.model.UnitSystem
import com.shelfsag.spacer.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(private val preferences: ShelfPreferences) : SettingsRepository {

    override suspend fun settings(): ShelfSettings = withContext(Dispatchers.IO) {
        ShelfSettings(
            units = UnitSystem.fromId(preferences.readUnits()),
            sagLimitK = preferences.readSagLimit(GameConfig.DEFAULT_SAG_LIMIT_K),
            hardLimitMm = preferences.readHardLimit(GameConfig.DEFAULT_HARD_LIMIT_MM),
            studSpacingMm = preferences.readStudSpacing(GameConfig.STUD_SPACING_MM)
        )
    }

    override suspend fun update(settings: ShelfSettings) {
        withContext(Dispatchers.IO) {
            preferences.writeSettings(
                units = settings.units.name,
                sagLimit = settings.sagLimitK,
                hardLimit = settings.hardLimitMm,
                studSpacing = settings.studSpacingMm
            )
        }
    }
}
