package com.incode.app.ui.screens.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incode.app.data.model.ModelInfo
import com.incode.app.data.model.Provider
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.remote.ServerConnection
import com.incode.app.data.repository.ConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ModelsUiState(
    val providers: List<Provider> = emptyList(),
    val currentModel: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class ModelsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ModelsUiState())
    val uiState: StateFlow<ModelsUiState> = _uiState.asStateFlow()

    private var repository: ConfigRepository? = null

    fun init(api: OpenCodeApi) {
        repository = ConfigRepository(api)
        _uiState.value = _uiState.value.copy(
            currentModel = ServerConnection.currentModel
        )
        loadModels()
    }

    fun loadModels() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            repository?.getProviders()?.onSuccess { providers ->
                _uiState.value = _uiState.value.copy(
                    providers = providers,
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

    fun selectModel(modelInfo: ModelInfo) {
        val modelId = "${modelInfo.providerId}/${modelInfo.id}"
        ServerConnection.currentModel = modelId
        _uiState.value = _uiState.value.copy(currentModel = modelId)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}
