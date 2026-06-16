package com.incode.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.incode.app.ui.navigation.NavRoutes
import com.incode.app.ui.navigation.Screen
import com.incode.app.ui.theme.IncodeBackground
import com.incode.app.ui.theme.IncodeBottomNav
import com.incode.app.ui.theme.IncodeBottomNavBorder
import com.incode.app.ui.theme.IncodePrimary
import com.incode.app.ui.theme.IncodeTextSecondary

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Chats", NavRoutes.HOME, Icons.Filled.Chat, Icons.Outlined.Chat),
    BottomNavItem("Agents", NavRoutes.AGENTS, Icons.Filled.Settings, Icons.Outlined.SmartToy),
    BottomNavItem("Settings", NavRoutes.SETTINGS, Icons.Filled.Settings, Icons.Outlined.Settings)
)

val bottomNavRoutes = listOf(NavRoutes.HOME, NavRoutes.AGENTS, NavRoutes.SETTINGS)

@Composable
fun MainScreen(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IncodeBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                content()
            }

            // Bottom Navigation
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = IncodeBottomNav
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(NavRoutes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IncodePrimary,
                                selectedTextColor = IncodePrimary,
                                unselectedIconColor = IncodeTextSecondary,
                                unselectedTextColor = IncodeTextSecondary,
                                indicatorColor = IncodePrimary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    }
}
