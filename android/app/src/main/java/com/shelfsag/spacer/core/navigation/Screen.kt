package com.shelfsag.spacer.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val planId: String?) : Screen
    data class Result(val planId: String) : Screen
    data class Brackets(val planId: String) : Screen
    data class CutList(val planId: String) : Screen
    data object Settings : Screen
}
