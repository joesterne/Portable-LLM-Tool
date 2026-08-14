package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.List
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToLogs: () -> Unit = {},
    onNavigateToPromptManager: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LLM Controller", color = NeonBlue, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerminalDarker,
                    titleContentColor = NeonBlue
                ),
                actions = {
                    IconButton(onClick = onNavigateToPromptManager) {
                        Icon(imageVector = Icons.Default.Bookmarks, contentDescription = "Prompt Manager", tint = NeonPurple)
                    }
                    IconButton(onClick = onNavigateToLogs) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "View Logs", tint = NeonBlue)
                    }
                    Button(
                        onClick = { viewModel.connect() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(if (state.connectionStatus.contains("Connected")) "Connected" else "Connect")
                    }
                }
            )
        },
        containerColor = TerminalDarker
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${state.connectionStatus}",
                    color = if (state.connectionStatus.contains("Connected")) TextGreen else TextGray,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall
                )
                ModelSelector(
                    activeModel = state.activeModel,
                    onModelSelected = { viewModel.setActiveModel(it) },
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terminal Output Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(TerminalDark, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false
                ) {
                    items(state.logs) { log ->
                        Text(
                            text = log,
                            color = if (log.startsWith(">")) NeonBlue else if (log.startsWith("[ERROR]")) Color.Red else TextGreen,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    if (state.isLoading) {
                        item {
                            CircularProgressIndicator(
                                color = NeonPurple,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Command Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.inputText,
                    onValueChange = { viewModel.setInputText(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("> Enter command...", color = TextGray, fontFamily = FontFamily.Monospace) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceHighlight,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = NeonBlue,
                        unfocusedBorderColor = DarkSurfaceHighlight,
                        focusedTextColor = TextGreen,
                        unfocusedTextColor = TextGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.sendCommand() },
                    modifier = Modifier
                        .background(NeonPurple, RoundedCornerShape(8.dp))
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Command",
                        tint = TerminalDarker
                    )
                }
            }
        }
    }
}
