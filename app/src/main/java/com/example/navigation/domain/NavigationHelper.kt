package com.example.navigation.domain

import androidx.navigation.NavController
import com.example.navigation.MainRouteConfig
import com.example.navigation.MainRouteConfig.Casino
import com.example.navigation.MainRouteConfig.Sports
import com.example.navigation.data.Config
import com.example.navigation.getStartCasino
import com.example.navigation.getStartRoute
import com.example.navigation.getStartSports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.LinkedList

class NavigationHelper(
    private val config: Config,
    private val controller: NavController
) {
    val startRoute = config.getStartRoute()
    private val stack = LinkedList<MainRouteConfig>().apply { push(startRoute) }
    private var _current = MutableStateFlow(startRoute)

    val current: StateFlow<MainRouteConfig> = _current
    val previous: MainRouteConfig?
        get() = stack.getOrNull(1)

    fun popBackStack(): Boolean {
        if (stack.size <= 1) return false
        if (controller.popBackStack()) {
            stack.pop()
            stack.peek()?.let { _current.value = it }
            return true
        }
        return false
    }

    fun navigate(route: MainRouteConfig) {
        if (stack.peek() == route) return

        val distance = LinkedList<MainRouteConfig>()
        if (controller.popBackStack(route = route, inclusive = false, saveState = true)) {
            while (stack.peek() != route) {
                distance.push(stack.pop())
            }
            controller.popBackStack(route = route, inclusive = true, saveState = true)
            distance.add(stack.pop())
        } else {
            distance.push(route)
        }
        while (distance.isNotEmpty()) {
            val target = distance.pop()
            stack.push(target)
            controller.navigate(target) {
                launchSingleTop = true
                restoreState = true
            }
        }
        _current.value = route
    }

    fun navigateToCasino() {
        stack.forEach { route ->
            if (route is Casino) {
                navigate(route)
                return
            }
        }
        navigate(config.getStartCasino())
    }

    fun navigateToSports() {
        stack.forEach { route ->
            if (route is Sports) {
                navigate(route)
                return
            }
        }
        navigate(config.getStartSports())
    }
}