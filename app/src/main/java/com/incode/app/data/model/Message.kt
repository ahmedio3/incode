package com.incode.app.data.model

import org.json.JSONObject

data class Message(
    val id: String,
    val sessionId: String,
    val timeCreated: Long,
    val role: String,
    val agent: String?,
    val model: String?,
    val summary: String?
) {
    val isUser: Boolean get() = role == "user"

    companion object {
        private val KEY_ID = "id"
        private val KEY_SESSION_ID = "session_id"
        private val KEY_TIME_CREATED = "time_created"
        private val KEY_DATA = "data"

        fun fromJson(json: JSONObject): Message {
            val data = json.optJSONObject(KEY_DATA) ?: JSONObject()
            return Message(
                id = json.optString(KEY_ID, ""),
                sessionId = json.optString(KEY_SESSION_ID, ""),
                timeCreated = json.optLong(KEY_TIME_CREATED, 0),
                role = data.optString("role", "user"),
                agent = data.optString("agent", null),
                model = data.optString("model", null),
                summary = data.optString("summary", null)
            )
        }
    }
}
