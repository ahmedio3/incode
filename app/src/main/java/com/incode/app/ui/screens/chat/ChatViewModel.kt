package com.incode.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incode.app.data.model.Agent
import com.incode.app.data.model.Message
import com.incode.app.data.model.Part
import com.incode.app.data.model.SSEEvent
import com.incode.app.data.model.Session
import com.incode.app.data.remote.OpenCodeApi
import com.incode.app.data.remote.ServerConnection
import com.incode.app.data.repository.ChatRepository
import com.incode.app.data.repository.ConfigRepository
import com.incode.app.data.repository.MessageRepository
import com.incode.app.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatItem(
    val id: String,
    val type: ChatItemType,
    val text: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = 0,
    val role: String = "user",
    val agent: String? = null,
    val toolName: String? = null,
    val args: String? = null,
    val result: String? = null,
    val duration: Long? = null,
    val isRunning: Boolean = false
)

enum class ChatItemType {
    TEXT,
    REASONING,
    TOOL_INVOCATION,
    TOOL_RESULT,
    STEP_START,
    STEP_FINISH,
    STREAMING_TEXT
}

data class ChatUiState(
    val session: Session? = null,
    val messages: List<ChatItem> = emptyList(),
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val error: String? = null,
    val inputText: String = "",
    val currentAgent: String? = null,
    val currentModel: String? = null,
    val agents: List<Agent> = emptyList()
)

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var sessionRepository: SessionRepository? = null
    private var messageRepository: MessageRepository? = null
    private var chatRepository: ChatRepository? = null
    private var configRepository: ConfigRepository? = null
    private var sessionId: String = ""

    private var currentTextParts = mutableListOf<String>()

    fun init(sessionId: String, api: OpenCodeApi) {
        this.sessionId = sessionId
        ServerConnection.currentSessionId = sessionId

        sessionRepository = SessionRepository(api)
        messageRepository = MessageRepository(api)
        chatRepository = ChatRepository(api)
        configRepository = ConfigRepository(api)

        loadSession()
        loadMessages()
        loadConfig()
    }

    private fun loadSession() {
        sessionRepository?.getSession(sessionId)?.onSuccess { session ->
            _uiState.value = _uiState.value.copy(
                session = session,
                currentAgent = session.agent ?: ServerConnection.currentAgent,
                currentModel = session.model ?: ServerConnection.currentModel
            )
        }
    }

    private fun loadMessages() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            messageRepository?.getMessages(sessionId)?.onSuccess { messages ->
                val chatItems = mutableListOf<ChatItem>()

                for (msg in messages) {
                    val parts = messageRepository?.getParts(sessionId, msg.id)?.getOrNull() ?: emptyList()

                    if (parts.isEmpty()) {
                        // Just add the message summary
                        chatItems.add(
                            ChatItem(
                                id = msg.id,
                                type = ChatItemType.TEXT,
                                text = msg.summary ?: "",
                                isUser = msg.isUser,
                                timestamp = msg.timeCreated,
                                role = msg.role,
                                agent = msg.agent
                            )
                        )
                    } else {
                        for (part in parts) {
                            chatItems.add(partToChatItem(part, msg))
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    messages = chatItems,
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

    private fun loadConfig() {
        configRepository?.getAgents()?.onSuccess { agents ->
            _uiState.value = _uiState.value.copy(
                agents = agents.filter { it.isPrimary }
            )
        }
    }

    private fun partToChatItem(part: Part, msg: Message): ChatItem {
        return ChatItem(
            id = part.id,
            type = when (part.type) {
                "reasoning" -> ChatItemType.REASONING
                "tool-invocation" -> ChatItemType.TOOL_INVOCATION
                "tool-result" -> ChatItemType.TOOL_RESULT
                "step-start" -> ChatItemType.STEP_START
                "step-finish" -> ChatItemType.STEP_FINISH
                else -> ChatItemType.TEXT
            },
            text = part.text ?: "",
            isUser = msg.isUser,
            timestamp = part.timeCreated,
            role = msg.role,
            agent = msg.agent,
            toolName = part.toolName,
            args = part.args,
            result = part.result,
            duration = part.duration
        )
    }

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isStreaming) return

        // Add user message
        val userItem = ChatItem(
            id = "user_${System.currentTimeMillis()}",
            type = ChatItemType.TEXT,
            text = text,
            isUser = true,
            timestamp = System.currentTimeMillis() / 1000,
            role = "user"
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userItem,
            inputText = "",
            isStreaming = true,
            error = null
        )

        currentTextParts.clear()

        val model = _uiState.value.currentModel
        val agent = _uiState.value.currentAgent

        chatRepository?.sendMessage(
            sessionId = sessionId,
            text = text,
            model = model,
            agent = agent,
            onEvent = { event -> handleSSEEvent(event) },
            onComplete = { onStreamComplete() },
            onError = { error -> onStreamError(error) }
        )
    }

    private fun handleSSEEvent(event: SSEEvent) {
        val currentMessages = _uiState.value.messages.toMutableList()
        var changed = false

        for (part in event.parts) {
            val item = when (part.type) {
                "step-start" -> {
                    ChatItem(
                        id = "step_${System.currentTimeMillis()}",
                        type = ChatItemType.STEP_START,
                        text = "Thinking...",
                        isRunning = true
                    )
                }
                "step-finish" -> {
                    // Remove the step start if exists, add finished
                    ChatItem(
                        id = "step_${System.currentTimeMillis()}",
                        type = ChatItemType.STEP_FINISH,
                        text = "Step completed"
                    )
                }
                "reasoning" -> {
                    ChatItem(
                        id = "reasoning_${System.currentTimeMillis()}",
                        type = ChatItemType.REASONING,
                        text = part.text ?: "",
                        duration = if (part.time?.optLong("start") != null && part.time?.optLong("end") != null) {
                            (part.time.optLong("end") - part.time.optLong("start"))
                        } else null
                    )
                }
                "tool-invocation" -> {
                    ChatItem(
                        id = "tool_${System.currentTimeMillis()}",
                        type = ChatItemType.TOOL_INVOCATION,
                        text = part.text ?: "",
                        toolName = part.toolName,
                        args = part.args?.toString(),
                        isRunning = true
                    )
                }
                "tool-result" -> {
                    ChatItem(
                        id = "toolresult_${System.currentTimeMillis()}",
                        type = ChatItemType.TOOL_RESULT,
                        toolName = part.toolName,
                        result = part.result
                    )
                }
                "text" -> {
                    ChatItem(
                        id = "stream_${System.currentTimeMillis()}",
                        type = ChatItemType.TEXT,
                        text = part.text ?: "",
                        isUser = false,
                        timestamp = System.currentTimeMillis() / 1000,
                        role = "assistant"
                    )
                }
                else -> null
            }

            if (item != null) {
                currentMessages.add(item)
                changed = true
            }
        }

        if (changed) {
            _uiState.value = _uiState.value.copy(messages = currentMessages)
        }
    }

    private fun onStreamComplete() {
        _uiState.value = _uiState.value.copy(isStreaming = false)
        loadMessages() // Refresh messages from server
    }

    private fun onStreamError(error: Exception) {
        _uiState.value = _uiState.value.copy(
            isStreaming = false,
            error = error.localizedMessage ?: "Stream error"
        )
    }

    fun cancelStream() {
        chatRepository?.cancelStream()
        _uiState.value = _uiState.value.copy(isStreaming = false)
    }

    fun switchModel(modelName: String) {
        ServerConnection.currentModel = modelName
        _uiState.value = _uiState.value.copy(currentModel = modelName)
    }

    fun switchAgent(agentName: String) {
        ServerConnection.currentAgent = agentName
        _uiState.value = _uiState.value.copy(currentAgent = agentName)
    }
}
