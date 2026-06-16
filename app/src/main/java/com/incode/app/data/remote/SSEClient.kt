package com.incode.app.data.remote

import com.incode.app.data.model.SSEEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class SSEClient(private val client: OkHttpClient, private val baseUrl: String) {

    private val jsonMediaType = "application/json".toMediaType()

    /**
     * Streams a message to opencode via SSE.
     * Returns a cancel function.
     */
    fun streamMessage(
        sessionId: String,
        text: String,
        model: String? = null,
        agent: String? = null,
        onEvent: (SSEEvent) -> Unit,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ): () -> Unit {
        val cancelLock = Any()
        var cancelled = false

        val thread = Thread {
            try {
                val bodyJson = JSONObject().apply {
                    val parts = JSONArray()
                    parts.put(JSONObject().apply {
                        put("type", "text")
                        put("text", text)
                    })
                    put("parts", parts)
                    model?.let { put("model", it) }
                    agent?.let { put("agent", it) }
                }

                val request = Request.Builder()
                    .url("$baseUrl/session/$sessionId/message")
                    .post(bodyJson.toString().toRequestBody(jsonMediaType))
                    .header("Accept", "text/event-stream")
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body ?: throw Exception("Empty response body")

                if (!response.isSuccessful) {
                    throw Exception("HTTP ${response.code}: ${response.message}")
                }

                val reader = BufferedReader(InputStreamReader(body.byteStream()))
                var line: String?
                var currentEvent = ""
                var currentData = StringBuilder()

                while (reader.readLine().also { line = it } != null) {
                    synchronized(cancelLock) {
                        if (cancelled) {
                            reader.close()
                            body.close()
                            return@Thread
                        }
                    }

                    val currentLine = line ?: continue

                    when {
                        currentLine.startsWith("event:") -> {
                            currentEvent = currentLine.substring(6).trim()
                        }
                        currentLine.startsWith("data:") -> {
                            if (currentData.isNotEmpty()) currentData.append("\n")
                            currentData.append(currentLine.substring(5).trim())
                        }
                        currentLine.isEmpty() -> {
                            // Empty line = end of event
                            if (currentData.isNotEmpty() && currentEvent == "message.updated") {
                                try {
                                    val eventJson = JSONObject(currentData.toString())
                                    val event = SSEEvent.fromJson(eventJson)
                                    onEvent(event)

                                    if (event.info.finish) {
                                        onComplete()
                                    }
                                } catch (e: Exception) {
                                    onError(e)
                                }
                            }
                            currentEvent = ""
                            currentData = StringBuilder()
                        }
                    }
                }

                // Handle any remaining data
                if (currentData.isNotEmpty() && currentEvent == "message.updated") {
                    try {
                        val eventJson = JSONObject(currentData.toString())
                        val event = SSEEvent.fromJson(eventJson)
                        onEvent(event)

                        if (event.info.finish) {
                            onComplete()
                        }
                    } catch (e: Exception) {
                        onError(e)
                    }
                }

                reader.close()
                body.close()
                onComplete()
            } catch (e: Exception) {
                synchronized(cancelLock) {
                    if (!cancelled) {
                        onError(e)
                    }
                }
            }
        }

        thread.isDaemon = true
        thread.start()

        return {
            synchronized(cancelLock) {
                cancelled = true
            }
            thread.interrupt()
        }
    }
}
