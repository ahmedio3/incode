package com.incode.app.ui.navigation

sealed class Screen(val route: String) {
    object Connection : Screen("connection")
    object Home : Screen("home")
    object Chat : Screen("chat/{sessionId}") {
        fun createRoute(sessionId: String) = "chat/$sessionId"
    }
    object Agents : Screen("agents")
    object Models : Screen("models")
    object Settings : Screen("settings")
    object SessionDetail : Screen("session/{sessionId}/detail") {
        fun createRoute(sessionId: String) = "session/$sessionId/detail"
    }
}

object NavRoutes {
    const val CONNECTION = "connection"
    const val HOME = "home"
    const val CHAT = "chat/{sessionId}"
    const val AGENTS = "agents"
    const val MODELS = "models"
    const val SETTINGS = "settings"
    const val SESSION_DETAIL = "session/{sessionId}/detail"

    const val ARG_SESSION_ID = "sessionId"
}
