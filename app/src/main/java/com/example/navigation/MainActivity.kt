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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.MainRouteConfig.CasinoLive
import com.example.navigation.MainRouteConfig.CasinoSearch
import com.example.navigation.MainRouteConfig.CasinoSlots
import com.example.navigation.MainRouteConfig.CasinoTop
import com.example.navigation.MainRouteConfig.Menu
import com.example.navigation.MainRouteConfig.SportsMyBets
import com.example.navigation.MainRouteConfig.SportsSearch
import com.example.navigation.MainRouteConfig.SportsSport
import com.example.navigation.MainRouteConfig.SportsTop
import com.example.navigation.data.Config
import com.example.navigation.data.Config.Section.Button.LIVE
import com.example.navigation.data.Config.Section.Button.MENU
import com.example.navigation.data.Config.Section.Button.MY_BETS
import com.example.navigation.data.Config.Section.Button.SEARCH
import com.example.navigation.data.Config.Section.Button.SLOTS
import com.example.navigation.data.Config.Section.Button.SPORT
import com.example.navigation.data.Config.Section.Button.TOP
import com.example.navigation.data.Config.Section.Button.UNKNOWN
import com.example.navigation.data.Config.Tab.CASINO
import com.example.navigation.data.Config.Tab.SPORTS
import com.example.navigation.domain.BottomNavigationManager
import com.example.navigation.ui.compose.Tabs
import com.example.navigation.ui.theme.NavigationTheme
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MainActivity : ComponentActivity() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val manager = BottomNavigationManager(appScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        val jsonString = readJSONFromAssets(this, "config.json")
        val config = Gson().fromJson(jsonString, Config::class.java)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val items by manager.itemFlow.collectAsState()
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
}

@Composable
private fun MainContent(
    manager: BottomNavigationManager,
    modifier: Modifier,
    finish: () -> Unit,
    config: Config
) {
    val casino = config.casino.buttons
    val sports = config.sports.buttons
    val controller = rememberNavController()
    var tab by remember { mutableStateOf(config.start) }
    var lastCasino by remember { mutableStateOf(TOP) }
    var lastSport by remember { mutableStateOf(TOP) }
    val backStackEntry by controller.currentBackStackEntryAsState()
    val popBackStack = remember { { if (!controller.popBackStack()) finish() } }

    LaunchedEffect(tab) {
        if (tab == CASINO) {
            manager.setItems(casino, lastCasino)
        } else {
            manager.setItems(sports, lastSport)
        }
    }

    LaunchedEffect(backStackEntry) {
        val current = backStackEntry ?: return@LaunchedEffect

        fun checkScreenState(
            screenTab: Config.Tab,
            screenRoute: MainRouteConfig
        ): Boolean {
            if (current.isRoute(screenRoute)) {
                val button = screenRoute.screenButton()
                if (tab == screenTab) {
                    manager.selectItem(button)
                } else {
                    manager.setItems(casino, button)
                    tab = screenTab
                }
                lastCasino = button
                return true
            }
            return false
        }
        when {
            // Casino
            checkScreenState(CASINO, CasinoTop) -> {}
            checkScreenState(CASINO, CasinoSlots) -> {}
            checkScreenState(CASINO, CasinoSearch) -> {}
            checkScreenState(CASINO, CasinoLive) -> {}
            // Sports
            checkScreenState(SPORTS, SportsTop) -> {}
            checkScreenState(SPORTS, SportsSport) -> {}
            checkScreenState(SPORTS, SportsSearch) -> {}
            checkScreenState(SPORTS, SportsMyBets) -> {}
            // Shared
            current.isRoute(Menu) -> {
                if (tab == CASINO) {
                    lastCasino = MENU
                } else {
                    lastSport = MENU
                }
                manager.selectItem(MENU)
            }
        }
    }

    LaunchedEffect(Unit) {
        manager.onItemSelectedFlow.collect { selected ->
            when (selected) {
                TOP -> if (tab == CASINO) CasinoTop else SportsTop
                SEARCH -> if (tab == CASINO) CasinoSearch else SportsSearch
                SLOTS -> CasinoSlots
                LIVE -> CasinoLive
                MENU -> Menu
                SPORT -> SportsSport
                MY_BETS -> SportsMyBets
                UNKNOWN -> null
            }?.let { destination ->
                controller.navigateTo(destination)
            }
        }
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Tabs(startTab = tab) {
            tab = it
        }
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = controller,
            startDestination = if (config.start == CASINO) {
                config.getStartCasino()
            } else {
                config.getStartSports()
            }
        ) {
            // Casino
            addSimpleScreen<CasinoTop>(onClick = popBackStack)
            addSimpleScreen<CasinoSlots>(onClick = popBackStack)
            addSimpleScreen<CasinoSearch>(onClick = popBackStack)
            addSimpleScreen<CasinoLive>(onClick = popBackStack)
            // Sports
            addSimpleScreen<SportsTop>(onClick = popBackStack)
            addSimpleScreen<SportsSport>(onClick = popBackStack)
            addSimpleScreen<SportsSearch>(onClick = popBackStack)
            addSimpleScreen<SportsMyBets>(onClick = popBackStack)
            // Shared
            addSimpleScreen<Menu>(onClick = popBackStack)
        }
    }
}
