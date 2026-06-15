package com.incode.app.ui.screens.agents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incode.app.data.model.Agent
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.remote.ServerConnection
import com.incode.app.data.repository.ConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AgentsUiState(
    val primaryAgents: List<Agent> = emptyList(),
    val subAgents: List<Agent> = emptyList(),
    val currentAgent: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class AgentsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AgentsUiState())
    val uiState: StateFlow<AgentsUiState> = _uiState.asStateFlow()

    private var repository: ConfigRepository? = null

    fun init(api: OpenCodeApi) {
        repository = ConfigRepository(api)
        _uiState.value = _uiState.value.copy(
            currentAgent = ServerConnection.currentAgent
        )
        loadAgents()
    }

    fun loadAgents() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            repository?.getAgents()?.onSuccess { agents ->
                val filtered = filterAgents(agents, _uiState.value.searchQuery)
                _uiState.value = _uiState.value.copy(
                    primaryAgents = filtered.filter { it.isPrimary || it.isAll },
                    subAgents = filtered.filter { it.isSubagent },
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

    fun switchAgent(agentName: String) {
        ServerConnection.currentAgent = agentName
        _uiState.value = _uiState.value.copy(currentAgent = agentName)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadAgents()
    }

    private fun filterAgents(agents: List<Agent>, query: String): List<Agent> {
        if (query.isBlank()) return agents
        return agents.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.description?.contains(query, ignoreCase = true) == true
        }
    }
}
