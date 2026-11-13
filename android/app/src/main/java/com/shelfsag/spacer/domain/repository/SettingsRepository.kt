package com.shelfsag.spacer.domain.repository

import com.shelfsag.spacer.domain.model.ShelfSettings

interface SettingsRepository {
    suspend fun settings(): ShelfSettings

    suspend fun update(settings: ShelfSettings)
}
