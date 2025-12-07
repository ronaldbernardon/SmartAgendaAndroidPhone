package com.smartagenda.repository

import com.smartagenda.data.api.SmartAgendaApi
import com.smartagenda.data.local.EventDao
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour gérer les données SmartAgenda
 * Gère le cache local et les appels API
 */
@Singleton
class SmartAgendaRepository @Inject constructor(
    private val api: SmartAgendaApi,
    private val eventDao: EventDao,
    private val preferencesManager: PreferencesManager
) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Récupère le résumé complet du jour
     */
    fun getDailySummary(date: LocalDate = LocalDate.now()): Flow<Result<DailySummary>> = flow {
        val dateString = date.format(dateFormatter)

        try {
            // Récupérer toutes les données en parallèle
            val events = fetchEvents(dateString)
            val uvData = fetchUVData()
            val weatherData = fetchWeatherData(dateString)
            val ferieData = fetchFerieData(dateString)
            val congeData = fetchCongeData(dateString)

            // Créer le résumé
            val summary = DailySummary(
                date = dateString,
                events = events ?: emptyList(),
                uvData = uvData,
                weatherData = weatherData,
                ferieNom = if (ferieData?.ferie == true) ferieData.nom else null,
                congeNom = if (congeData?.conge == true) congeData.nom else null
            )

            // Mettre à jour le cache
            if (events != null && events.isNotEmpty()) {
                eventDao.insertEvents(events)
            }

            preferencesManager.setLastSyncTimestamp(System.currentTimeMillis())

            emit(Result.success(summary))

        } catch (e: Exception) {
            // En cas d'erreur, essayer de charger depuis le cache
            val cachedEvents = try {
                eventDao.getEventsForDate(dateString)
            } catch (_: Exception) {
                null
            }

            if (cachedEvents != null) {
                emit(Result.success(DailySummary(
                    date = dateString,
                    events = emptyList(),
                    uvData = null,
                    weatherData = null,
                    ferieNom = null,
                    congeNom = null,
                    lastUpdated = preferencesManager.getLastSyncTimestamp()
                )))
            } else {
                emit(Result.failure(e))
            }
        }
    }

    /**
     * Récupère les événements d'un jour
     */
    private suspend fun fetchEvents(date: String): List<Event>? {
        return try {
            val response = api.getEventsForDay(date)
            if (response.isSuccessful) {
                response.body()?.events
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Récupère les données UV
     */
    private suspend fun fetchUVData(): UVData? {
        return try {
            val response = api.getUVToday()
            if (response.isSuccessful) {
                response.body()?.uvData
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Récupère les données météo
     */
    private suspend fun fetchWeatherData(date: String): WeatherData? {
        return try {
            val response = api.getWeatherForDate(date)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.tempMin != null && body.tempMax != null) {
                    WeatherData(
                        tempMin = body.tempMin,
                        tempMax = body.tempMax,
                        weatherCode = 0,
                        weatherDescription = body.weatherDescription ?: "",
                        icon = body.icon ?: "",
                        date = date
                    )
                } else null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Vérifie si le jour est férié
     */
    private suspend fun fetchFerieData(date: String): FerieResponse? {
        return try {
            val response = api.checkFerie(date)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Vérifie si le jour est en congés scolaires
     */
    private suspend fun fetchCongeData(date: String): CongeResponse? {
        return try {
            val response = api.checkConge(date)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Authentification
     */
    suspend fun login(password: String): Result<Boolean> {
        return try {
            val response = api.login(LoginRequest(password))
            if (response.isSuccessful && response.body()?.success == true) {
                preferencesManager.setPassword(password)
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Authentification échouée"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Test de connexion
     */
    suspend fun testConnection(): Result<Boolean> {
        return try {
            val response = api.healthCheck()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Récupère les événements du cache
     */
    fun getCachedEventsForDate(date: LocalDate): Flow<List<Event>> {
        return eventDao.getEventsForDate(date.format(dateFormatter))
    }

    /**
     * Nettoie les anciens événements du cache (> 7 jours)
     */
    suspend fun cleanOldCache() {
        val cutoffDate = LocalDate.now().minusDays(7).format(dateFormatter)
        eventDao.deleteOldEvents(cutoffDate)
    }
}
