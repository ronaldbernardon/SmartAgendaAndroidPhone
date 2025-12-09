package com.smartagenda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Event data classes
@Entity(tableName = "events")
data class Event(
    @PrimaryKey val Id: String,
    val Title: String,
    val Description: String?,
    val Category: String,
    val Date: String,
    val Time: String?,
    val Recurring: Boolean = false,
    val RecurringPattern: String? = null,
    val Reminder: String? = null,
    val NotifyChannel: String = "android",
    val StartDate: String? = null
)

// Daily Summary for HomeScreen avec TOUS les champs
data class DailySummary(
    val date: String,
    val events: List<Event>,
    val isFerie: Boolean = false,
    val ferieNom: String? = null,
    val isConge: Boolean = false,
    val congeNom: String? = null,
    val weatherData: WeatherData? = null,
    val uvData: UVData? = null,
    val lastUpdated: String? = null
)

// Weather data avec TOUS les champs
data class WeatherData(
    val tempMin: Double,
    val tempMax: Double,
    val weatherCode: Int? = null,
    val weatherDescription: String,
    val icon: String,
    val date: String? = null
)

// API Response types
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

data class UVResponse(
    val success: Boolean,
    val uv_data: UVData? = null
)

data class UVData(
    val date: String,
    val uvIndex: Double,
    val levelInfo: UVLevelInfo
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
