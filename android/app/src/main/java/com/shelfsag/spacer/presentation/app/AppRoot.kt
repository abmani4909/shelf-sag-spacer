package com.shelfsag.spacer.presentation.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shelfsag.spacer.core.di.AppViewModelFactory
import com.shelfsag.spacer.core.navigation.Screen
import com.shelfsag.spacer.presentation.brackets.BracketLayoutScreen
import com.shelfsag.spacer.presentation.cutlist.CutListScreen
import com.shelfsag.spacer.presentation.game.GameScreen
import com.shelfsag.spacer.presentation.gameover.GameOverScreen
import com.shelfsag.spacer.presentation.menu.MenuScreen
import com.shelfsag.spacer.presentation.settings.SettingsScreen
import com.shelfsag.spacer.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler {
        if (!appViewModel.back()) {
            (context as? Activity)?.finish()
        }
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            (fadeIn(tween(220)) + slideInVertically(tween(220)) { full -> full / 14 })
                .togetherWith(fadeOut(tween(180)))
        },
        label = "appRoot",
        modifier = Modifier.fillMaxSize()
    ) { target ->
        when (target) {
            Screen.Splash -> SplashScreen(
                onFinished = { appViewModel.navigate(Screen.Menu) }
            )

            Screen.Menu -> MenuScreen(
                onStartPlanning = { appViewModel.navigate(Screen.Game(null)) },
                onOpenPlan = { planId -> appViewModel.navigate(Screen.Game(planId)) },
                onOpenSettings = { appViewModel.navigate(Screen.Settings) }
            )

            is Screen.Game -> GameScreen(
                planId = target.planId,
                onBack = { appViewModel.back() },
                onResult = { planId -> appViewModel.navigate(Screen.Result(planId)) }
            )

            is Screen.Result -> GameOverScreen(
                planId = target.planId,
                onBrackets = { appViewModel.navigate(Screen.Brackets(target.planId)) },
                onPlanAgain = { appViewModel.navigate(Screen.Game(target.planId)) },
                onMenu = { appViewModel.popToMenu() }
            )

            is Screen.Brackets -> BracketLayoutScreen(
                planId = target.planId,
                onBack = { appViewModel.back() },
                onCutList = { appViewModel.navigate(Screen.CutList(target.planId)) }
            )

            is Screen.CutList -> CutListScreen(
                planId = target.planId,
                onBack = { appViewModel.back() },
                onMenu = { appViewModel.popToMenu() }
            )

            Screen.Settings -> SettingsScreen(
                onBack = { appViewModel.back() },
                onMenu = { appViewModel.popToMenu() }
            )
        }
    }
}
