package com.smartagenda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

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
    val category: String?,
    
    @SerializedName("Date")
    val date: String,
    
    @SerializedName("Time")
    val time: String?,
    
    @SerializedName("StartDate")
    val startDate: String,
    
    @SerializedName("Recurring")
    val recurring: Boolean?,
    
    @SerializedName("RecurringPattern")
    val recurringPattern: String?,
    
    @SerializedName("Reminder")
    val reminder: String?,
    
    @SerializedName("NotifyChannel")
    val notifyChannel: String?
)

data class DailySummary(
    val date: String,
    val dateFormatted: String,
    val totalEvents: Int,
    val events: List<Event>,
    val uvIndex: Double?,
    val uvLevel: String?,
    val tempMin: Double?,
    val tempMax: Double?,
    val weatherDescription: String?,
    val weatherIcon: String?,
    val lastUpdated: Long?
)

data class UVResponse(
    val success: Boolean?,
    @SerializedName("uv_index")
    val uvIndex: Double?,
    val level: String?,
    val date: String?
)

data class WeatherResponse(
    val success: Boolean?,
    @SerializedName("temp_min")
    val tempMin: Double?,
    @SerializedName("temp_max")
    val tempMax: Double?,
    @SerializedName("weather_description")
    val weatherDescription: String?,
    val icon: String?,
    val date: String?
)
