package com.shelfsag.spacer.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelfsag.spacer.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _finished = MutableStateFlow(false)
    val finished: StateFlow<Boolean> = _finished.asStateFlow()

    private var timer: Job? = null

    init {
        timer = viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            _finished.value = true
        }
    }

    fun pause() {
        timer?.cancel()
        timer = null
    }
}
