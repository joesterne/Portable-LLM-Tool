package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun ModelSelector(
    activeModel: String,
    onModelSelected: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val models = listOf(
        "gemini-3.1-pro-preview",
        "codex-davinci",
        "ollama-llama3",
        "claude-code",
        "github-copilot"
    )

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { expanded = true }
                .background(DarkSurfaceHighlight, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Model: $activeModel",
                color = NeonBlue,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Model",
                tint = NeonBlue
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkSurfaceHighlight)
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = model,
                            color = if (model == activeModel) NeonPurple else TextGray,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    onClick = {
                        onModelSelected(model)
                        expanded = false
                    }
                )
            }
            HorizontalDivider(color = TextGray.copy(alpha = 0.2f))
            DropdownMenuItem(
                text = {
                    Text(
                        text = "API Settings",
                        color = TextGreen,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                onClick = {
                    expanded = false
                    onNavigateToSettings()
                }
            )
        }
    }
}
