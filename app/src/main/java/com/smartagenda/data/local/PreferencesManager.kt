package com.smartagenda.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val PASSWORD = stringPreferencesKey("password")
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        private val IS_CONFIGURED = booleanPreferencesKey("is_configured")
        private val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
    }

    val serverUrl: Flow<String> = dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: ""
    }

    val password: Flow<String> = dataStore.data.map { preferences ->
        preferences[PASSWORD] ?: ""
    }

    val notificationHour: Flow<Int> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_HOUR] ?: 7
    }

    val notificationMinute: Flow<Int> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_MINUTE] ?: 0
    }

    val isConfigured: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_CONFIGURED] ?: false
    }

    val isConfiguredFlow: Flow<Boolean> = isConfigured

    suspend fun getServerUrl(): String {
        return serverUrl.first()
    }

    suspend fun getPassword(): String {
        return password.first()
    }

    suspend fun getLastSyncTimestamp(): Long {
        return dataStore.data.map { it[LAST_SYNC_TIMESTAMP] ?: 0L }.first()
    }

    suspend fun updateServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }

    suspend fun updatePassword(password: String) {
        dataStore.edit { preferences ->
            preferences[PASSWORD] = password
        }
    }

    suspend fun setPassword(password: String) {
        updatePassword(password)
    }

    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = hour
            preferences[NOTIFICATION_MINUTE] = minute
        }
    }

    suspend fun updateConfigured(configured: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_CONFIGURED] = configured
        }
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] = timestamp
        }
    }
}
