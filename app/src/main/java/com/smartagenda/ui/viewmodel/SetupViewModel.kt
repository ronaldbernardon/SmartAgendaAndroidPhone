package com.smartagenda.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartagenda.data.api.SmartAgendaApi
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.data.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

data class SetupUiState(
    val serverUrl: String = "",
    val password: String = "",
    val notificationHour: Int = 7,
    val notificationMinute: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            preferencesManager.serverUrl.collect { url ->
                _uiState.update { it.copy(serverUrl = url) }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.password.collect { pwd ->
                _uiState.update { it.copy(password = pwd) }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.notificationHour.collect { hour ->
                _uiState.update { it.copy(notificationHour = hour) }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.notificationMinute.collect { minute ->
                _uiState.update { it.copy(notificationMinute = minute) }
            }
        }
    }
    
    fun updateServerUrl(url: String) {
        _uiState.update { it.copy(serverUrl = url) }
    }
    
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }
    
    fun updateNotificationTime(hour: Int, minute: Int) {
        _uiState.update { 
            it.copy(
                notificationHour = hour,
                notificationMinute = minute
            )
        }
    }
    
    fun testConnection(serverUrl: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // Créer une instance temporaire de Retrofit pour tester
                val retrofit = Retrofit.Builder()
                    .baseUrl(serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                
                val api = retrofit.create(SmartAgendaApi::class.java)
                
                // Tester la connexion
                val response = api.login(LoginRequest(password))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.body()?.message ?: "Mot de passe incorrect"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erreur: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun saveSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val state = _uiState.value
                
                preferencesManager.updateServerUrl(state.serverUrl)
                preferencesManager.updatePassword(state.password)
                preferencesManager.updateNotificationTime(
                    state.notificationHour,
                    state.notificationMinute
                )
                preferencesManager.updateConfigured(true)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erreur sauvegarde: ${e.message}"
                    )
                }
            }
        }
    }
}
