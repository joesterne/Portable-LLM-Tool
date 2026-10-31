package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    private val _apiKeys = MutableStateFlow<Map<String, String>>(emptyMap())
    val apiKeys: StateFlow<Map<String, String>> = _apiKeys.asStateFlow()

    private val models = SupportedModels.ids

    init {
        loadKeys()
    }

    private fun loadKeys() {
        viewModelScope.launch {
            val keysMap = mutableMapOf<String, String>()
            for (model in models) {
                val key = repository.getApiKey(model).first()
                keysMap[model] = key
            }
            _apiKeys.value = keysMap
        }
    }

    fun updateApiKey(model: String, key: String) {
        viewModelScope.launch {
            repository.saveApiKey(model, key)
            val updatedKeys = _apiKeys.value.toMutableMap()
            updatedKeys[model] = key
            _apiKeys.value = updatedKeys
        }
    }
}
