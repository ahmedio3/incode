package com.incode.app.data.remote

import com.incode.app.data.model.Agent
import com.incode.app.data.model.Message
import com.incode.app.data.model.ModelInfo
import com.incode.app.data.model.Part
import com.incode.app.data.model.Provider
import com.incode.app.data.model.ServerConfig
import com.incode.app.data.model.Session
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class OpenCodeApi(private val config: ServerConfig) {

    private val jsonMediaType = "application/json".toMediaType()

    private val client: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        // Add auth if configured
        if (config.username.isNotBlank()) {
            val credential = okhttp3.Credentials.basic(config.username, config.password)
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", credential)
                    .build()
                chain.proceed(request)
            }
        }

        builder.build()
    }

    private fun buildRequest(path: String, method: String = "GET", body: String? = null): Request {
        val url = "${config.baseUrl}$path"
        val builder = Request.Builder()
            .url(url)
            .method(method, body?.toRequestBody(jsonMediaType))

        return builder.build()
    }

    private fun execute(request: Request): String {
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code}: ${response.message} | $body")
        }
        return body ?: "{}"
    }

    // ==================== Health ====================
    fun health(): JSONObject {
        val request = buildRequest("/global/health")
        val body = execute(request)
        return JSONObject(body)
    }

    // ==================== Sessions ====================
    fun listSessions(): List<Session> {
        val request = buildRequest("/session")
        val body = execute(request)
        val json = JSONObject(body)
        val sessions = mutableListOf<Session>()

        // Try both formats: array or {sessions: [...]}
        val arr: JSONArray
        if (json.has("sessions")) {
            arr = json.getJSONArray("sessions")
        } else {
            // Assume it's directly an array wrapped in object
            arr = json.optJSONArray("sessions") ?: JSONArray()
        }

        for (i in 0 until arr.length()) {
            val sessionJson = arr.getJSONObject(i)
            sessions.add(Session.fromJson(sessionJson))
        }

        return sessions.sortedByDescending { it.timeUpdated }
    }

    fun getSession(sessionId: String): Session {
        val request = buildRequest("/session/$sessionId")
        val body = execute(request)
        return Session.fromJson(JSONObject(body))
    }

    fun createSession(title: String = "New Session"): Session {
        val json = JSONObject().apply {
            put("title", title)
        }
        val request = buildRequest("/session", "POST", json.toString())
        val body = execute(request)
        return Session.fromJson(JSONObject(body))
    }

    fun deleteSession(sessionId: String): Boolean {
        val request = buildRequest("/session/$sessionId", "DELETE")
        try {
            execute(request)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // ==================== Messages ====================
    fun getMessages(sessionId: String): List<Message> {
        val request = buildRequest("/session/$sessionId/messages")
        val body = execute(request)
        val json = JSONObject(body)
        val arr = json.optJSONArray("messages") ?: JSONArray()
        val messages = mutableListOf<Message>()

        for (i in 0 until arr.length()) {
            messages.add(Message.fromJson(arr.getJSONObject(i)))
        }

        return messages
    }

    fun getParts(sessionId: String, messageId: String): List<Part> {
        val request = buildRequest("/session/${sessionId}/message/${messageId}/parts")
        val body = execute(request)
        val json = JSONObject(body)
        val arr = json.optJSONArray("parts") ?: JSONArray()
        val parts = mutableListOf<Part>()

        for (i in 0 until arr.length()) {
            parts.add(Part.fromJson(arr.getJSONObject(i)))
        }

        return parts
    }

    // ==================== Config ====================
    fun getConfig(): JSONObject {
        val request = buildRequest("/config")
        val body = execute(request)
        return JSONObject(body)
    }

    fun getProviders(): JSONObject {
        val request = buildRequest("/config/providers")
        val body = execute(request)
        return JSONObject(body)
    }

    fun parseAgents(configJson: JSONObject): List<Agent> {
        val agents = mutableListOf<Agent>()
        val agentsObj = configJson.optJSONObject("agent") ?: return agents

        for (key in agentsObj.keys()) {
            val agentJson = agentsObj.getJSONObject(key)
            agents.add(Agent.fromJson(key, agentJson))
        }

        return agents
    }

    fun parseProviders(configJson: JSONObject): List<Provider> {
        val providers = mutableListOf<Provider>()
        val providersObj = configJson.optJSONObject("provider") ?: return providers

        for (providerId in providersObj.keys()) {
            val providerJson = providersObj.getJSONObject(providerId)
            val models = mutableListOf<ModelInfo>()
            val modelsObj = providerJson.optJSONObject("models")

            if (modelsObj != null) {
                for (modelId in modelsObj.keys()) {
                    val modelJson = modelsObj.getJSONObject(modelId)
                    models.add(ModelInfo.fromJson(modelId, modelJson, providerId))
                }
            }

            providers.add(
                Provider(
                    id = providerId,
                    name = providerJson.optString("name", providerId),
                    models = models
                )
            )
        }

        return providers
    }

    // Get HTTP client for SSE streaming (needs longer timeouts)
    fun getSSEClient(): OkHttpClient {
        return client.newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun getBaseUrl(): String = config.baseUrl
}
