package com.incode.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incode.app.data.model.Session
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val sessions: List<Session> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val showCreateDialog: Boolean = false
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var repository: SessionRepository? = null
    private var cachedSessions: List<Session> = emptyList()

    fun init(api: OpenCodeApi) {
        repository = SessionRepository(api)
        loadSessions()
    }

    fun loadSessions() {
        val repo = repository ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            repo.getSessions()
                .onSuccess { sessions ->
                    cachedSessions = sessions
                    _uiState.value = _uiState.value.copy(
                        sessions = filterSessions(sessions, _uiState.value.searchQuery),
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load sessions"
                    )
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        _uiState.value = _uiState.value.copy(
            sessions = filterSessions(cachedSessions, query)
        )
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun createSession(title: String) {
        val repo = repository ?: return
        _uiState.value = _uiState.value.copy(showCreateDialog = false)

        viewModelScope.launch {
            repo.createSession(title)
                .onSuccess { session ->
                    // Navigate to chat with new session
                    _newSessionId = session.id
                    loadSessions()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.localizedMessage ?: "Failed to create session"
                    )
                }
        }
    }

    fun deleteSession(sessionId: String) {
        val repo = repository ?: return

        viewModelScope.launch {
            repo.deleteSession(sessionId)
                .onSuccess {
                    loadSessions()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.localizedMessage ?: "Failed to delete session"
                    )
                }
        }
    }

    // Track new session for navigation
    private var _newSessionId: String? = null
    fun consumeNewSessionId(): String? {
        val id = _newSessionId
        _newSessionId = null
        return id
    }

    private fun filterSessions(sessions: List<Session>, query: String): List<Session> {
        if (query.isBlank()) return sessions
        return sessions.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.slug.contains(query, ignoreCase = true) ||
            (it.agent?.contains(query, ignoreCase = true) == true)
        }
    }
}
