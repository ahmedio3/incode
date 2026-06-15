package com.incode.app.data.model

import org.json.JSONObject

data class Provider(
    val id: String,
    val name: String?,
    val models: List<ModelInfo>
)

data class ModelInfo(
    val id: String,
    val name: String?,
    val providerId: String
) {
    companion object {
        fun fromJson(id: String, json: JSONObject, providerId: String): ModelInfo {
            return ModelInfo(
                id = id,
                name = json.optString("name", id),
                providerId = providerId
            )
        }
    }
}

data class ConfigResponse(
    val agents: List<Agent>,
    val providers: List<Provider>,
    val version: String?
)
