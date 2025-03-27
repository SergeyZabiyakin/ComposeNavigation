package com.example.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.MainRouteConfig.Casino
import com.example.navigation.MainRouteConfig.CasinoLive
import com.example.navigation.MainRouteConfig.CasinoSearch
import com.example.navigation.MainRouteConfig.CasinoSlots
import com.example.navigation.MainRouteConfig.CasinoTop
import com.example.navigation.MainRouteConfig.Menu
import com.example.navigation.MainRouteConfig.Sports
import com.example.navigation.MainRouteConfig.SportsMyBets
import com.example.navigation.MainRouteConfig.SportsSearch
import com.example.navigation.MainRouteConfig.SportsSport
import com.example.navigation.MainRouteConfig.SportsTop
import com.example.navigation.data.Config
import com.example.navigation.data.Config.Tab.CASINO
import com.example.navigation.data.Config.Tab.SPORTS
import com.example.navigation.domain.BottomMenuManager
import com.example.navigation.domain.RootNavigationHelper
import com.example.navigation.ui.compose.Screen
import com.example.navigation.ui.compose.Tabs
import com.example.navigation.ui.theme.NavigationTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val manager = BottomMenuManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        val jsonString = readJSONFromAssets(this, "config.json")
        val config = Gson().fromJson(jsonString, Config::class.java)
        if (config.start == CASINO) {
            manager.setItems(config.casino, config.casino.first())
        } else {
            manager.setItems(config.sports, config.sports.first())
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val visible by manager.visible.collectAsState()
                        AnimatedVisibility(
                            visible = visible,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            val items by manager.state.collectAsState()
                            NavigationBar {
                                items.forEach { item ->
                                    NavigationBarItem(
                                        selected = item.selected,
                                        label = {
                                            Text(text = item.type.text)
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.type.text
                                            )
                                        },
                                        onClick = {
                                            manager.selectItem(item.type)
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    MainContent(
                        modifier = Modifier.padding(padding),
                        finish = { finish() },
                        config = config
                    )
                }
            }
        }
    }

    @Composable
    private fun MainContent(
        modifier: Modifier,
        finish: () -> Unit,
        config: Config
    ) {
        val controller = rememberNavController()
        var selectedTab by remember { mutableStateOf(config.start) }
        val navigationHelper = remember { RootNavigationHelper(config, controller) }

        fun popBackStack() {
            val previous = navigationHelper.previous
            if (previous is Casino && selectedTab != CASINO) {
                manager.setItems(config.casino, previous.screenButton())
                selectedTab = CASINO
            }
            if (previous is Sports && selectedTab != SPORTS) {
                manager.setItems(config.sports, previous.screenButton())
                selectedTab = SPORTS
            }
            if (!navigationHelper.popBackStack()) finish()
        }

        LaunchedEffect(Unit) {
            navigationHelper.current.collect { current ->
                manager.selectItem(current.screenButton())
                if (current.bottomMenuVisible()) {
                    manager.showMenu()
                } else {
                    manager.hideMenu()
                }
            }
        }
        LaunchedEffect(Unit) {
            manager.selected.collect { selected ->
                if (selectedTab == CASINO) {
                    casinoRouteMap[selected.type]
                } else {
                    sportsRouteMap[selected.type]
                }?.let { destination ->
                    navigationHelper.navigate(destination)
                }
            }
        }
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Tabs(selectedTab = selectedTab) {
                selectedTab = it
                if (it == CASINO) {
                    val lastCasino = navigationHelper.lastCasino()
                    manager.setItems(config.casino, lastCasino.screenButton())
                    navigationHelper.navigate(lastCasino)
                } else {
                    val lastSports = navigationHelper.lastSports()
                    manager.setItems(config.sports, lastSports.screenButton())
                    navigationHelper.navigate(lastSports)
                }
            }
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = controller,
                startDestination = navigationHelper.startRoute
            ) {
                // Casino
                addSimpleScreen<CasinoTop>(goBack = { popBackStack() })
                addSimpleScreen<CasinoSlots>(goBack = { popBackStack() })
                addSimpleScreen<CasinoSearch>(goBack = { popBackStack() })
                addSimpleScreen<CasinoLive>(goBack = { popBackStack() })
                // Sports
                addSimpleScreen<SportsTop>(goBack = { popBackStack() })
                addSimpleScreen<SportsSport>(goBack = { popBackStack() })
                addSimpleScreen<SportsSearch>(goBack = { popBackStack() })
                addSimpleScreen<SportsMyBets>(goBack = { popBackStack() })
                // Shared
                addSimpleScreen<Menu>(goBack = { popBackStack() })
            }
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.addSimpleScreen(noinline goBack: () -> Unit) {
    composable<T> {
        Screen(
            text = T::class.simpleName ?: "",
            goBack = goBack,
        )
    }
}
