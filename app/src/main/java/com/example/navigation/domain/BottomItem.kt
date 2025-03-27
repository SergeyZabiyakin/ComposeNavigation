package com.example.navigation.domain

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.navigation.data.Config.Button

data class BottomItem(
    val type: Button,
    val selected: Boolean,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
) {
    val icon: ImageVector
        get() = if (selected) iconSelected else iconUnselected
}
