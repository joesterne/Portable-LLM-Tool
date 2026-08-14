package com.example

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPromptDao {
    @Query("SELECT * FROM saved_prompts ORDER BY title ASC")
    fun getAllPrompts(): Flow<List<SavedPrompt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: SavedPrompt)

    @Update
    suspend fun updatePrompt(prompt: SavedPrompt)

    @Delete
    suspend fun deletePrompt(prompt: SavedPrompt)
}
