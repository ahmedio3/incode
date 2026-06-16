package com.incode.app.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.incode.app.ui.components.AgentChip
import com.incode.app.ui.components.ChatBubble
import com.incode.app.ui.components.ReasoningBlock
import com.incode.app.ui.components.StreamingIndicator
import com.incode.app.ui.components.ToolCallCard
import com.incode.app.ui.components.parseAgentColor
import com.incode.app.ui.theme.IncodeBackground
import com.incode.app.ui.theme.IncodeError
import com.incode.app.ui.theme.IncodeInputBar
import com.incode.app.ui.theme.IncodeInputField
import com.incode.app.ui.theme.IncodeOutline
import com.incode.app.ui.theme.IncodePrimary
import com.incode.app.ui.theme.IncodeSurface
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary
import com.incode.app.ui.theme.IncodeTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    sessionId: String,
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    val showScrollDownButton by remember {
        derivedStateOf {
            if (listState.layoutInfo.totalItemsCount == 0) false
            else {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisible < listState.layoutInfo.totalItemsCount - 2
            }
        }
    }

    // Auto-scroll when new messages arrive
    LaunchedEffect(state.messages.size, state.isStreaming) {
        if (state.messages.isNotEmpty() && state.isStreaming) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    LaunchedEffect(sessionId) {
        // Initialize will happen via the viewModel that's shared
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IncodeBackground)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = state.session?.title ?: "Chat",
                        style = MaterialTheme.typography.titleMedium,
                        color = IncodeTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.currentAgent != null) {
                            Text(
                                text = state.currentAgent!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = IncodePrimary
                            )
                        }
                        if (state.currentModel != null) {
                            if (state.currentAgent != null) {
                                Text(
                                    text = " · ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IncodeTextSecondary
                                )
                            }
                            Text(
                                text = state.currentModel!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = IncodeTextSecondary
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Back",
                        tint = IncodeTextPrimary
                    )
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = IncodeTextSecondary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Clear chat", color = IncodeTextPrimary) },
                            onClick = {
                                showMenu = false
                                // Implement clear chat
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Session details", color = IncodeTextPrimary) },
                            onClick = {
                                showMenu = false
                                // Navigate to session details
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = IncodeTopBar
            )
        )

        // Messages list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading messages...", color = IncodeTextSecondary)
                }
            } else if (state.messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = IncodeTextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Start a conversation",
                            style = MaterialTheme.typography.bodyLarge,
                            color = IncodeTextSecondary
                        )
                        Text(
                            text = "Send a message to begin",
                            style = MaterialTheme.typography.bodySmall,
                            color = IncodeTextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Use Box wrapper to resolve scope ambiguity
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = state.messages,
                            key = { it.id }
                        ) { item ->
                            when (item.type) {
                                ChatItemType.TEXT -> {
                                    if (item.isUser) {
                                        ChatBubble(
                                            text = item.text,
                                            isUser = true,
                                            timestamp = item.timestamp
                                        )
                                    } else {
                                        ChatBubble(
                                            text = item.text,
                                            isUser = false,
                                            timestamp = item.timestamp,
                                            agent = item.agent ?: state.currentAgent
                                        )
                                    }
                                }
                                ChatItemType.REASONING -> {
                                    ReasoningBlock(
                                        text = item.text,
                                        duration = item.duration
                                    )
                                }
                                ChatItemType.TOOL_INVOCATION -> {
                                    ToolCallCard(
                                        toolName = item.toolName ?: "tool",
                                        args = item.args,
                                        isRunning = item.isRunning
                                    )
                                }
                                ChatItemType.TOOL_RESULT -> {
                                    ToolCallCard(
                                        toolName = item.toolName ?: "tool",
                                        args = null,
                                        result = item.result,
                                        isRunning = false
                                    )
                                }
                                ChatItemType.STEP_START -> {
                                    StreamingIndicator(isStreaming = true)
                                }
                                ChatItemType.STEP_FINISH -> {
                                    // Small gap
                                }
                                ChatItemType.STREAMING_TEXT -> {
                                    ChatBubble(
                                        text = item.text,
                                        isUser = false,
                                        agent = item.agent ?: state.currentAgent
                                    )
                                }
                            }
                        }
                    }

                    // Scroll to bottom button
                    AnimatedVisibility(
                        visible = showScrollDownButton,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    listState.animateScrollToItem(state.messages.size - 1)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(IncodeSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Scroll to bottom",
                                tint = IncodeTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Error message
        AnimatedVisibility(
            visible = state.error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = state.error ?: "",
                color = IncodeError,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // Agent chips row
        if (state.agents.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (agent in state.agents.take(5)) {
                    val color = parseAgentColor(agent.color)
                    AgentChip(
                        name = agent.name,
                        color = color,
                        isCurrent = agent.name == state.currentAgent,
                        onClick = { viewModel.switchAgent(agent.name) }
                    )
                }
            }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(IncodeInputBar)
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = state.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                placeholder = {
                    Text(
                        "Type a message...",
                        color = IncodeTextSecondary
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp)
                    .animateContentSize(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = IncodeTextPrimary,
                    unfocusedTextColor = IncodeTextPrimary,
                    cursorColor = IncodePrimary,
                    focusedBorderColor = IncodeOutline,
                    unfocusedBorderColor = IncodeOutline,
                    focusedContainerColor = IncodeInputField,
                    unfocusedContainerColor = IncodeInputField
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (!state.isStreaming && state.inputText.isNotBlank()) {
                            viewModel.sendMessage()
                        }
                    }
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send or Cancel button
            IconButton(
                onClick = {
                    if (state.isStreaming) {
                        viewModel.cancelStream()
                    } else if (state.inputText.isNotBlank()) {
                        viewModel.sendMessage()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (state.isStreaming) IncodeError else IncodePrimary
                    )
            ) {
                Icon(
                    imageVector = if (state.isStreaming) Icons.Default.Close else Icons.Default.Send,
                    contentDescription = if (state.isStreaming) "Cancel" else "Send",
                    tint = IncodeTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
