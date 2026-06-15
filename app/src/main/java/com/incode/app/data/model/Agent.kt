package com.incode.app.data.model

import org.json.JSONObject

data class Agent(
    val name: String,
    val description: String?,
    val mode: String,
    val color: String?,
    val model: String?,
    val isBuiltin: Boolean = false
) {
    val isPrimary: Boolean get() = mode == "primary"
    val isSubagent: Boolean get() = mode == "subagent"
    val isAll: Boolean get() = mode == "all"

    companion object {
        fun fromJson(name: String, json: JSONObject): Agent {
            return Agent(
                name = name,
                description = json.optString("description", null),
                mode = json.optString("mode", "primary"),
                color = json.optString("color", null),
                model = json.optString("model", null),
                isBuiltin = json.optBoolean("builtin", false)
            )
        }
    }
}
