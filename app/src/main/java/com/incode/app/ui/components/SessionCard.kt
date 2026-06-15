package com.incode.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.incode.app.data.model.Session
import com.incode.app.ui.theme.IncodeOutline
import com.incode.app.ui.theme.IncodePrimary
import com.incode.app.ui.theme.IncodeSurface
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary

@Composable
fun SessionCard(
    session: Session,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = IncodeSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.dp, top = 0.dp, bottom = 0.dp, end = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Left accent bar
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(40.dp)
                        .background(
                            color = IncodePrimary,
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Title
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = IncodeTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Slug and time
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = session.slug,
                            style = MaterialTheme.typography.bodySmall,
                            color = IncodeTextSecondary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.bodySmall,
                            color = IncodeTextSecondary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = session.formatTimeAgo(),
                            style = MaterialTheme.typography.bodySmall,
                            color = IncodeTextSecondary
                        )
                    }

                    // Agent and model
                    if (session.agent != null || session.model != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (session.agent != null) {
                                Text(
                                    text = session.agent,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IncodePrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (session.model != null) {
                                if (session.agent != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "·",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IncodeTextSecondary.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = session.model,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IncodeTextSecondary
                                )
                            }
                        }
                    }
                }

                // Stats column
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    if (session.messageCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Messages",
                                tint = IncodeTextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${session.messageCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = IncodeTextSecondary
                            )
                        }
                    }
                    if (session.cost > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$${String.format("%.4f", session.cost)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = IncodeTextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
