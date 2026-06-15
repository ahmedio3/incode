package com.incode.app.data.model

import org.json.JSONObject

data class Part(
    val id: String,
    val messageId: String,
    val sessionId: String,
    val timeCreated: Long,
    val type: String,
    val text: String?,
    val toolName: String?,
    val args: String?,
    val result: String?,
    val timeStart: Long?,
    val timeEnd: Long?,
    val duration: Long?
) {
    val isReasoning: Boolean get() = type == "reasoning"
    val isText: Boolean get() = type == "text"
    val isToolInvocation: Boolean get() = type == "tool-invocation"
    val isToolResult: Boolean get() = type == "tool-result"
    val isStepStart: Boolean get() = type == "step-start"
    val isStepFinish: Boolean get() = type == "step-finish"

    companion object {
        fun fromJson(json: JSONObject): Part {
            return Part(
                id = json.optString("id", ""),
                messageId = json.optString("message_id", ""),
                sessionId = json.optString("session_id", ""),
                timeCreated = json.optLong("time_created", 0),
                type = json.optString("type", ""),
                text = json.optString("text", null),
                toolName = json.optString("toolName", null),
                args = json.optString("args", null),
                result = json.optString("result", null),
                timeStart = null,
                timeEnd = null,
                duration = null
            )
        }

        fun fromSSEPart(json: JSONObject): Part {
            val time = json.optJSONObject("time")
            val start = time?.optLong("start")
            val end = time?.optLong("end")
            var duration: Long? = null
            if (start != null && end != null) {
                duration = end - start
            }

            return Part(
                id = "",
                messageId = "",
                sessionId = "",
                timeCreated = System.currentTimeMillis(),
                type = json.optString("type", ""),
                text = json.optString("text", null),
                toolName = json.optString("toolName", null),
                args = json.optJSONObject("args")?.toString(),
                result = json.optString("result", null),
                timeStart = start,
                timeEnd = end,
                duration = duration
            )
        }
    }
}
