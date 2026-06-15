package com.incode.app.data.repository

import com.incode.app.data.model.Agent
import com.incode.app.data.model.Provider
import com.incode.app.data.remote.OpenCodeApi

class ConfigRepository(private val api: OpenCodeApi) {

    fun getAgents(): Result<List<Agent>> = runCatching {
        val config = api.getConfig()
        api.parseAgents(config)
    }

    fun getProviders(): Result<List<Provider>> = runCatching {
        val config = api.getConfig()
        api.parseProviders(config)
    }

    fun getConfigJson(): Result<String> = runCatching {
        api.getConfig().toString(2)
    }

    fun healthCheck(): Result<String> = runCatching {
        val health = api.health()
        health.optString("version", "unknown")
    }
}
