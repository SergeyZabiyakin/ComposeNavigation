package com.example.navigation.ui.compose

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.navigation.data.Config.Tab
import com.example.navigation.data.Config.Tab.CASINO
import com.example.navigation.data.Config.Tab.SPORTS

@Composable
fun Tabs(
    modifier: Modifier = Modifier,
    selectedTab: Tab,
    onClick: (Tab) -> Unit
) {
    val tabs = remember { listOf(CASINO, SPORTS) }
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTab.ordinal
    ) {
        tabs.forEach { item ->
            Tab(
                selected = item == selectedTab,
                onClick = {
                    if (selectedTab != item){
                        onClick.invoke(item)
                    }
                },
                text = {
                    Text(text = item.name)
                },
            )
        }
    }
}