package com.shelfsag.spacer.presentation.app

import androidx.lifecycle.ViewModel
import com.shelfsag.spacer.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash && current != target) {
            backStack.add(current)
        }
        _screen.value = target
    }

    fun popToMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun back(): Boolean {
        if (backStack.isEmpty()) return false
        _screen.value = backStack.removeAt(backStack.lastIndex)
        return true
    }
}
