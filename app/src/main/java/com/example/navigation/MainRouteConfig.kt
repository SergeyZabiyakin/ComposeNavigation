package com.example.navigation

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
import com.example.navigation.data.Config.Button
import com.example.navigation.data.Config.Button.LIVE
import com.example.navigation.data.Config.Button.MENU
import com.example.navigation.data.Config.Button.MY_BETS
import com.example.navigation.data.Config.Button.SEARCH
import com.example.navigation.data.Config.Button.SLOTS
import com.example.navigation.data.Config.Button.SPORT
import com.example.navigation.data.Config.Button.TOP
import com.example.navigation.data.Config.Tab.CASINO
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRouteConfig {
    fun saveState(): Boolean
    fun screenButton(): Button
    fun bottomMenuVisible() : Boolean

    @Serializable
    sealed interface Casino : MainRouteConfig

    @Serializable
    sealed interface Sports : MainRouteConfig

    @Serializable
    data object CasinoTop : Casino {
        override fun saveState() = true
        override fun screenButton() = TOP
        override fun bottomMenuVisible() = true
    }

    @Serializable
    data object CasinoSlots : Casino {
        override fun saveState() = true
        override fun screenButton() = SLOTS
        override fun bottomMenuVisible() = true
    }

    @Serializable
    data object CasinoSearch : Casino {
        override fun saveState() = false
        override fun screenButton() = SEARCH
        override fun bottomMenuVisible() = false
    }

    @Serializable
    data object CasinoLive : Casino {
        override fun saveState() = true
        override fun screenButton() = LIVE
        override fun bottomMenuVisible() = true
    }

    @Serializable
    data object SportsTop : Sports {
        override fun saveState() = true
        override fun screenButton() = TOP
        override fun bottomMenuVisible() = true
    }

    @Serializable
    data object SportsSport : Sports {
        override fun saveState() = true
        override fun screenButton() = SPORT
        override fun bottomMenuVisible() = true
    }

    @Serializable
    data object SportsSearch : Sports {
        override fun saveState() = false
        override fun screenButton() = SEARCH
        override fun bottomMenuVisible() = false
    }

    @Serializable
    data object SportsMyBets : Sports {
        override fun saveState() = true
        override fun screenButton() = MY_BETS
        override fun bottomMenuVisible() = true
    }

    @Serializable
    data object Menu : MainRouteConfig {
        override fun saveState() = false
        override fun screenButton() = MENU
        override fun bottomMenuVisible() = true
    }
}

val casinoRouteMap = hashMapOf(
    TOP to CasinoTop,
    SLOTS to CasinoSlots,
    SEARCH to CasinoSearch,
    LIVE to CasinoLive,
    MENU to Menu,
)

val sportsRouteMap = hashMapOf(
    TOP to SportsTop,
    SPORT to SportsSport,
    SEARCH to SportsSearch,
    MY_BETS to SportsMyBets,
    MENU to Menu,
)

fun Config.getStartRoute(): MainRouteConfig {
    return if (start == CASINO) {
        getStartCasino()
    } else {
        getStartSports()
    }
}

fun Config.getStartCasino(): MainRouteConfig {
    return casinoRouteMap[casino.first()] ?: throw IllegalArgumentException("Unknown Casino type")
}

fun Config.getStartSports(): MainRouteConfig {
    return sportsRouteMap[sports.first()] ?: throw IllegalArgumentException("Unknown Sports type")
}