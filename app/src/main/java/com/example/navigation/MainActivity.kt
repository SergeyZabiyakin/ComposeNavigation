package com.example.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.navigation.domain.BottomNavigationManager
import com.example.navigation.domain.NavigationHelper
import com.example.navigation.ui.compose.Screen
import com.example.navigation.ui.compose.Tabs
import com.example.navigation.ui.theme.NavigationTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val manager = BottomNavigationManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        val jsonString = readJSONFromAssets(this, "config.json")
        val config = Gson().fromJson(jsonString, Config::class.java)
        if (config.start == CASINO) {
            manager.setItems(config.casino)
        } else {
            manager.setItems(config.sports)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
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
                ) { padding ->
                    MainContent(
                        modifier = Modifier.padding(padding),
                        manager = manager,
                        finish = { finish() },
                        config = config
                    )
                }
            }
        }
    }

    @Composable
    private fun MainContent(
        manager: BottomNavigationManager,
        modifier: Modifier,
        finish: () -> Unit,
        config: Config
    ) {
        val controller = rememberNavController()
        var selectedTab by remember { mutableStateOf(config.start) }
        val navigationHelper = remember { NavigationHelper(config, controller) }

        fun beforeGoBack() {
            val previous = navigationHelper.previous
            if (previous is Casino && selectedTab != CASINO) {
                manager.setItems(config.casino)
                selectedTab = CASINO
            }
            if (previous is Sports && selectedTab != SPORTS) {
                manager.setItems(config.sports)
                selectedTab = SPORTS
            }
        }

        fun popBackStack() {
            beforeGoBack()
            if (!navigationHelper.popBackStack()) finish()
        }

        LaunchedEffect(Unit) {
            navigationHelper.current.collect { current ->
                manager.selectItem(current.screenButton())
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
                    manager.setItems(config.casino)
                    navigationHelper.navigateToCasino()
                } else {
                    manager.setItems(config.sports)
                    navigationHelper.navigateToSports()
                }
            }
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = controller,
                startDestination = navigationHelper.startRoute
            ) {
                // Casino
                addSimpleScreen<CasinoTop>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                addSimpleScreen<CasinoSlots>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                addSimpleScreen<CasinoSearch>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                addSimpleScreen<CasinoLive>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                // Sports
                addSimpleScreen<SportsTop>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                addSimpleScreen<SportsSport>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                addSimpleScreen<SportsSearch>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                addSimpleScreen<SportsMyBets>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
                // Shared
                addSimpleScreen<Menu>(
                    onClick = { popBackStack() },
                    beforeGoBack = { beforeGoBack() }
                )
            }
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.addSimpleScreen(
    noinline onClick: () -> Unit,
    noinline beforeGoBack: () -> Unit
) {
    composable<T> {
        Screen(
            text = T::class.simpleName ?: "",
            goBack = onClick,
            beforeGoBack = beforeGoBack
        )
    }
}
