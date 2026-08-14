package com.example

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptManagerScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit,
    onPromptSelected: (String) -> Unit
) {
    val prompts by viewModel.savedPrompts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingPrompt by remember { mutableStateOf<SavedPrompt?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prompt Manager", color = NeonBlue, fontFamily = FontFamily.Monospace) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingPrompt = null
                    showDialog = true 
                },
                containerColor = NeonPurple,
                contentColor = TerminalDarker
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Prompt")
            }
        },
        containerColor = TerminalDarker
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(prompts) { prompt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            onPromptSelected(prompt.content)
                            onNavigateBack()
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prompt.title,
                                    color = NeonBlue,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Model: ${prompt.targetModel}",
                                    color = NeonPurple,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Row {
                                IconButton(onClick = { 
                                    editingPrompt = prompt
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextGray)
                                }
                                IconButton(onClick = { viewModel.deletePrompt(prompt) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = prompt.content,
                            color = TextGray,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3
                        )
                    }
                }
            }
            if (prompts.isEmpty()) {
                item {
                    Text(
                        text = "No saved prompts yet. Click the + button to add one.",
                        color = TextGray,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        if (showDialog) {
            PromptDialog(
                prompt = editingPrompt,
                onDismiss = { showDialog = false },
                onSave = { title, content, targetModel ->
                    viewModel.savePrompt(title, content, targetModel, editingPrompt?.id ?: 0)
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptDialog(
    prompt: SavedPrompt?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(prompt?.title ?: "") }
    var content by remember { mutableStateOf(prompt?.content ?: "") }
    var targetModel by remember { mutableStateOf(prompt?.targetModel ?: "gemini-3.1-pro-preview") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (prompt == null) "New Prompt" else "Edit Prompt",
                color = NeonBlue,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextGreen,
                        unfocusedTextColor = TextGreen,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = TextGray,
                        cursorColor = NeonBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetModel,
                    onValueChange = { targetModel = it },
                    label = { Text("Target Model", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextGreen,
                        unfocusedTextColor = TextGreen,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = TextGray,
                        cursorColor = NeonBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Prompt Content", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextGreen,
                        unfocusedTextColor = TextGreen,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = TextGray,
                        cursorColor = NeonBlue
                    ),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, content, targetModel)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Text("Save", color = TerminalDarker)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
            }
        }
    )
}
