package com.example.navigation.domain

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
class BottomSheetManager {
    private val _state = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state

    fun show() {
        _state.value = true
    }

    fun hide() {
        _state.value = false
    }
}