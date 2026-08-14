package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_request_logs")
data class LlmRequestLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val command: String,
    val response: String,
    val model: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)
