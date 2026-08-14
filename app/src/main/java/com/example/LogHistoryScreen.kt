package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Share
import java.text.SimpleDateFormat
import java.util.*
import com.example.ui.theme.*

import androidx.compose.material.icons.filled.PieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogHistoryScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToStats: () -> Unit = {}
) {
    val requestLogs by viewModel.requestHistory.collectAsState()
    val dateFormat = SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault())
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Logs", color = NeonBlue, fontFamily = FontFamily.Monospace) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonBlue)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(imageVector = Icons.Default.PieChart, contentDescription = "Visual Stats", tint = NeonBlue)
                    }
                    IconButton(onClick = { viewModel.exportLogsToCsv(context) }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Export CSV", tint = NeonBlue)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(requestLogs) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = log.model,
                                color = NeonPurple,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = dateFormat.format(Date(log.timestamp)),
                                color = TextGray,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "> ${log.command}",
                            color = NeonBlue,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = log.response,
                            color = if (log.status == "Error") MaterialTheme.colorScheme.error else TextGreen,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            if (requestLogs.isEmpty()) {
                item {
                    Text(
                        text = "No logs available yet.",
                        color = TextGray,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
