package com.smartagenda.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.repository.SmartAgendaRepository
import com.smartagenda.worker.DailySyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * État de la configuration
 */
sealed class SetupState {
    object Idle : SetupState()
    object Testing : SetupState()
    object Authenticating : SetupState()
    data class Success(val message: String) : SetupState()
    data class Error(val message: String) : SetupState()
}

/**
 * ViewModel pour l'écran de configuration
 */
@HiltViewModel
class SetupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SmartAgendaRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _setupState = MutableStateFlow<SetupState>(SetupState.Idle)
    val setupState: StateFlow<SetupState> = _setupState.asStateFlow()

    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _notificationHour = MutableStateFlow(7)
    val notificationHour: StateFlow<Int> = _notificationHour.asStateFlow()

    private val _notificationMinute = MutableStateFlow(0)
    val notificationMinute: StateFlow<Int> = _notificationMinute.asStateFlow()

    init {
        // Charger les valeurs existantes si disponibles
        _serverUrl.value = preferencesManager.getServerUrl() ?: ""
        _notificationHour.value = preferencesManager.getNotificationHour()
        _notificationMinute.value = preferencesManager.getNotificationMinute()
    }

    fun setServerUrl(url: String) {
        _serverUrl.value = url
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setNotificationHour(hour: Int) {
        _notificationHour.value = hour
    }

    fun setNotificationMinute(minute: Int) {
        _notificationMinute.value = minute
    }

    /**
     * Test de connexion au serveur
     */
    fun testConnection() {
        viewModelScope.launch {
            _setupState.value = SetupState.Testing

            // Valider l'URL
            val url = _serverUrl.value.trim()
            if (!isValidUrl(url)) {
                _setupState.value = SetupState.Error("URL invalide. Format attendu: http://IP:8086")
                return@launch
            }

            // Sauvegarder temporairement l'URL pour le test
            preferencesManager.setServerUrl(url)

            repository.testConnection().fold(
                onSuccess = {
                    _setupState.value = SetupState.Success("✅ Connexion réussie au serveur !")
                },
                onFailure = { error ->
                    _setupState.value = SetupState.Error(
                        "❌ Impossible de se connecter. Vérifiez:\n" +
                        "• Votre connexion VPN\n" +
                        "• L'URL du serveur\n" +
                        "• Que SmartAgenda est démarré\n\n" +
                        "Erreur: ${error.message}"
                    )
                }
            )
        }
    }

    /**
     * Sauvegarde la configuration complète
     */
    fun saveConfiguration() {
        viewModelScope.launch {
            val url = _serverUrl.value.trim()
            val pwd = _password.value

            // Validations
            if (!isValidUrl(url)) {
                _setupState.value = SetupState.Error("URL invalide")
                return@launch
            }

            if (pwd.length < 8) {
                _setupState.value = SetupState.Error("Le mot de passe doit contenir au moins 8 caractères")
                return@launch
            }

            _setupState.value = SetupState.Authenticating

            // Sauvegarder l'URL
            preferencesManager.setServerUrl(url)

            // Tenter l'authentification
            repository.login(pwd).fold(
                onSuccess = {
                    // Sauvegarder les préférences
                    preferencesManager.setPassword(pwd)
                    preferencesManager.setNotificationHour(_notificationHour.value)
                    preferencesManager.setNotificationMinute(_notificationMinute.value)
                    preferencesManager.setNotificationsEnabled(true)

                    // Planifier les synchronisations quotidiennes
                    WorkManagerScheduler.scheduleDailySync(context, hour, minute)
                        context,
                        _notificationHour.value,
                        _notificationMinute.value
                    )

                    _setupState.value = SetupState.Success("✅ Configuration enregistrée !")
                },
                onFailure = { error ->
                    _setupState.value = SetupState.Error(
                        "❌ Authentification échouée\n${error.message}"
                    )
                }
            )
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return url.matches(Regex("^https?://[\\w\\d.-]+(:\\d+)?/?$"))
    }

    fun resetState() {
        _setupState.value = SetupState.Idle
    }
}
