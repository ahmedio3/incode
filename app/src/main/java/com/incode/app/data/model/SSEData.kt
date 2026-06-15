package com.incode.app.data.model

import org.json.JSONArray
import org.json.JSONObject

data class SSEEvent(
    val info: SSEInfo,
    val parts: List<SSEPart>
) {
    data class SSEInfo(
        val finish: Boolean = false
    )

    data class SSEPart(
        val type: String,
        val text: String?,
        val toolName: String?,
        val args: JSONObject?,
        val result: String?,
        val time: JSONObject?
    )

    companion object {
        fun fromJson(json: JSONObject): SSEEvent {
            val infoObj = json.optJSONObject("info") ?: JSONObject()
            val partsArray = json.optJSONArray("parts") ?: JSONArray()
            val parts = mutableListOf<SSEPart>()

            for (i in 0 until partsArray.length()) {
                val partObj = partsArray.optJSONObject(i) ?: continue
                parts.add(
                    SSEPart(
                        type = partObj.optString("type", ""),
                        text = partObj.optString("text", null),
                        toolName = partObj.optString("toolName", null),
                        args = partObj.optJSONObject("args"),
                        result = partObj.optString("result", null),
                        time = partObj.optJSONObject("time")
                    )
                )
            }

            return SSEEvent(
                info = SSEInfo(
                    finish = infoObj.optBoolean("finish", false)
                ),
                parts = parts
            )
        }
    }
}
