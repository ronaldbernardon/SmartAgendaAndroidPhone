package com.smartagenda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * Modèle représentant un événement
 */
@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    @SerializedName("Id")
    val id: String,
    
    @SerializedName("Title")
    val title: String,
    
    @SerializedName("Description")
    val description: String?,
    
    @SerializedName("Category")
    val category: String,
    
    @SerializedName("Date")
    val date: String,
    
    @SerializedName("Time")
    val time: String?,
    
    @SerializedName("StartDate")
    val startDate: String,
    
    @SerializedName("Recurring")
    val recurring: Boolean = false,
    
    @SerializedName("RecurringPattern")
    val recurringPattern: String?,
    
    @SerializedName("Reminder")
    val reminder: String?,
    
    @SerializedName("NotifyChannel")
    val notifyChannel: String = "android",
    
    // Cache timestamp
    val cachedAt: Long = System.currentTimeMillis()
)

/**
 * Réponse API pour les événements
 */
data class EventsResponse(
    @SerializedName("events")
    val events: List<Event>
)

/**
 * Modèle pour les données UV
 */
data class UVData(
    @SerializedName("date")
    val date: String,
    
    @SerializedName("uv_index")
    val uvIndex: Double,
    
    @SerializedName("uv_clear_sky")
    val uvClearSky: Double?,
    
    @SerializedName("level_info")
    val levelInfo: UVLevelInfo,
    
    @SerializedName("is_today")
    val isToday: Boolean = false
)

data class UVLevelInfo(
    @SerializedName("level")
    val level: String,
    
    @SerializedName("color")
    val color: String,
    
    @SerializedName("protection")
    val protection: String
)

data class UVResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("uv_data")
    val uvData: UVData?
)

/**
 * Modèle pour les données météo
 */
data class WeatherData(
    @SerializedName("temp_min")
    val tempMin: Double,
    
    @SerializedName("temp_max")
    val tempMax: Double,
    
    @SerializedName("weather_code")
    val weatherCode: Int,
    
    @SerializedName("weather_description")
    val weatherDescription: String,
    
    @SerializedName("icon")
    val icon: String,
    
    @SerializedName("date")
    val date: String
)

data class WeatherResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("temp_min")
    val tempMin: Double?,
    
    @SerializedName("temp_max")
    val tempMax: Double?,
    
    @SerializedName("weather_description")
    val weatherDescription: String?,
    
    @SerializedName("icon")
    val icon: String?
)

/**
 * Modèle pour jour férié
 */
data class FerieResponse(
    @SerializedName("ferie")
    val ferie: Boolean,
    
    @SerializedName("nom")
    val nom: String?
)

/**
 * Modèle pour congés scolaires
 */
data class CongeResponse(
    @SerializedName("conge")
    val conge: Boolean,
    
    @SerializedName("nom")
    val nom: String?
)

/**
 * Modèle pour l'authentification
 */
data class LoginRequest(
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?
)

/**
 * Modèle combiné pour l'affichage du jour
 */
@Entity(tableName = "daily_summary")
data class DailySummary(
    @PrimaryKey
    val date: String,
    val events: List<Event>,
    val uvData: UVData?,
    val weatherData: WeatherData?,
    val ferieNom: String?,
    val congeNom: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)

// API Response types

data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null
)

data class EventsResponse(
    val events: List<Event>
)

data class NotificationsResponse(
    val success: Boolean,
    val count: Int,
    val notifications: List<NotificationItem>
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: String
)

data class HealthResponse(
    val status: String,
    val version: String? = null
)

data class FerieResponse(
    val ferie: Boolean,
    val nom: String? = null
)

data class CongeResponse(
    val conge: Boolean,
    val nom: String? = null
)

data class UVResponse(
    val success: Boolean,
    val uv_data: UVData? = null
)

data class UVData(
    val date: String,
    val uv_index: Double,
    val level_info: UVLevelInfo
)

data class UVLevelInfo(
    val level: String,
    val color: String,
    val protection: String
)

data class WeatherResponse(
    val success: Boolean,
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val weather_description: String? = null,
    val icon: String? = null
)
