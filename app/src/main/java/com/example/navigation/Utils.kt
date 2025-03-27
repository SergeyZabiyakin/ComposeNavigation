package com.example.navigation

import android.content.Context
import androidx.navigation.NavBackStackEntry
import java.io.BufferedReader
import java.io.InputStreamReader

fun readJSONFromAssets(context: Context, path: String): String {
    try {
        val file = context.assets.open(path)
        val bufferedReader = BufferedReader(InputStreamReader(file))
        val stringBuilder = StringBuilder()
        bufferedReader.useLines { lines ->
            lines.forEach {
                stringBuilder.append(it)
            }
        }
        val jsonString = stringBuilder.toString()
        return jsonString
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

fun NavBackStackEntry.isRoute(route: MainRouteConfig): Boolean {
    val name = route::class.qualifiedName ?: return false
    return destination.route?.contains(name) == true
}