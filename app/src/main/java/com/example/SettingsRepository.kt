package com.example

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "api_settings")

class SettingsRepository(private val context: Context) {

    fun getApiKey(provider: String): Flow<String> {
        val key = SupportedModels.keyFor(provider)
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }
    }

    suspend fun saveApiKey(provider: String, apiKey: String) {
        // Unknown providers are ignored, as before.
        if (SupportedModels.all.none { it.id == provider }) return
        val key = SupportedModels.keyFor(provider)
        context.dataStore.edit { preferences ->
            preferences[key] = apiKey
        }
    }
}
