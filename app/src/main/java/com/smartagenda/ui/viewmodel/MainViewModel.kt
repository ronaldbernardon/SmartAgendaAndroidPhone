package com.smartagenda.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.data.model.DailySummary
import com.smartagenda.repository.SmartAgendaRepository
import com.smartagenda.worker.DailySyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * État de l'UI pour l'écran principal
 */
sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val summary: DailySummary) : MainUiState()
    data class Error(val message: String) : MainUiState()
    object NotConfigured : MainUiState()
}

/**
 * ViewModel principal de l'application
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SmartAgendaRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val isConfigured: Flow<Boolean> = preferencesManager.isConfiguredFlow

    init {
        viewModelScope.launch {
            preferencesManager.isConfiguredFlow.collect { configured ->
                if (configured) {
                    loadDailySummary()
                } else {
                    _uiState.value = MainUiState.NotConfigured
                }
            }
        }
    }

    fun loadDailySummary(date: LocalDate = _selectedDate.value) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            
            repository.getDailySummary(date).collect { result ->
                result.onSuccess { summary ->
                    _uiState.value = MainUiState.Success(summary)
                    _isRefreshing.value = false
                }.onFailure { error ->
                    _uiState.value = MainUiState.Error(
                        error.message ?: "Erreur de connexion. Vérifiez votre VPN."
                    )
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        loadDailySummary()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDailySummary(date)
    }

    fun getLastSyncTime(): Long {
        return runBlocking { preferencesManager.getLastSyncTimestamp() }
    }
}
