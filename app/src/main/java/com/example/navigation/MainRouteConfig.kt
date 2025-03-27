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
    fun screenButton(): Button

    @Serializable
    sealed interface Casino : MainRouteConfig

    @Serializable
    sealed interface Sports : MainRouteConfig

    @Serializable
    data object CasinoTop : Casino {
        override fun screenButton() = TOP
    }

    @Serializable
    data object CasinoSlots : Casino {
        override fun screenButton() = SLOTS
    }

    @Serializable
    data object CasinoSearch : Casino {
        override fun screenButton() = SEARCH
    }

    @Serializable
    data object CasinoLive : Casino {
        override fun screenButton() = LIVE
    }

    @Serializable
    data object SportsTop : Sports {
        override fun screenButton() = TOP
    }

    @Serializable
    data object SportsSport : Sports {
        override fun screenButton() = SPORT
    }

    @Serializable
    data object SportsSearch : Sports {
        override fun screenButton() = SEARCH
    }

    @Serializable
    data object SportsMyBets : Sports {
        override fun screenButton() = MY_BETS
    }

    @Serializable
    data object Menu : MainRouteConfig {
        override fun screenButton() = MENU
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