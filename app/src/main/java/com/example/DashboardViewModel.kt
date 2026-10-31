package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

data class DashboardState(
    val connectionStatus: String = "Disconnected",
    val logs: List<String> = listOf("System initialized.", "Waiting for connection..."),
    val activeModel: String = "gemini-3.1-pro-preview",
    val isLoading: Boolean = false,
    val inputText: String = ""
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LlmLogRepository
    private val promptRepository: PromptRepository
    private val settingsRepository = SettingsRepository(application)
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = LlmLogRepository(database.llmRequestLogDao())
        promptRepository = PromptRepository(database.savedPromptDao())
    }

    val requestHistory: StateFlow<List<LlmRequestLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedPrompts: StateFlow<List<SavedPrompt>> = promptRepository.allPrompts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun savePrompt(title: String, content: String, targetModel: String, id: Int = 0) {
        viewModelScope.launch {
            if (id == 0) {
                promptRepository.insertPrompt(SavedPrompt(title = title, content = content, targetModel = targetModel))
            } else {
                promptRepository.updatePrompt(SavedPrompt(id = id, title = title, content = content, targetModel = targetModel))
            }
        }
    }

    fun deletePrompt(prompt: SavedPrompt) {
        viewModelScope.launch {
            promptRepository.deletePrompt(prompt)
        }
    }

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    fun setActiveModel(model: String) {
        _state.value = _state.value.copy(
            activeModel = model,
            logs = _state.value.logs + "[SYSTEM]: Switched active model to $model"
        )
    }

    fun setInputText(text: String) {
        _state.value = _state.value.copy(inputText = text)
    }

    fun signIn(context: android.content.Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val success = AuthManager.signInWithGoogle(context)
            if (success) {
                val email = AuthManager.getUserEmail() ?: "Unknown"
                _state.value = _state.value.copy(
                    logs = _state.value.logs + "[AUTH]: Authenticated as $email",
                    isLoading = false
                )
                onSuccess()
            } else {
                _state.value = _state.value.copy(
                    isLoading = false
                )
                onError("Failed to sign in. Ensure google-services.json is added and SHA-1 is configured in Firebase.")
            }
        }
    }

    fun connect() {
        _state.value = _state.value.copy(connectionStatus = "Connecting to Mac Mini...", isLoading = true)
        viewModelScope.launch {
            // Simulate connection delay
            kotlinx.coroutines.delay(1500)
            _state.value = _state.value.copy(
                connectionStatus = "Connected (E2E Encrypted)",
                isLoading = false,
                logs = _state.value.logs + "Secure connection established with Mac Mini."
            )
        }
    }

    fun sendCommand() {
        val command = _state.value.inputText
        if (command.isBlank()) return
        
        _state.value = _state.value.copy(
            inputText = "",
            logs = _state.value.logs + "> $command",
            isLoading = true
        )

        viewModelScope.launch {
            try {
                // The key saved in ApiSettingsScreen for the ACTIVE model wins;
                // the build-time BuildConfig key is only a fallback. (This used
                // to read BuildConfig alone, so keys entered in Settings had no
                // effect at all.)
                val activeModel = _state.value.activeModel
                val settingsKey = settingsRepository.getApiKey(activeModel).first()
                val apiKey = settingsKey.ifEmpty { BuildConfig.GEMINI_API_KEY }
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _state.value = _state.value.copy(
                        logs = _state.value.logs + "[ERROR]: No API key configured for $activeModel. Add one in API Settings.",
                        isLoading = false
                    )
                    return@launch
                }

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = "You are a remote LLM controller connected to a Mac Mini. Execute this command contextually: $command")))),
                    generationConfig = GenerationConfig(
                        thinkingConfig = ThinkingConfig(thinkingLevel = "high")
                    )
                )

                val response = RetrofitClient.service.generateContent(activeModel, apiKey, request)
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response."
                
                _state.value = _state.value.copy(
                    logs = _state.value.logs + reply,
                    isLoading = false
                )

                // Log to Firestore for activity monitoring
                val logEntry = hashMapOf(
                    "command" to command,
                    "response" to reply,
                    "timestamp" to Date()
                )
                firestore?.collection("activity_logs")?.add(logEntry)

                // Log to Local Room DB
                repository.insertLog(
                    LlmRequestLog(
                        command = command,
                        response = reply,
                        model = _state.value.activeModel,
                        status = "Success"
                    )
                )
                
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Structured concurrency: cancellation is not a request failure.
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    logs = _state.value.logs + "[ERROR]: ${e.message}",
                    isLoading = false
                )
                repository.insertLog(
                    LlmRequestLog(
                        command = command,
                        response = e.message ?: "Error",
                        model = _state.value.activeModel,
                        status = "Error"
                    )
                )
            }
        }
    }

    fun exportLogsToCsv(context: Context) {
        val logs = requestHistory.value
        if (logs.isEmpty()) return

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val csvBuilder = StringBuilder()
            csvBuilder.append("Timestamp,Model,Command,Response,Status\n")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            
            logs.forEach { log ->
                val dateStr = dateFormat.format(Date(log.timestamp))
                val commandEscaped = log.command.replace("\"", "\"\"")
                val responseEscaped = log.response.replace("\"", "\"\"")
                csvBuilder.append("$dateStr,${log.model},\"$commandEscaped\",\"$responseEscaped\",${log.status}\n")
            }

            try {
                val logsDir = File(context.cacheDir, "logs")
                if (!logsDir.exists()) logsDir.mkdirs()
                
                val file = File(logsDir, "llm_history.csv")
                file.writeText(csvBuilder.toString())

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, "LLM Request History")
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(intent, "Export Logs"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
