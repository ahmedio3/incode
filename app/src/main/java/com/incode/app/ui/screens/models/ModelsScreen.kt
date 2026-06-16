package com.incode.app.ui.screens.models

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.incode.app.data.model.ModelInfo
import com.incode.app.data.model.Provider
import com.incode.app.ui.theme.IncodeBackground
import com.incode.app.ui.theme.IncodeInputField
import com.incode.app.ui.theme.IncodeOutline
import com.incode.app.ui.theme.IncodePrimary
import com.incode.app.ui.theme.IncodeSurface
import com.incode.app.ui.theme.IncodeSurfaceHigh
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary
import com.incode.app.ui.theme.IncodeTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen(
    viewModel: ModelsViewModel = viewModel()
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
                    "Models",
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
            placeholder = { Text("Search models...", color = IncodeTextSecondary) },
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

        // Current model indicator
        if (state.currentModel != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = IncodePrimary.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = null,
                        tint = IncodePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current: ${state.currentModel}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IncodePrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Providers list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(
                items = state.providers,
                key = { it.id }
            ) { provider ->
                ProviderSection(
                    provider = provider,
                    currentModel = state.currentModel,
                    searchQuery = state.searchQuery,
                    onModelSelect = { viewModel.selectModel(it) }
                )
            }
        }
    }
}

@Composable
private fun ProviderSection(
    provider: Provider,
    currentModel: String?,
    searchQuery: String,
    onModelSelect: (ModelInfo) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val filteredModels = if (searchQuery.isBlank()) provider.models
    else provider.models.filter {
        it.name?.contains(searchQuery, ignoreCase = true) == true ||
        it.id.contains(searchQuery, ignoreCase = true)
    }

    if (filteredModels.isEmpty() && searchQuery.isNotBlank()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .animateContentSize()
    ) {
        // Provider header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .background(IncodeSurfaceHigh, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                tint = IncodeTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = provider.name ?: provider.id,
                style = MaterialTheme.typography.titleSmall,
                color = IncodeTextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${provider.models.size} models",
                style = MaterialTheme.typography.labelSmall,
                color = IncodeTextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = IncodeTextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(if (isExpanded) 90f else 0f)
            )
        }

        // Models list
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            ) {
                filteredModels.forEach { model ->
                    ModelRow(
                        model = model,
                        isSelected = currentModel == "${model.providerId}/${model.id}",
                        onClick = { onModelSelect(model) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelRow(
    model: ModelInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            contentDescription = if (isSelected) "Selected" else "Not selected",
            tint = if (isSelected) IncodePrimary else IncodeTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = model.name ?: model.id,
                style = MaterialTheme.typography.bodyMedium,
                color = IncodeTextPrimary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
            Text(
                text = model.id,
                style = MaterialTheme.typography.labelSmall,
                color = IncodeTextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}


