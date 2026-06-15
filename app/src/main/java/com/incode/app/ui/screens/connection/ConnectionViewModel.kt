package com.incode.app.ui.screens.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incode.app.data.model.ServerConfig
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.remote.ServerConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConnectionUiState(
    val host: String = "127.0.0.1",
    val port: String = "4096",
    val username: String = "",
    val password: String = "",
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val error: String? = null,
    val serverVersion: String? = null
)

class ConnectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    private var api: OpenCodeApi? = null

    fun initFromConfig(config: ServerConfig) {
        _uiState.value = _uiState.value.copy(
            host = config.host,
            port = config.port.toString(),
            username = config.username,
            password = config.password
        )
    }

    fun updateHost(host: String) {
        _uiState.value = _uiState.value.copy(host = host, error = null)
    }

    fun updatePort(port: String) {
        _uiState.value = _uiState.value.copy(port = port, error = null)
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, error = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun connect() {
        val state = _uiState.value

        // Validate
        if (state.host.isBlank()) {
            _uiState.value = state.copy(error = "Host cannot be empty")
            return
        }
        val port = state.port.toIntOrNull()
        if (port == null || port !in 1..65535) {
            _uiState.value = state.copy(error = "Invalid port (1-65535)")
            return
        }

        _uiState.value = state.copy(isConnecting = true, error = null)

        val config = ServerConfig(
            host = state.host.trim(),
            port = port,
            username = state.username.trim(),
            password = state.password
        )

        ServerConnection.updateConfig(config)
        api = OpenCodeApi(config)

        viewModelScope.launch {
            try {
                val health = api?.health()
                val version = health?.optString("version", "unknown") ?: "unknown"

                ServerConnection.setConnected(true, version)
                _uiState.value = _uiState.value.copy(
                    isConnecting = false,
                    isConnected = true,
                    serverVersion = version,
                    error = null
                )
            } catch (e: Exception) {
                ServerConnection.setConnected(false)
                _uiState.value = _uiState.value.copy(
                    isConnecting = false,
                    isConnected = false,
                    error = "Connection failed: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun getApi(): OpenCodeApi? = api
}
