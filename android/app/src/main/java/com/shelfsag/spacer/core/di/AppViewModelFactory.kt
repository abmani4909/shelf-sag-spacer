package com.shelfsag.spacer.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.shelfsag.spacer.presentation.app.AppViewModel
import com.shelfsag.spacer.presentation.brackets.BracketLayoutViewModel
import com.shelfsag.spacer.presentation.cutlist.CutListViewModel
import com.shelfsag.spacer.presentation.game.GameViewModel
import com.shelfsag.spacer.presentation.gameover.GameOverViewModel
import com.shelfsag.spacer.presentation.menu.MenuViewModel
import com.shelfsag.spacer.presentation.settings.SettingsViewModel
import com.shelfsag.spacer.presentation.splash.SplashViewModel

class AppViewModelFactory(private val planId: String? = null) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val plans = ServiceLocator.planRepository
        val settings = ServiceLocator.settingsRepository
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(plans, settings)
            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(planId, plans, settings)
            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(planId, plans, settings)
            modelClass.isAssignableFrom(BracketLayoutViewModel::class.java) ->
                BracketLayoutViewModel(planId, plans, settings)
            modelClass.isAssignableFrom(CutListViewModel::class.java) ->
                CutListViewModel(planId, plans, settings)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(settings)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        create(modelClass)
}
