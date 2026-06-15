package com.incode.app.ui.screens.agents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.incode.app.data.model.Agent
import com.incode.app.ui.theme.IncodeBackground
import com.incode.app.ui.theme.IncodeInputField
import com.incode.app.ui.theme.IncodeOutline
import com.incode.app.ui.theme.IncodePrimary
import com.incode.app.ui.theme.IncodeSurface
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary
import com.incode.app.ui.theme.IncodeTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsScreen(
    viewModel: AgentsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IncodeBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Agents",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = IncodeTextPrimary
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = IncodeTopBar
            )
        )

        // Search
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search agents...", color = IncodeTextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = IncodeTextPrimary,
                unfocusedTextColor = IncodeTextPrimary,
                cursorColor = IncodePrimary,
                focusedBorderColor = IncodeOutline,
                unfocusedBorderColor = IncodeOutline,
                focusedContainerColor = IncodeInputField,
                unfocusedContainerColor = IncodeInputField
            ),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = IncodeTextSecondary)
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = IncodeTextSecondary)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Primary Agents section
            if (state.primaryAgents.isNotEmpty()) {
                item {
                    SectionHeader("Primary Agents")
                }
                items(
                    items = state.primaryAgents,
                    key = { it.name }
                ) { agent ->
                    AgentCard(
                        agent = agent,
                        isCurrent = agent.name == state.currentAgent,
                        onClick = { viewModel.switchAgent(agent.name) }
                    )
                }
            }

            // Subagents section
            if (state.subAgents.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SectionHeader("Subagents")
                }
                items(
                    items = state.subAgents,
                    key = { it.name }
                ) { agent ->
                    AgentCard(
                        agent = agent,
                        isCurrent = agent.name == state.currentAgent,
                        onClick = { viewModel.switchAgent(agent.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = IncodeTextSecondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun AgentCard(
    agent: Agent,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val agentColor = parseColor(agent.color)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = IncodeSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(agentColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = agent.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = IncodeTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = IncodePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (agent.description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = agent.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = IncodeTextSecondary
                    )
                }

                if (agent.model != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Model: ${agent.model}",
                        style = MaterialTheme.typography.labelSmall,
                        color = IncodeTextSecondary.copy(alpha = 0.7f)
                    )
                }
            }

            if (isCurrent) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Current agent",
                    tint = agentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun parseColor(colorString: String?): Color {
    if (colorString == null) return Color(0xFF757575)
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color(0xFF757575)
    }
}
