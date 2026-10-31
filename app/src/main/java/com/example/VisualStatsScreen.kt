package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.FloatEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualStatsScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit
) {
    val requestLogs by viewModel.requestHistory.collectAsState()
    
    val successCount = requestLogs.count { it.status == "Success" }
    val errorCount = requestLogs.count { it.status == "Error" }
    
    val successRateModel = entryModelOf(
        listOf(
            FloatEntry(0f, successCount.toFloat()),
            FloatEntry(1f, errorCount.toFloat())
        )
    )

    // Token usage over time (estimated as length / 4)
    // Reverse to show chronological order from left to right
    val tokenUsageEntries = requestLogs
        .sortedBy { it.timestamp }
        .mapIndexed { index, log ->
            val estimatedTokens = (log.command.length + log.response.length) / 4f
            FloatEntry(index.toFloat(), estimatedTokens)
        }
        .takeIf { it.isNotEmpty() } ?: listOf(FloatEntry(0f, 0f))
        
    val tokenUsageModel = entryModelOf(tokenUsageEntries)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visual Analytics", color = NeonBlue, fontFamily = FontFamily.Monospace) },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Request Status",
                    color = TextGreen,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Success: $successCount", color = TextGreen, fontFamily = FontFamily.Monospace)
                            Text("Error: $errorCount", color = MaterialTheme.colorScheme.error, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Chart(
                            chart = columnChart(),
                            model = successRateModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Estimated Token Usage Trends",
                    color = TextGreen,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Chart(
                            chart = lineChart(),
                            model = tokenUsageModel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
        }
    }
}
