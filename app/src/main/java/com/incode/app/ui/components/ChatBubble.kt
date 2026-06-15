package com.incode.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incode.app.ui.theme.IncodeAIBubble
import com.incode.app.ui.theme.IncodeAIBubbleBorder
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary
import com.incode.app.ui.theme.IncodeUserBubble
import com.incode.app.ui.theme.IncodeUserBubbleBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubble(
    text: String,
    isUser: Boolean,
    timestamp: Long? = null,
    agent: String? = null,
    showTimestamp: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isUser) IncodeUserBubble else IncodeAIBubble
    val borderColor = if (isUser) IncodeUserBubbleBorder else IncodeAIBubbleBorder
    val textColor = IncodeTextPrimary
    val alignment = if (isUser) Arrangement.End else Arrangement.Start
    val shape = if (isUser) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 4.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .animateContentSize(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Agent name label
        if (!isUser && agent != null) {
            Text(
                text = agent,
                style = MaterialTheme.typography.labelSmall,
                color = IncodeTextSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }

        Row(
            horizontalArrangement = alignment,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                Spacer(modifier = Modifier.width(40.dp))
            }

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(backgroundColor)
                    .then(
                        if (!isUser) {
                            Modifier
                        } else {
                            Modifier
                        }
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )

                    if (showTimestamp && timestamp != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatTimestamp(timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = IncodeTextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(40.dp))
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}
