package com.example.navigation.data

import com.google.gson.annotations.SerializedName


data class Config(
    val casino: Section,
    val sports: Section,
    val start: Tab
) {
    data class Section(
        val buttons: List<Button>
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

            UNKNOWN("Unknown")
        }
    }

    enum class Tab {
        @SerializedName("Casino")
        CASINO,

        @SerializedName("Sports")
        SPORTS
    }
}