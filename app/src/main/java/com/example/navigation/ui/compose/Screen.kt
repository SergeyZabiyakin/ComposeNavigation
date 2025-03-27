package com.example.navigation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class ScreenData(val i: Int = 0)

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    text: String = "",
    goBack: () -> Unit = {},
    openBottomSheet: () -> Unit = {}
) {
    BackInterceptor(goBack)
    val controller = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = controller,
        startDestination = ScreenData()
    ) {
        composable<ScreenData> { backStackEntry ->
            val data: ScreenData = backStackEntry.toRoute()
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = text + " stack " + data.i)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    controller.navigate(ScreenData(data.i + 1))
                }) {
                    Text("Add stack")
                }
                Button(onClick = openBottomSheet) {
                    Text("Open bottom sheet")
                }
                Button(onClick = {
                    if (!controller.popBackStack()) {
                        goBack()
                    }
                }) {
                    Text(text = "Go Back")
                }
            }
        }
    }
}