package com.incode.app.data.model

import org.json.JSONObject

data class Session(
    val id: String,
    val title: String,
    val slug: String,
    val agent: String?,
    val model: String?,
    val timeCreated: Long,
    val timeUpdated: Long,
    val cost: Double,
    val tokensInput: Int,
    val tokensOutput: Int,
    val tokensReasoning: Int,
    val tokensCacheRead: Int,
    val tokensCacheWrite: Int,
    val messageCount: Int = 0
) {
    companion object {
        fun fromJson(json: JSONObject): Session {
            return Session(
                id = json.optString("id", ""),
                title = json.optString("title", "Untitled"),
                slug = json.optString("slug", ""),
                agent = json.optString("agent", null),
                model = json.optString("model", null),
                timeCreated = json.optLong("time_created", 0),
                timeUpdated = json.optLong("time_updated", 0),
                cost = json.optDouble("cost", 0.0),
                tokensInput = json.optInt("tokens_input", 0),
                tokensOutput = json.optInt("tokens_output", 0),
                tokensReasoning = json.optInt("tokens_reasoning", 0),
                tokensCacheRead = json.optInt("tokens_cache_read", 0),
                tokensCacheWrite = json.optInt("tokens_cache_write", 0),
                messageCount = json.optInt("message_count", 0)
            )
        }
    }

    fun formatTimeAgo(): String {
        val now = System.currentTimeMillis()
        val diff = now - (timeUpdated * 1000)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            days < 30 -> "${days / 7}w ago"
            else -> "${days / 30}mo ago"
        }
    }
}
