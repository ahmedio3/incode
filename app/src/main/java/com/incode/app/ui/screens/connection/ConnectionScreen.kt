package com.incode.app.ui.screens.connection

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.incode.app.ui.theme.IncodeBackground
import com.incode.app.ui.theme.IncodeError
import com.incode.app.ui.theme.IncodeInputField
import com.incode.app.ui.theme.IncodeOutline
import com.incode.app.ui.theme.IncodePrimary
import com.incode.app.ui.theme.IncodeSuccess
import com.incode.app.ui.theme.IncodeSurface
import com.incode.app.ui.theme.IncodeTextPrimary
import com.incode.app.ui.theme.IncodeTextSecondary

@Composable
fun ConnectionScreen(
    onConnected: () -> Unit,
    initialConfig: ConnectionUiState? = null,
    viewModel: ConnectionViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Pre-fill from initial config if provided
    LaunchedEffect(initialConfig) {
        if (initialConfig != null) {
            viewModel.initFromConfig(
                com.incode.app.data.model.ServerConfig(
                    host = initialConfig.host,
                    port = initialConfig.port.toIntOrNull() ?: 4096,
                    username = initialConfig.username,
                    password = initialConfig.password
                )
            )
        }
    }

    // Navigate when connected
    LaunchedEffect(state.isConnected) {
        if (state.isConnected) {
            onConnected()
        }
    }

    var showAdvanced by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IncodeBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo section
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                tint = IncodePrimary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "incode",
                style = MaterialTheme.typography.displayLarge,
                color = IncodeTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "OpenCode Mobile Client",
                style = MaterialTheme.typography.bodyLarge,
                color = IncodeTextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Connection form
            OutlinedTextField(
                value = state.host,
                onValueChange = { viewModel.updateHost(it) },
                label = { Text("Host") },
                placeholder = { Text("127.0.0.1") },
                singleLine = true,
                enabled = !state.isConnecting,
                colors = textFieldColors(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.port,
                onValueChange = { viewModel.updatePort(it) },
                label = { Text("Port") },
                placeholder = { Text("4096") },
                singleLine = true,
                enabled = !state.isConnecting,
                colors = textFieldColors(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Advanced settings toggle
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showAdvanced = !showAdvanced }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Advanced",
                        tint = IncodeTextSecondary
                    )
                }
            }

            // Advanced fields
            AnimatedVisibility(
                visible = showAdvanced,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = { viewModel.updateUsername(it) },
                        label = { Text("Username (optional)") },
                        singleLine = true,
                        enabled = !state.isConnecting,
                        colors = textFieldColors(),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = IncodeTextSecondary)
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { Text("Password (optional)") },
                        singleLine = true,
                        enabled = !state.isConnecting,
                        colors = textFieldColors(),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = IncodeTextSecondary)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = state.error ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IncodeError,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // Connect button
            Button(
                onClick = { viewModel.connect() },
                enabled = !state.isConnecting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IncodePrimary
                )
            ) {
                if (state.isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = IncodeTextPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Connecting...")
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connect", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = IncodeTextPrimary,
    unfocusedTextColor = IncodeTextPrimary,
    cursorColor = IncodePrimary,
    focusedBorderColor = IncodePrimary,
    unfocusedBorderColor = IncodeOutline,
    focusedLabelColor = IncodePrimary,
    unfocusedLabelColor = IncodeTextSecondary,
    focusedContainerColor = IncodeInputField,
    unfocusedContainerColor = IncodeInputField
)
