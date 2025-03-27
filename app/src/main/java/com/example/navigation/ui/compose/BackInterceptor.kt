package com.example.navigation.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun BackInterceptor(goBack: () -> Unit) {
    BackHandler(enabled = true) {
        goBack()
    }
}