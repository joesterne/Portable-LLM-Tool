package com.example

import kotlinx.coroutines.flow.Flow

class PromptRepository(private val dao: SavedPromptDao) {
    val allPrompts: Flow<List<SavedPrompt>> = dao.getAllPrompts()

    suspend fun insertPrompt(prompt: SavedPrompt) {
        dao.insertPrompt(prompt)
    }

    suspend fun updatePrompt(prompt: SavedPrompt) {
        dao.updatePrompt(prompt)
    }

    suspend fun deletePrompt(prompt: SavedPrompt) {
        dao.deletePrompt(prompt)
    }
}
