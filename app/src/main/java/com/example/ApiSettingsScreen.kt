package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val apiKeys by viewModel.apiKeys.collectAsState()

    val models = listOf(
        "gemini-3.1-pro-preview" to "Gemini API Key",
        "codex-davinci" to "Codex API Key",
        "ollama-llama3" to "Ollama Endpoint/Key",
        "claude-code" to "Claude API Key",
        "github-copilot" to "GitHub Token"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Settings", color = NeonBlue, fontFamily = FontFamily.Monospace) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerminalDarker
                )
            )
        },
        containerColor = TerminalDarker
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Configure API Keys",
                    color = TextGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Keys are securely stored in local DataStore.",
                    color = TextGray,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }
            items(models) { (modelId, label) ->
                var isVisible by remember { mutableStateOf(false) }
                val currentKey = apiKeys[modelId] ?: ""

                OutlinedTextField(
                    value = currentKey,
                    onValueChange = { viewModel.updateApiKey(modelId, it) },
                    label = { Text(label, color = TextGray, fontFamily = FontFamily.Monospace) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceHighlight,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = DarkSurfaceHighlight,
                        focusedTextColor = TextGreen,
                        unfocusedTextColor = TextGreen,
                        cursorColor = NeonBlue
                    ),
                    visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isVisible = !isVisible }) {
                            Icon(
                                imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility",
                                tint = TextGray
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
                )
            }
        }
    }
}
