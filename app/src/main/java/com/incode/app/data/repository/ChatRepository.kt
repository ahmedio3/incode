package com.incode.app.data.repository

import com.incode.app.data.model.SSEEvent
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.remote.SSEClient

class ChatRepository(private val api: OpenCodeApi) {

    private var sseClient: SSEClient? = null
    private var cancelFunction: (() -> Unit)? = null

    fun sendMessage(
        sessionId: String,
        text: String,
        model: String?,
        agent: String?,
        onEvent: (SSEEvent) -> Unit,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Cancel any existing stream
        cancelFunction?.invoke()

        val client = api.getSSEClient()
        sseClient = SSEClient(client, api.getBaseUrl())

        cancelFunction = sseClient!!.streamMessage(
            sessionId = sessionId,
            text = text,
            model = model,
            agent = agent,
            onEvent = onEvent,
            onComplete = {
                cancelFunction = null
                onComplete()
            },
            onError = { error ->
                cancelFunction = null
                onError(error)
            }
        )
    }

    fun cancelStream() {
        cancelFunction?.invoke()
        cancelFunction = null
    }

    fun isStreaming(): Boolean = cancelFunction != null
}
