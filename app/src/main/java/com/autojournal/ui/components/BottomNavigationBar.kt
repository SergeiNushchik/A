package com.autojournal.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    NavItem("Главная", Icons.Default.Home, "dashboard"),
    NavItem("История", Icons.Default.List, "history"),
    NavItem("Ремонт", Icons.Default.Build, "repairs"),
    NavItem("Статистика", Icons.Default.BarChart, "statistics")
)

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String, String?) -> Unit,
    carId: String?
) {
    NavigationBar {
        navItems.forEach { item ->
            val isSelected = when (item.route) {
                "dashboard" -> currentRoute == "dashboard"
                "history" -> currentRoute.startsWith("history/")
                "repairs" -> currentRoute.startsWith("repairs/")
                "statistics" -> currentRoute.startsWith("statistics/")
                else -> false
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = {
                    if (item.route == "dashboard") {
                        onNavigate(item.route, null)
                    } else {
                        onNavigate(item.route, carId)
                    }
                }
            )
        }
    }
}