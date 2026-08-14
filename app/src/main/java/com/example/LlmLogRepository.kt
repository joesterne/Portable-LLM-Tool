package com.example

import kotlinx.coroutines.flow.Flow

class LlmLogRepository(private val dao: LlmRequestLogDao) {
    val allLogs: Flow<List<LlmRequestLog>> = dao.getAllLogs()

    suspend fun insertLog(log: LlmRequestLog) {
        dao.insertLog(log)
    }

    suspend fun clearLogs() {
        dao.clearLogs()
    }
}
