package com.smartagenda.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.data.model.DailySummary
import com.smartagenda.data.repository.SmartAgendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val dailySummary: DailySummary? = null,
    val error: String? = null,
    val currentDate: String = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(Date())
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SmartAgendaRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        Log.d("MainViewModel", "ViewModel créé - chargement initial des données")
        loadDailySummary()
    }
    
    fun loadDailySummary(date: String = SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH).format(Date())) {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Chargement du résumé quotidien pour: $date")
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Récupérer le résumé en une seule fois (pas de collect infini)
                repository.getDailySummary(date).first().fold(
                    onSuccess = { summary ->
                        Log.d("MainViewModel", "Résumé chargé: ${summary.totalEvents} événements")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                dailySummary = summary,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e("MainViewModel", "Erreur chargement: ${error.message}")
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Erreur de chargement"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception: ${e.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Erreur: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun refresh() {
        Log.d("MainViewModel", "Actualisation manuelle demandée")
        loadDailySummary()
    }
}
