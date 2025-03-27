package com.example.navigation.domain

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Stable
import com.example.navigation.data.Config.Button
import com.example.navigation.data.Config.Button.LIVE
import com.example.navigation.data.Config.Button.MENU
import com.example.navigation.data.Config.Button.MY_BETS
import com.example.navigation.data.Config.Button.SEARCH
import com.example.navigation.data.Config.Button.SLOTS
import com.example.navigation.data.Config.Button.SPORT
import com.example.navigation.data.Config.Button.TOP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull

@Stable
class BottomMenuManager {
    private val _state = MutableStateFlow(emptyList<BottomItem>())
    private val _selected = MutableStateFlow<BottomItem?>(null)
    private val _visible = MutableStateFlow(true)

    val state: StateFlow<List<BottomItem>> = _state
    val selected: Flow<BottomItem> = _selected.mapNotNull { it }
    val visible: StateFlow<Boolean> = _visible

    fun setItems(items: List<Button>, selected: Button) {
        val buttons = items.map { mapItem(it, it == selected) }
        _state.value = buttons
        _selected.value = buttons.first { it.selected }
    }

    fun selectItem(button: Button) {
        val items = _state.value
        val newItems = buildList(items.size) {
            items.forEach { item ->
                if (item.type == button) {
                    if (item.selected) return
                    val selected = item.copy(selected = true)
                    add(selected)
                    _selected.value = selected
                } else {
                    add(item.copy(selected = false))
                }
            }
        }
        _state.value = newItems
    }

    fun showMenu() {
        _visible.value = true
    }

    fun hideMenu() {
        _visible.value = false
    }

    private fun mapItem(item: Button, selected: Boolean): BottomItem {
        val icons = iconsMap[item] ?: throw IllegalArgumentException("Unknown Button type")
        return BottomItem(
            type = item,
            selected = selected,
            iconSelected = icons.first,
            iconUnselected = icons.second
        )
    }

    private val iconsMap = hashMapOf(
        TOP to Pair(Filled.Place, Outlined.Place),
        SLOTS to Pair(Filled.Email, Outlined.Email),
        SEARCH to Pair(Filled.Search, Outlined.Search),
        LIVE to Pair(Filled.Lock, Outlined.Lock),
        MENU to Pair(Filled.Menu, Outlined.Menu),
        SPORT to Pair(Filled.ShoppingCart, Outlined.ShoppingCart),
        MY_BETS to Pair(Filled.AccountBox, Outlined.AccountBox),
    )
}