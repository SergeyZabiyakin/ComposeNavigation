package com.example.navigation.domain

import androidx.compose.material.icons.Icons
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
class BottomNavigationManager {
    private val _state = MutableStateFlow(emptyList<BottomNavigationItem>())
    val state: StateFlow<List<BottomNavigationItem>> = _state
    val selected: Flow<BottomNavigationItem> = _state.mapNotNull { list ->
        list.firstOrNull { it.selected }
    }

    fun setItems(items: List<Button>) {
        _state.value = items.mapNotNull { itemsMap[it] }
    }

    fun selectItem(button: Button) {
        val items = _state.value
        val newItems = buildList(items.size) {
            items.forEach { item ->
                if (item.type == button) {
                    if (!item.selected) {
                        add(item.copy(selected = true))
                    } else {
                        return
                    }
                } else {
                    add(item.copy(selected = false))
                }
            }
        }
        _state.value = newItems
    }

    private val itemsMap = hashMapOf(
        TOP to BottomNavigationItem(
            type = TOP,
            selected = false,
            iconSelected = Icons.Filled.Place,
            iconUnselected = Icons.Outlined.Place
        ),

        SLOTS to BottomNavigationItem(
            type = SLOTS,
            selected = false,
            iconSelected = Icons.Filled.Email,
            iconUnselected = Icons.Outlined.Email
        ),

        SEARCH to BottomNavigationItem(
            type = SEARCH,
            selected = false,
            iconSelected = Icons.Filled.Search,
            iconUnselected = Icons.Outlined.Search
        ),

        LIVE to BottomNavigationItem(
            type = LIVE,
            selected = false,
            iconSelected = Icons.Filled.Lock,
            iconUnselected = Icons.Outlined.Lock
        ),

        MENU to BottomNavigationItem(
            type = MENU,
            selected = false,
            iconSelected = Icons.Filled.Menu,
            iconUnselected = Icons.Outlined.Menu
        ),

        SPORT to BottomNavigationItem(
            type = SPORT,
            selected = false,
            iconSelected = Icons.Filled.ShoppingCart,
            iconUnselected = Icons.Outlined.ShoppingCart
        ),

        MY_BETS to BottomNavigationItem(
            type = MY_BETS,
            selected = false,
            iconSelected = Icons.Filled.AccountBox,
            iconUnselected = Icons.Outlined.AccountBox
        )
    )
}