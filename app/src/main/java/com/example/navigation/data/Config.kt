package com.example.navigation.data

import com.google.gson.annotations.SerializedName

data class Config(
    val casino: List<Button>,
    val sports: List<Button>,
    val start: Tab
) {

    enum class Button(val text: String) {
        @SerializedName("Top")
        TOP("Top"),

        @SerializedName("Slots")
        SLOTS("Slots"),

        @SerializedName("Search")
        SEARCH("Search"),

        @SerializedName("Live")
        LIVE("Live"),

        @SerializedName("Menu")
        MENU("Menu"),

        @SerializedName("Sport")
        SPORT("Sport"),

        @SerializedName("My bets")
        MY_BETS("My bets"),
    }

    enum class Tab {
        @SerializedName("Casino")
        CASINO,

        @SerializedName("Sports")
        SPORTS
    }
}