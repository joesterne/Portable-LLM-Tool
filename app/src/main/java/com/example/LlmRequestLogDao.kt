package com.example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LlmRequestLogDao {
    @Query("SELECT * FROM llm_request_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LlmRequestLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LlmRequestLog)

    @Query("DELETE FROM llm_request_logs")
    suspend fun clearLogs()
}
