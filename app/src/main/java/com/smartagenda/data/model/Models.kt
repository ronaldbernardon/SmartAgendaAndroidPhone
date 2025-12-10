package com.smartagenda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Event - Convention Kotlin standard (camelCase)
 * @SerializedName mappe l'API Python (PascalCase)
 */
@Entity(tableName = "events")
data class Event(
    @PrimaryKey 
    @SerializedName("Id") val id: String,
    
    @SerializedName("Title") val title: String,
    
    @SerializedName("Description") val description: String? = null,
    
    @SerializedName("Category") val category: String,
    
    @SerializedName("Date") val date: String,
    
    @SerializedName("Time") val time: String? = null,
    
    @SerializedName("Recurring") val recurring: Boolean = false,
    
    @SerializedName("RecurringPattern") val recurringPattern: String? = null,
    
    @SerializedName("Reminder") val reminder: String? = null,
    
    @SerializedName("NotifyChannel") val notifyChannel: String = "android",
    
    @SerializedName("StartDate") val startDate: String? = null
)

/**
 * Daily Summary pour HomeScreen
 */
data class DailySummary(
    val date: String,
    val events: List<Event>,
    val isFerie: Boolean = false,
    val ferieNom: String? = null,
    val isConge: Boolean = false,
    val congeNom: String? = null,
    val weatherData: WeatherData? = null,
    val uvData: UVData? = null,
    val lastUpdated: Long? = null
)

/**
 * Weather Data (camelCase)
 */
data class WeatherData(
    val tempMin: Double,
    val tempMax: Double,
    val weatherCode: Int? = null,
    val weatherDescription: String,
    val icon: String,
    val date: String? = null
)

/**
 * API Response types
 */
data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)

data class LoginRequest(
    val password: String
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

/**
 * UV Response - API Python snake_case → Kotlin camelCase
 */
data class UVResponse(
    val success: Boolean,
    @SerializedName("uv_data") val uvData: UVData? = null
)

/**
 * UV Data - API Python snake_case → Kotlin camelCase
 */
data class UVData(
    val date: String,
    @SerializedName("uv_index") val uvIndex: Double,
    @SerializedName("level_info") val levelInfo: UVLevelInfo
)

data class UVLevelInfo(
    val level: String,
    val color: String,
    val protection: String
)

/**
 * Weather Response - API Python snake_case → Kotlin camelCase
 */
data class WeatherResponse(
    val success: Boolean,
    @SerializedName("temp_min") val tempMin: Double? = null,
    @SerializedName("temp_max") val tempMax: Double? = null,
    @SerializedName("weather_description") val weatherDescription: String? = null,
    val icon: String? = null
)
