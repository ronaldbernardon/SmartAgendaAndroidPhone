package com.smartagenda.data.api

import com.smartagenda.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface SmartAgendaApi {
    
    @POST("auth/password")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("api/events/upcoming")
    suspend fun getUpcomingEvents(): Response<EventsResponse>
    
    @GET("api/events/day")
    suspend fun getEventsForDay(@Query("date") date: String): Response<EventsResponse>
    
    @POST("api/events")
    suspend fun createEvent(@Body event: EventRequest): Response<ApiResponse>
    
    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: String, @Body event: EventRequest): Response<ApiResponse>
    
    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<ApiResponse>
    
    @GET("api/notifications/pending")
    suspend fun getPendingNotifications(): Response<NotificationsResponse>
    
    @GET("api/feries")
    suspend fun checkFerie(@Query("date") date: String): Response<FerieResponse>
    
    @GET("api/conges")
    suspend fun checkConge(@Query("date") date: String): Response<CongeResponse>
    
    @GET("api/uv/today")
    suspend fun getUVToday(): Response<UVResponse>
    
    @GET("api/weather/date")
    suspend fun getWeatherForDate(@Query("date") date: String): Response<WeatherResponse>
    
    @GET("/")
    suspend fun healthCheck(): Response<HealthResponse>
}

// Request data classes
data class EventRequest(
    val title: String,
    val description: String? = null,
    val category: String,
    val date: String,
    val time: String?,
    val Recurring: Boolean = false,
    val RecurringPattern: String? = null,
    val Reminder: String? = null,
    val NotifyChannel: String = "android"
)
