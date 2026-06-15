package com.incode.app.data.remote

import com.incode.app.data.model.ServerConfig

object ServerConnection {
    var config: ServerConfig = ServerConfig()
        private set
    var isConnected: Boolean = false
        private set
    var serverVersion: String? = null
        private set
    var currentSessionId: String? = null
    var currentAgent: String? = null
    var currentModel: String? = null

    fun updateConfig(newConfig: ServerConfig) {
        config = newConfig
    }

    fun setConnected(connected: Boolean, version: String? = null) {
        isConnected = connected
        if (connected) {
            serverVersion = version
        } else {
            serverVersion = null
        }
    }

    fun reset() {
        isConnected = false
        serverVersion = null
        currentSessionId = null
        currentAgent = null
        currentModel = null
    }

    fun getBaseUrl(): String = config.baseUrl
}
