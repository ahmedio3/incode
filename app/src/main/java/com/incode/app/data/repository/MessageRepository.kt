package com.incode.app.data.repository

import com.incode.app.data.model.Message
import com.incode.app.data.model.Part
import com.incode.app.data.remote.OpenCodeApi

class MessageRepository(private val api: OpenCodeApi) {

    fun getMessages(sessionId: String): Result<List<Message>> = runCatching {
        api.getMessages(sessionId)
    }

    fun getParts(sessionId: String, messageId: String): Result<List<Part>> = runCatching {
        api.getParts(sessionId, messageId)
    }

    fun getAllPartsForSession(sessionId: String, messages: List<Message>): Result<Map<String, List<Part>>> = runCatching {
        val result = mutableMapOf<String, List<Part>>()
        for (msg in messages) {
            result[msg.id] = api.getParts(sessionId, msg.id)
        }
        result
    }
}
