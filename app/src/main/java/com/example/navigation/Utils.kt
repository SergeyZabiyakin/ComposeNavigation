package com.example.navigation

import android.content.Context
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.navigation.ui.compose.Screen
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

inline fun <reified T : Any> NavGraphBuilder.addSimpleScreen(noinline onClick: () -> Unit) {
    composable<T> {
        Screen(
            text = T::class.simpleName ?: "",
            buttonText = "Go Back",
            onClick = onClick
        )
    }
}

fun NavController.navigateTo(route: MainRouteConfig) {
    if (!popBackStack(route = route, inclusive = false, saveState = true)) {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

fun NavBackStackEntry.isRoute(route: MainRouteConfig): Boolean {
    val name = route::class.qualifiedName ?: return false
    return destination.route?.contains(name) == true
}