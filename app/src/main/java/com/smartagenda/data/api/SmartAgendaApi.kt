package com.smartagenda.data.network

import com.smartagenda.data.model.*
import retrofit2.http.*

interface SmartAgendaApi {
    
    @POST("auth/password")
    suspend fun authenticate(@Body request: AuthRequest): AuthResponse
    
    @GET("api/events/day")
    suspend fun getEventsForDay(@Query("date") date: String): EventsResponse
    
    @GET("api/uv/date")
    suspend fun getUVForDate(@Query("date") date: String): UVResponse
    
    @GET("api/weather/date")
    suspend fun getWeatherForDate(@Query("date") date: String): WeatherResponse
    
    @GET("api/notifications/pending")
    suspend fun getPendingNotifications(): NotificationsResponse
}

// Modèles de réponse
data class AuthRequest(
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String?
)

data class EventsResponse(
    val events: List<Event>?
)

data class NotificationsResponse(
    val success: Boolean,
    val count: Int?,
    val notifications: List<Notification>?
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: String
)
