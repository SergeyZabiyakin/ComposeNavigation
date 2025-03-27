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
import com.example.navigation.domain.BottomSheetManager
import com.example.navigation.domain.RootNavigationHelper
import com.example.navigation.ui.compose.BottomSheet
import com.example.navigation.ui.compose.Screen
import com.example.navigation.ui.compose.Tabs
import com.example.navigation.ui.theme.NavigationTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val menuManager = BottomMenuManager()
    private val bottomSheetManager = BottomSheetManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        val jsonString = readJSONFromAssets(this, "config.json")
        val config = Gson().fromJson(jsonString, Config::class.java)
        if (config.start == CASINO) {
            menuManager.setItems(config.casino, config.casino.first())
        } else {
            menuManager.setItems(config.sports, config.sports.first())
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                val bottomSheet by bottomSheetManager.state.collectAsState()
                if (bottomSheet) {
                    BottomSheet(onDismissRequest = { bottomSheetManager.hide() })
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val visible by menuManager.visible.collectAsState()
                        AnimatedVisibility(
                            visible = visible,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            val items by menuManager.state.collectAsState()
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
                                            menuManager.selectItem(item.type)
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    MainContent(
                        modifier = Modifier.padding(padding),
                        config = config
                    )
                }
            }
        }
    }

    @Composable
    private fun MainContent(
        modifier: Modifier = Modifier,
        config: Config
    ) {
        val controller = rememberNavController()
        var selectedTab by remember { mutableStateOf(config.start) }
        val navigationHelper = remember { RootNavigationHelper(config, controller) }

        fun popBackStack() {
            val previous = navigationHelper.previous
            if (previous is Casino && selectedTab != CASINO) {
                menuManager.setItems(config.casino, previous.screenButton())
                selectedTab = CASINO
            }
            if (previous is Sports && selectedTab != SPORTS) {
                menuManager.setItems(config.sports, previous.screenButton())
                selectedTab = SPORTS
            }
            if (!navigationHelper.popBackStack()) finish()
        }

        LaunchedEffect(Unit) {
            navigationHelper.current.collect { current ->
                menuManager.selectItem(current.screenButton())
                if (current.bottomMenuVisible()) {
                    menuManager.showMenu()
                } else {
                    menuManager.hideMenu()
                }
            }
        }
        LaunchedEffect(Unit) {
            menuManager.selected.collect { selected ->
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
                    menuManager.setItems(config.casino, lastCasino.screenButton())
                    navigationHelper.navigate(lastCasino)
                } else {
                    val lastSports = navigationHelper.lastSports()
                    menuManager.setItems(config.sports, lastSports.screenButton())
                    navigationHelper.navigate(lastSports)
                }
            }
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = controller,
                startDestination = navigationHelper.startRoute
            ) {
                // Casino
                addSimpleScreen<CasinoTop>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                addSimpleScreen<CasinoSlots>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                addSimpleScreen<CasinoSearch>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                addSimpleScreen<CasinoLive>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                // Sports
                addSimpleScreen<SportsTop>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                addSimpleScreen<SportsSport>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                addSimpleScreen<SportsSearch>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                addSimpleScreen<SportsMyBets>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
                // Shared
                addSimpleScreen<Menu>(
                    goBack = { popBackStack() },
                    openBottomSheet = bottomSheetManager::show
                )
            }
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.addSimpleScreen(
    noinline goBack: () -> Unit,
    noinline openBottomSheet: () -> Unit,
) {
    composable<T> {
        Screen(
            text = T::class.simpleName ?: "",
            goBack = goBack,
            openBottomSheet = openBottomSheet,
        )
    }
}
