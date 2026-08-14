package com.example

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "api_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val GEMINI_KEY = stringPreferencesKey("gemini_api_key")
        val CODEX_KEY = stringPreferencesKey("codex_api_key")
        val OLLAMA_KEY = stringPreferencesKey("ollama_api_key")
        val CLAUDE_KEY = stringPreferencesKey("claude_api_key")
        val GITHUB_KEY = stringPreferencesKey("github_api_key")
    }

    fun getApiKey(provider: String): Flow<String> {
        val key = when (provider) {
            "gemini-3.1-pro-preview" -> GEMINI_KEY
            "codex-davinci" -> CODEX_KEY
            "ollama-llama3" -> OLLAMA_KEY
            "claude-code" -> CLAUDE_KEY
            "github-copilot" -> GITHUB_KEY
            else -> GEMINI_KEY
        }
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }
    }

    suspend fun saveApiKey(provider: String, apiKey: String) {
        val key = when (provider) {
            "gemini-3.1-pro-preview" -> GEMINI_KEY
            "codex-davinci" -> CODEX_KEY
            "ollama-llama3" -> OLLAMA_KEY
            "claude-code" -> CLAUDE_KEY
            "github-copilot" -> GITHUB_KEY
            else -> return
        }
        context.dataStore.edit { preferences ->
            preferences[key] = apiKey
        }
    }
}
