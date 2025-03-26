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
import com.example.navigation.data.Config.Section.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Stable
class BottomNavigationManager(
    private val appScope: CoroutineScope
) {
    private val _itemFlow = MutableStateFlow(emptyList<BottomNavigationItem>())
    private val _onItemSelectedFlow = MutableSharedFlow<Button>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val itemFlow: StateFlow<List<BottomNavigationItem>> = _itemFlow
    val onItemSelectedFlow: Flow<Button> = _onItemSelectedFlow

    fun setItems(items: List<Button>, selectedItem: Button) = appScope.launch {
        val newItems = items.map {
            if (it == selectedItem) {
                _onItemSelectedFlow.emit(selectedItem)
            }
            getItem(it, it == selectedItem)
        }
        _itemFlow.emit(newItems)
    }

    fun selectItem(button: Button) = appScope.launch {
        val items = _itemFlow.value
        val newItems = buildList(items.size) {
            items.forEach { item ->
                if (item.type == button) {
                    if (!item.selected) {
                        add(item.copy(selected = true))
                    } else {
                        return@launch
                    }
                } else {
                    add(item.copy(selected = false))
                }
            }
        }
        _itemFlow.emit(newItems)
        _onItemSelectedFlow.emit(button)
    }
}

private fun getItem(item: Button, selected: Boolean): BottomNavigationItem = when (item) {
    Button.TOP -> BottomNavigationItem(
        type = Button.TOP,
        selected = selected,
        iconSelected = Icons.Filled.Place,
        iconUnselected = Icons.Outlined.Place
    )

    Button.SLOTS -> BottomNavigationItem(
        type = Button.SLOTS,
        selected = selected,
        iconSelected = Icons.Filled.Email,
        iconUnselected = Icons.Outlined.Email
    )

    Button.SEARCH -> BottomNavigationItem(
        type = Button.SEARCH,
        selected = selected,
        iconSelected = Icons.Filled.Search,
        iconUnselected = Icons.Outlined.Search
    )

    Button.LIVE -> BottomNavigationItem(
        type = Button.LIVE,
        selected = selected,
        iconSelected = Icons.Filled.Lock,
        iconUnselected = Icons.Outlined.Lock
    )

    Button.MENU -> BottomNavigationItem(
        type = Button.MENU,
        selected = selected,
        iconSelected = Icons.Filled.Menu,
        iconUnselected = Icons.Outlined.Menu
    )

    Button.SPORT -> BottomNavigationItem(
        type = Button.SPORT,
        selected = selected,
        iconSelected = Icons.Filled.ShoppingCart,
        iconUnselected = Icons.Outlined.ShoppingCart
    )

    Button.MY_BETS -> BottomNavigationItem(
        type = Button.MY_BETS,
        selected = selected,
        iconSelected = Icons.Filled.AccountBox,
        iconUnselected = Icons.Outlined.AccountBox
    )

    Button.UNKNOWN -> throw IllegalArgumentException()
}