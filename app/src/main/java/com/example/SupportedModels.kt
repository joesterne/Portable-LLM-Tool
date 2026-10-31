package com.example

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

data class SupportedModel(
    val id: String,
    val settingsLabel: String,
    val keyName: String
) {
    val preferencesKey: Preferences.Key<String> get() = stringPreferencesKey(keyName)
}

// Single source of truth for the model list. It was hardcoded in four places
// (ModelSelector, SettingsViewModel, ApiSettingsScreen, and when-branches in
// SettingsRepository), so adding a model meant editing four files and the
// copies could silently drift apart. keyName values match the DataStore keys
// the app has always used, so existing saved keys are unaffected.
object SupportedModels {
    val all: List<SupportedModel> = listOf(
        SupportedModel("gemini-3.1-pro-preview", "Gemini API Key", "gemini_api_key"),
        SupportedModel("codex-davinci", "Codex API Key", "codex_api_key"),
        SupportedModel("ollama-llama3", "Ollama Endpoint/Key", "ollama_api_key"),
        SupportedModel("claude-code", "Claude API Key", "claude_api_key"),
        SupportedModel("github-copilot", "GitHub Token", "github_api_key")
    )

    val ids: List<String> get() = all.map { it.id }

    // Unknown providers fall back to the first (Gemini) key, matching the
    // repository's previous behavior.
    fun keyFor(provider: String): Preferences.Key<String> =
        (all.find { it.id == provider } ?: all.first()).preferencesKey
}
