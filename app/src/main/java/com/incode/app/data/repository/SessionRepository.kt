package com.incode.app.data.repository

import com.incode.app.data.model.Session
import com.incode.app.data.remote.OpenCodeApi

class SessionRepository(private val api: OpenCodeApi) {

    fun getSessions(): Result<List<Session>> = runCatching {
        api.listSessions()
    }

    fun getSession(sessionId: String): Result<Session> = runCatching {
        api.getSession(sessionId)
    }

    fun createSession(title: String = "New Session"): Result<Session> = runCatching {
        api.createSession(title)
    }

    fun deleteSession(sessionId: String): Result<Unit> = runCatching {
        if (!api.deleteSession(sessionId)) {
            throw Exception("Failed to delete session")
        }
    }
}
