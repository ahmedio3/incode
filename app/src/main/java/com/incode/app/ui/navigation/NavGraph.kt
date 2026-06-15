package com.incode.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.remote.ServerConnection
import com.incode.app.ui.screens.agents.AgentsScreen
import com.incode.app.ui.screens.agents.AgentsViewModel
import com.incode.app.ui.screens.chat.ChatScreen
import com.incode.app.ui.screens.chat.ChatViewModel
import com.incode.app.ui.screens.connection.ConnectionScreen
import com.incode.app.ui.screens.connection.ConnectionViewModel
import com.incode.app.ui.screens.home.HomeScreen
import com.incode.app.ui.screens.home.HomeViewModel
import com.incode.app.ui.screens.models.ModelsScreen
import com.incode.app.ui.screens.models.ModelsViewModel
import com.incode.app.ui.screens.session.SessionDetailScreen
import com.incode.app.ui.screens.session.SessionDetailViewModel
import com.incode.app.ui.screens.settings.SettingsScreen
import com.incode.app.ui.screens.settings.SettingsViewModel
import com.incode.app.util.PreferencesManager

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    val savedConfig = remember { prefsManager.getServerConfig() }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.CONNECTION
    ) {
        // Connection Screen
        composable(NavRoutes.CONNECTION) {
            val viewModel: ConnectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            viewModel.initFromConfig(savedConfig)

            ConnectionScreen(
                onConnected = {
                    val api = viewModel.getApi()
                    if (api != null) {
                        // Save config on successful connection
                        val state = viewModel.uiState.value
                        prefsManager.saveServerConfig(
                            com.incode.app.data.model.ServerConfig(
                                host = state.host,
                                port = state.port.toIntOrNull() ?: 4096,
                                username = state.username,
                                password = state.password
                            )
                        )
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.CONNECTION) { inclusive = true }
                        }
                    }
                },
                viewModel = viewModel
            )
        }

        // Home Screen (with bottom nav tabs)
        composable(NavRoutes.HOME) {
            val api = remember {
                if (ServerConnection.isConnected) {
                    OpenCodeApi(ServerConnection.config)
                } else {
                    null
                }
            }

            if (api == null) {
                navController.navigate(NavRoutes.CONNECTION) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }

            val viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            androidx.compose.runtime.LaunchedEffect(api) {
                viewModel.init(api)
            }

            HomeScreen(
                onSessionClick = { sessionId ->
                    ServerConnection.currentSessionId = sessionId
                    prefsManager.setCurrentSessionId(sessionId)
                    navController.navigate(Screen.Chat.createRoute(sessionId))
                },
                viewModel = viewModel
            )
        }

        // Chat Screen
        composable(
            route = NavRoutes.CHAT,
            arguments = listOf(
                navArgument(NavRoutes.ARG_SESSION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString(NavRoutes.ARG_SESSION_ID) ?: return@composable
            val api = remember {
                if (ServerConnection.isConnected) {
                    OpenCodeApi(ServerConnection.config)
                } else {
                    navController.navigate(NavRoutes.CONNECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                    null
                }
            }

            if (api == null) return@composable

            val viewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            androidx.compose.runtime.LaunchedEffect(sessionId, api) {
                viewModel.init(sessionId, api)
            }

            ChatScreen(
                sessionId = sessionId,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // Agents Screen
        composable(NavRoutes.AGENTS) {
            val api = remember {
                if (ServerConnection.isConnected) {
                    OpenCodeApi(ServerConnection.config)
                } else null
            }
            if (api == null) return@composable

            val viewModel: AgentsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            androidx.compose.runtime.LaunchedEffect(api) {
                viewModel.init(api)
            }

            AgentsScreen(viewModel = viewModel)
        }

        // Models Screen
        composable(NavRoutes.MODELS) {
            val api = remember {
                if (ServerConnection.isConnected) {
                    OpenCodeApi(ServerConnection.config)
                } else null
            }
            if (api == null) return@composable

            val viewModel: ModelsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            androidx.compose.runtime.LaunchedEffect(api) {
                viewModel.init(api)
            }

            ModelsScreen(viewModel = viewModel)
        }

        // Settings Screen
        composable(NavRoutes.SETTINGS) {
            val viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            androidx.compose.runtime.LaunchedEffect(prefsManager) {
                viewModel.init(prefsManager)
            }

            SettingsScreen(viewModel = viewModel)
        }

        // Session Detail Screen
        composable(
            route = NavRoutes.SESSION_DETAIL,
            arguments = listOf(
                navArgument(NavRoutes.ARG_SESSION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString(NavRoutes.ARG_SESSION_ID) ?: return@composable
            val api = remember {
                if (ServerConnection.isConnected) {
                    OpenCodeApi(ServerConnection.config)
                } else null
            }
            if (api == null) return@composable

            val viewModel: SessionDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            androidx.compose.runtime.LaunchedEffect(sessionId, api) {
                viewModel.init(sessionId, api)
            }

            SessionDetailScreen(
                sessionId = sessionId,
                onBack = { navController.popBackStack() },
                onDeleted = {
                    navController.popBackStack(NavRoutes.HOME, false)
                },
                viewModel = viewModel
            )
        }
    }
}
