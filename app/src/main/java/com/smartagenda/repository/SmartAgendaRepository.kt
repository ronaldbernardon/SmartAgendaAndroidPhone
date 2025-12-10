package com.smartagenda.data.repository

import android.util.Log
import com.smartagenda.data.local.EventDao
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.data.model.DailySummary
import com.smartagenda.data.model.Event
import com.smartagenda.data.network.SmartAgendaApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartAgendaRepository @Inject constructor(
    private val api: SmartAgendaApi,
    private val eventDao: EventDao,
    private val preferencesManager: PreferencesManager
) {
    
    suspend fun getDailySummary(date: String): Flow<Result<DailySummary>> = flow {
        try {
            Log.d("Repository", "Chargement du résumé pour: $date")
            
            // Appel API pour les événements du jour
            val eventsResponse = api.getEventsForDay(date)
            val events = eventsResponse.events ?: emptyList()
            
            Log.d("Repository", "Événements reçus: ${events.size}")
            
            // Appel API pour l'indice UV
            var uvIndex: Double? = null
            var uvLevel: String? = null
            try {
                val uvResponse = api.getUVForDate(date)
                if (uvResponse.success == true) {
                    uvIndex = uvResponse.uvIndex
                    uvLevel = uvResponse.level
                    Log.d("Repository", "UV: $uvIndex ($uvLevel)")
                }
            } catch (e: Exception) {
                Log.w("Repository", "Erreur UV: ${e.message}")
            }
            
            // Appel API pour la météo
            var tempMin: Double? = null
            var tempMax: Double? = null
            var weatherDescription: String? = null
            var weatherIcon: String? = null
            try {
                val weatherResponse = api.getWeatherForDate(date)
                if (weatherResponse.success == true) {
                    tempMin = weatherResponse.tempMin
                    tempMax = weatherResponse.tempMax
                    weatherDescription = weatherResponse.weatherDescription
                    weatherIcon = weatherResponse.icon
                    Log.d("Repository", "Météo: ${tempMin}°/${tempMax}° - $weatherDescription")
                }
            } catch (e: Exception) {
                Log.w("Repository", "Erreur météo: ${e.message}")
            }
            
            // Sauvegarder les événements en cache
            eventDao.insertEvents(events)
            
            // Créer le résumé
            val summary = DailySummary(
                date = date,
                dateFormatted = formatDate(date),
                totalEvents = events.size,
                events = events,
                uvIndex = uvIndex,
                uvLevel = uvLevel,
                tempMin = tempMin,
                tempMax = tempMax,
                weatherDescription = weatherDescription,
                weatherIcon = weatherIcon,
                lastUpdated = System.currentTimeMillis()
            )
            
            Log.d("Repository", "Résumé créé avec succès")
            emit(Result.success(summary))
            
        } catch (e: Exception) {
            Log.e("Repository", "Erreur: ${e.message}", e)
            
            // Fallback: charger depuis le cache
            try {
                val cachedEvents = eventDao.getEventsForDate(date).first()
                
                val summary = DailySummary(
                    date = date,
                    dateFormatted = formatDate(date),
                    totalEvents = cachedEvents.size,
                    events = cachedEvents,
                    uvIndex = null,
                    uvLevel = null,
                    tempMin = null,
                    tempMax = null,
                    weatherDescription = null,
                    weatherIcon = null,
                    lastUpdated = null
                )
                
                Log.d("Repository", "Chargé depuis le cache: ${cachedEvents.size} événements")
                emit(Result.success(summary))
            } catch (cacheError: Exception) {
                Log.e("Repository", "Erreur cache: ${cacheError.message}")
                emit(Result.failure(e))
            }
        }
    }
    
    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH)
            val outputFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH)
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }
}
