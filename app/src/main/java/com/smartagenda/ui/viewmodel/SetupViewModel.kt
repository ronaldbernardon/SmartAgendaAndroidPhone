package com.smartagenda.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.repository.SmartAgendaRepository
import com.smartagenda.worker.WorkManagerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val serverUrl: String = "",
    val password: String = "",
    val notificationHour: Int = 7,
    val notificationMinute: Int = 0,
    val isLoading: Boolean = false,
    val isTestingConnection: Boolean = false,
    val connectionTestResult: String? = null,
    val errorMessage: String? = null,
    val isConfigured: Boolean = false
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SmartAgendaRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.isConfigured.collect { isConfigured ->
                _uiState.update { it.copy(isConfigured = isConfigured) }
            }
        }
    }

    fun updateServerUrl(url: String) {
        _uiState.update { it.copy(serverUrl = url, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun updateNotificationTime(hour: Int, minute: Int) {
        _uiState.update { 
            it.copy(
                notificationHour = hour,
                notificationMinute = minute,
                errorMessage = null
            )
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnection = true, connectionTestResult = null, errorMessage = null) }
            
            try {
                val url = _uiState.value.serverUrl
                val password = _uiState.value.password
                
                if (url.isBlank() || password.isBlank()) {
                    _uiState.update { 
                        it.copy(
                            isTestingConnection = false,
                            errorMessage = "Veuillez remplir tous les champs"
                        )
                    }
                    return@launch
                }
                
                val result = repository.testConnection(url, password)
                
                result.onSuccess {
                    _uiState.update { 
                        it.copy(
                            isTestingConnection = false,
                            connectionTestResult = "Connexion reussie"
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isTestingConnection = false,
                            errorMessage = "Erreur : ${error.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isTestingConnection = false,
                        errorMessage = "Erreur : ${e.message}"
                    )
                }
            }
        }
    }

    fun saveConfiguration() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val url = _uiState.value.serverUrl
                val password = _uiState.value.password
                val hour = _uiState.value.notificationHour
                val minute = _uiState.value.notificationMinute
                
                if (url.isBlank()) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "URL du serveur requise"
                        )
                    }
                    return@launch
                }
                
                if (password.length < 8) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Mot de passe trop court"
                        )
                    }
                    return@launch
                }
                
                preferencesManager.saveServerUrl(url)
                preferencesManager.savePassword(password)
                preferencesManager.saveNotificationTime(hour, minute)
                preferencesManager.setConfigured(true)
                
                WorkManagerScheduler.scheduleDailySync(context, hour, minute)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isConfigured = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erreur : ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearConnectionTestResult() {
        _uiState.update { it.copy(connectionTestResult = null) }
    }
}
