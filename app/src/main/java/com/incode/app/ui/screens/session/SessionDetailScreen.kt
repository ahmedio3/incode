package com.incode.app.ui.screens.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.incode.app.ui.theme.IncodeBackground
import com.incode.app.ui.theme.IncodeError
import com.incode.app.ui.theme.IncodePrimary
import com.incode.app.ui.theme.IncodeSurface
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary
import com.incode.app.ui.theme.IncodeTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    sessionId: String,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: SessionDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        // ViewModel already initialized
    }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onDeleted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IncodeBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Session Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = IncodeTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = IncodeTopBar
            )
        )

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = IncodePrimary)
            }
        } else if (state.session != null) {
            val session = state.session!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = IncodeTextPrimary,
                    fontWeight = FontWeight.Bold
                )

                if (session.slug.isNotBlank()) {
                    Text(
                        text = "@${session.slug}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IncodeTextSecondary
                    )
                }

                // Stats card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = IncodeSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        StatRow(
                            icon = Icons.Default.Schedule,
                            label = "Created",
                            value = formatDate(session.timeCreated)
                        )
                        StatRow(
                            icon = Icons.Default.Schedule,
                            label = "Updated",
                            value = formatDate(session.timeUpdated)
                        )
                        StatRow(
                            icon = Icons.Default.Chat,
                            label = "Messages",
                            value = "${session.messageCount}"
                        )
                        if (session.agent != null) {
                            StatRow(
                                icon = Icons.Default.Token,
                                label = "Agent",
                                value = session.agent
                            )
                        }
                        if (session.model != null) {
                            StatRow(
                                icon = Icons.Default.Token,
                                label = "Model",
                                value = session.model
                            )
                        }
                    }
                }

                // Token stats
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = IncodeSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Token Usage",
                            style = MaterialTheme.typography.titleSmall,
                            color = IncodeTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StatRow(null, "Input", formatTokens(session.tokensInput))
                        StatRow(null, "Output", formatTokens(session.tokensOutput))
                        StatRow(null, "Reasoning", formatTokens(session.tokensReasoning))
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = IncodeTextSecondary.copy(alpha = 0.2f)
                        )
                        StatRow(
                            icon = Icons.Default.AttachMoney,
                            label = "Cost",
                            value = "$${String.format("%.6f", session.cost)}"
                        )
                    }
                }

                // Actions
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.titleSmall,
                    color = IncodeTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = { viewModel.deleteSession() },
                    enabled = !state.isDeleting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IncodeError
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = IncodeTextPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Session")
                }

                AnimatedVisibility(
                    visible = state.deleteError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = state.deleteError ?: "",
                        color = IncodeError,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    icon: ImageVector? = null,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IncodeTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = IncodeTextSecondary
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = IncodeTextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

private fun formatTokens(count: Int): String {
    return when {
        count >= 1000000 -> String.format("%.1fM", count / 1000000.0)
        count >= 1000 -> String.format("%.1fK", count / 1000.0)
        else -> "$count"
    }
}
