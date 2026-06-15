package com.incode.app.ui.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incode.app.data.model.Session
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SessionDetailUiState(
    val session: Session? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val deleteError: String? = null
)

class SessionDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    private var repository: SessionRepository? = null
    private var sessionId: String = ""

    fun init(sessionId: String, api: OpenCodeApi) {
        this.sessionId = sessionId
        repository = SessionRepository(api)
        loadSession()
    }

    fun loadSession() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            repository?.getSession(sessionId)?.onSuccess { session ->
                _uiState.value = _uiState.value.copy(
                    session = session,
                    isLoading = false
                )
            }?.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
            }
        }
    }

    fun deleteSession() {
        _uiState.value = _uiState.value.copy(isDeleting = true, deleteError = null)

        viewModelScope.launch {
            repository?.deleteSession(sessionId)?.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    isDeleted = true
                )
            }?.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    deleteError = e.localizedMessage
                )
            }
        }
    }
}
