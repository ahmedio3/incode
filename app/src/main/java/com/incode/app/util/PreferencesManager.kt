package com.incode.app.util

import android.content.Context
import android.content.SharedPreferences
import com.incode.app.data.model.ServerConfig

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("incode_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HOST = "server_host"
        private const val KEY_PORT = "server_port"
        private const val KEY_USERNAME = "server_username"
        private const val KEY_PASSWORD = "server_password"
        private const val KEY_CURRENT_SESSION = "current_session"
        private const val KEY_CURRENT_AGENT = "current_agent"
        private const val KEY_CURRENT_MODEL = "current_model"

        private const val DEFAULT_HOST = "127.0.0.1"
        private const val DEFAULT_PORT = 4096
    }

    fun getServerConfig(): ServerConfig {
        return ServerConfig(
            host = prefs.getString(KEY_HOST, DEFAULT_HOST) ?: DEFAULT_HOST,
            port = prefs.getInt(KEY_PORT, DEFAULT_PORT),
            username = prefs.getString(KEY_USERNAME, "") ?: "",
            password = prefs.getString(KEY_PASSWORD, "") ?: ""
        )
    }

    fun saveServerConfig(config: ServerConfig) {
        prefs.edit()
            .putString(KEY_HOST, config.host)
            .putInt(KEY_PORT, config.port)
            .putString(KEY_USERNAME, config.username)
            .putString(KEY_PASSWORD, config.password)
            .apply()
    }

    fun getCurrentSessionId(): String? {
        return prefs.getString(KEY_CURRENT_SESSION, null)
    }

    fun setCurrentSessionId(sessionId: String?) {
        prefs.edit().putString(KEY_CURRENT_SESSION, sessionId).apply()
    }

    fun getCurrentAgent(): String? {
        return prefs.getString(KEY_CURRENT_AGENT, null)
    }

    fun setCurrentAgent(agent: String?) {
        prefs.edit().putString(KEY_CURRENT_AGENT, agent).apply()
    }

    fun getCurrentModel(): String? {
        return prefs.getString(KEY_CURRENT_MODEL, null)
    }

    fun setCurrentModel(model: String?) {
        prefs.edit().putString(KEY_CURRENT_MODEL, model).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
