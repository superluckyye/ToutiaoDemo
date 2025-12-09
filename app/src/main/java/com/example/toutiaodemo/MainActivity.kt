package com.example.toutiaodemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "首页", Icons.Default.Home)
    object My : Screen("my", "我的", Icons.Default.Person)
}

val items = listOf(Screen.Home, Screen.My)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    var selectedItem by remember { mutableStateOf(0) }
    val currentScreen = items[selectedItem]

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) { // 使用 Material 3 的 NavigationBar
                items.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            is Screen.Home -> HomeScreen(Modifier.padding(innerPadding))
            is Screen.My -> MyScreen(Modifier.padding(innerPadding))
        }
    }
}