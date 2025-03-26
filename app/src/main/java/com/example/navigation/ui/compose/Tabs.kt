package com.example.navigation.ui.compose

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.navigation.data.Config
import com.example.navigation.data.Config.Tab.CASINO
import com.example.navigation.data.Config.Tab.SPORTS

@Composable
fun Tabs(
    modifier: Modifier = Modifier,
    startTab: Config.Tab,
    onClick: (Config.Tab) -> Unit
) {
    var selectedTabIndex by remember(startTab) { mutableIntStateOf(startTab.ordinal) }
    val tabs = remember { listOf(CASINO, SPORTS) }
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex
    ) {
        tabs.forEachIndexed { index, item ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = {
                    selectedTabIndex = index
                    onClick.invoke(item)
                },
                text = {
                    Text(text = item.name)
                },
            )
        }
    }
}