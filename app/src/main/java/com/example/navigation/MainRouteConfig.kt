package com.example.navigation

import com.example.navigation.data.Config
import com.example.navigation.data.Config.Section.Button
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRouteConfig {

    @Serializable
    data object CasinoTop : MainRouteConfig

    @Serializable
    data object CasinoSlots : MainRouteConfig

    @Serializable
    data object CasinoSearch : MainRouteConfig

    @Serializable
    data object CasinoLive : MainRouteConfig

    @Serializable
    data object SportsTop : MainRouteConfig

    @Serializable
    data object SportsSport : MainRouteConfig

    @Serializable
    data object SportsSearch : MainRouteConfig

    @Serializable
    data object SportsMyBets : MainRouteConfig

    @Serializable
    data object Menu : MainRouteConfig
}

fun MainRouteConfig.screenButton(): Button {
    return when (this) {
        MainRouteConfig.CasinoLive -> Button.LIVE
        MainRouteConfig.CasinoSearch -> Button.SEARCH
        MainRouteConfig.CasinoSlots -> Button.SLOTS
        MainRouteConfig.CasinoTop -> Button.TOP
        MainRouteConfig.Menu -> Button.MENU
        MainRouteConfig.SportsMyBets -> Button.MY_BETS
        MainRouteConfig.SportsSearch -> Button.SEARCH
        MainRouteConfig.SportsSport -> Button.SPORT
        MainRouteConfig.SportsTop -> Button.TOP
    }
}

fun Config.getStartCasino(): MainRouteConfig {
    return when (casino.buttons.first()) {
        Button.TOP -> MainRouteConfig.CasinoTop
        Button.SLOTS -> MainRouteConfig.CasinoSlots
        Button.SEARCH -> MainRouteConfig.CasinoSearch
        Button.LIVE -> MainRouteConfig.CasinoLive
        Button.MENU -> MainRouteConfig.Menu
        else -> throw IllegalArgumentException("Unknown Casino type")
    }
}

fun Config.getStartSports(): MainRouteConfig {
    return when (sports.buttons.first()) {
        Button.TOP -> MainRouteConfig.SportsTop
        Button.SPORT -> MainRouteConfig.SportsSport
        Button.SEARCH -> MainRouteConfig.SportsSearch
        Button.MY_BETS -> MainRouteConfig.SportsMyBets
        Button.MENU -> MainRouteConfig.Menu
        else -> throw IllegalArgumentException("Unknown Sports type")
    }
}