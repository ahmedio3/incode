package com.incode.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary
import com.incode.app.ui.theme.IncodeToolBg
import com.incode.app.ui.theme.IncodeToolBorder
import com.incode.app.ui.theme.CodeTextStyle

@Composable
fun ToolCallCard(
    toolName: String,
    args: String? = null,
    result: String? = null,
    isRunning: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val icon = getToolIcon(toolName)
    val iconColor = getToolColor(toolName)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(IncodeToolBg)
                .clickable { isExpanded = !isExpanded }
                .padding(12.dp)
        ) {
            Column {
                // Header row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = toolName,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = toolName,
                        style = MaterialTheme.typography.labelLarge,
                        color = iconColor,
                        fontWeight = FontWeight.Medium
                    )
                    if (isRunning) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "●",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = IncodeTextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (isExpanded) 90f else 0f)
                    )
                }

                // Args (always visible if not expanded)
                if (args != null && !isExpanded) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = args.take(100) + if (args.length > 100) "..." else "",
                        style = CodeTextStyle,
                        color = IncodeTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Expanded content
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        if (args != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Arguments:",
                                style = MaterialTheme.typography.labelSmall,
                                color = IncodeTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = args,
                                style = CodeTextStyle,
                                color = IncodeTextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (result != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Result:",
                                style = MaterialTheme.typography.labelSmall,
                                color = IncodeTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result,
                                style = CodeTextStyle,
                                color = IncodeTextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getToolIcon(toolName: String): ImageVector {
    return when (toolName.lowercase()) {
        "bash" -> Icons.Default.Terminal
        "read" -> Icons.Default.Description
        "write" -> Icons.Default.Edit
        "edit" -> Icons.Default.Edit
        "glob" -> Icons.Default.Folder
        "grep" -> Icons.Default.Search
        "webfetch" -> Icons.Default.FindInPage
        else -> Icons.Default.Code
    }
}

private fun getToolColor(toolName: String): Color {
    return when (toolName.lowercase()) {
        "bash" -> Color(0xFF4CAF50)       // Green
        "read" -> Color(0xFF42A5F5)       // Blue
        "write" -> Color(0xFFFFB74D)      // Orange
        "edit" -> Color(0xFFAB47BC)       // Purple
        "glob" -> Color(0xFF26C6DA)       // Cyan
        "grep" -> Color(0xFFEF5350)       // Red
        "webfetch" -> Color(0xFF66BB6A)   // Light Green
        "websearch" -> Color(0xFF42A5F5)  // Blue
        "task" -> Color(0xFFFFA726)       // Orange
        else -> IncodeTextSecondary
    }
}

private fun Modifier.rotate(degrees: Float): Modifier = this.then(
    androidx.compose.ui.draw.rotate(degrees)
)
