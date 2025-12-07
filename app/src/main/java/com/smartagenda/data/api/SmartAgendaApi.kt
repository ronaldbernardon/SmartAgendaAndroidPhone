package com.smartagenda.data.api

import com.smartagenda.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface API SmartAgenda
 * Définit tous les endpoints disponibles
 */
interface SmartAgendaApi {

    /**
     * Authentification
     */
    @POST("auth/password")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Récupère les événements d'un jour spécifique
     */
    @GET("api/events/day")
    suspend fun getEventsForDay(@Query("date") date: String): Response<EventsResponse>

    /**
     * Récupère les événements à venir
     */
    @GET("api/events/upcoming")
    suspend fun getUpcomingEvents(): Response<EventsResponse>

    /**
     * Récupère l'indice UV du jour
     */
    @GET("api/uv/today")
    suspend fun getUVToday(): Response<UVResponse>

    /**
     * Récupère l'indice UV pour une date
     */
    @GET("api/uv/date")
    suspend fun getUVForDate(@Query("date") date: String): Response<UVResponse>

    /**
     * Récupère la météo pour une date
     */
    @GET("api/weather/date")
    suspend fun getWeatherForDate(@Query("date") date: String): Response<WeatherResponse>

    /**
     * Vérifie si une date est fériée
     */
    @GET("api/feries")
    suspend fun checkFerie(@Query("date") date: String): Response<FerieResponse>

    /**
     * Vérifie si une date est en congés scolaires
     */
    @GET("api/conges")
    suspend fun checkConge(@Query("date") date: String): Response<CongeResponse>

    /**
     * Healthcheck (pour vérifier la connexion)
     */
    @GET("/")
    suspend fun healthCheck(): Response<Unit>
}
