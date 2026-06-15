package com.incode.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incode.app.data.model.ServerConfig
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.remote.ServerConnection
import com.incode.app.util.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val host: String = "127.0.0.1",
    val port: String = "4096",
    val username: String = "",
    val password: String = "",
    val isConnected: Boolean = false,
    val serverVersion: String? = null,
    val isTesting: Boolean = false,
    val testResult: String? = null,
    val appVersion: String = "1.0.0"
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var api: OpenCodeApi? = null
    private var preferencesManager: PreferencesManager? = null

    fun init(prefsManager: PreferencesManager) {
        preferencesManager = prefsManager
        val config = prefsManager.getServerConfig()
        _uiState.value = _uiState.value.copy(
            host = config.host,
            port = config.port.toString(),
            username = config.username,
            password = config.password,
            isConnected = ServerConnection.isConnected,
            serverVersion = ServerConnection.serverVersion
        )
    }

    fun updateHost(host: String) {
        _uiState.value = _uiState.value.copy(host = host)
    }

    fun updatePort(port: String) {
        _uiState.value = _uiState.value.copy(port = port)
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun saveAndReconnect() {
        val state = _uiState.value
        val port = state.port.toIntOrNull() ?: 4096

        val config = ServerConfig(
            host = state.host.trim(),
            port = port,
            username = state.username.trim(),
            password = state.password
        )

        preferencesManager?.saveServerConfig(config)
        ServerConnection.updateConfig(config)

        // Reconnect
        testConnection()
    }

    fun testConnection() {
        _uiState.value = _uiState.value.copy(isTesting = true, testResult = null)

        val config = ServerConfig(
            host = _uiState.value.host.trim(),
            port = _uiState.value.port.toIntOrNull() ?: 4096,
            username = _uiState.value.username.trim(),
            password = _uiState.value.password
        )

        api = OpenCodeApi(config)

        viewModelScope.launch {
            try {
                val health = api?.health()
                val version = health?.optString("version", "unknown") ?: "unknown"
                ServerConnection.setConnected(true, version)
                _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    isConnected = true,
                    serverVersion = version,
                    testResult = "Connected! Server: $version"
                )
            } catch (e: Exception) {
                ServerConnection.setConnected(false)
                _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    isConnected = false,
                    testResult = "Failed: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun disconnect() {
        ServerConnection.reset()
        _uiState.value = _uiState.value.copy(
            isConnected = false,
            serverVersion = null,
            testResult = "Disconnected"
        )
    }

    fun clearAllData() {
        preferencesManager?.clearAll()
        ServerConnection.reset()
        _uiState.value = SettingsUiState()
    }

    fun getApi(): OpenCodeApi? = api
}
