package com.smartagenda.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestionnaire des préférences de l'application avec encryption
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "smartagenda_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val normalPrefs = context.getSharedPreferences("smartagenda_prefs", Context.MODE_PRIVATE)

    // Flow pour observer les changements
    private val _serverUrlFlow = MutableStateFlow(getServerUrl())
    val serverUrlFlow: Flow<String?> = _serverUrlFlow.asStateFlow()

    private val _isConfiguredFlow = MutableStateFlow(isConfigured())
    val isConfiguredFlow: Flow<Boolean> = _isConfiguredFlow.asStateFlow()

    /**
     * URL du serveur
     */
    fun getServerUrl(): String? = normalPrefs.getString(KEY_SERVER_URL, null)

    fun setServerUrl(url: String) {
        normalPrefs.edit().putString(KEY_SERVER_URL, url).apply()
        _serverUrlFlow.value = url
        updateConfiguredState()
    }

    /**
     * Mot de passe (stocké de manière sécurisée)
     */
    fun getPassword(): String? = encryptedPrefs.getString(KEY_PASSWORD, null)

    fun setPassword(password: String) {
        encryptedPrefs.edit().putString(KEY_PASSWORD, password).apply()
        updateConfiguredState()
    }

    fun clearPassword() {
        encryptedPrefs.edit().remove(KEY_PASSWORD).apply()
        updateConfiguredState()
    }

    /**
     * Heure de notification (défaut: 7h00)
     */
    fun getNotificationHour(): Int = normalPrefs.getInt(KEY_NOTIFICATION_HOUR, 7)

    fun setNotificationHour(hour: Int) {
        normalPrefs.edit().putInt(KEY_NOTIFICATION_HOUR, hour).apply()
    }

    fun getNotificationMinute(): Int = normalPrefs.getInt(KEY_NOTIFICATION_MINUTE, 0)

    fun setNotificationMinute(minute: Int) {
        normalPrefs.edit().putInt(KEY_NOTIFICATION_MINUTE, minute).apply()
    }

    /**
     * Activation des notifications
     */
    fun isNotificationsEnabled(): Boolean = 
        normalPrefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

    fun setNotificationsEnabled(enabled: Boolean) {
        normalPrefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    /**
     * Thème (clair/sombre/auto)
     */
    fun getTheme(): String = normalPrefs.getString(KEY_THEME, THEME_AUTO) ?: THEME_AUTO

    fun setTheme(theme: String) {
        normalPrefs.edit().putString(KEY_THEME, theme).apply()
    }

    /**
     * Dernière synchronisation
     */
    fun getLastSyncTimestamp(): Long = normalPrefs.getLong(KEY_LAST_SYNC, 0L)

    fun setLastSyncTimestamp(timestamp: Long) {
        normalPrefs.edit().putLong(KEY_LAST_SYNC, timestamp).apply()
    }

    /**
     * Vérifie si l'application est configurée
     */
    fun isConfigured(): Boolean {
        val hasUrl = !getServerUrl().isNullOrBlank()
        val hasPassword = !getPassword().isNullOrBlank()
        return hasUrl && hasPassword
    }

    private fun updateConfiguredState() {
        _isConfiguredFlow.value = isConfigured()
    }

    /**
     * Réinitialisation complète
     */
    fun clearAll() {
        normalPrefs.edit().clear().apply()
        encryptedPrefs.edit().clear().apply()
        _serverUrlFlow.value = null
        _isConfiguredFlow.value = false
    }

    companion object {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_PASSWORD = "password"
        private const val KEY_NOTIFICATION_HOUR = "notification_hour"
        private const val KEY_NOTIFICATION_MINUTE = "notification_minute"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_THEME = "theme"
        private const val KEY_LAST_SYNC = "last_sync"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_AUTO = "auto"
    }
}
