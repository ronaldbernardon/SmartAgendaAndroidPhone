package com.smartagenda.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartagenda.R
import com.smartagenda.data.local.PreferencesManager
import com.smartagenda.repository.SmartAgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SmartAgendaRepository,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val today = java.time.LocalDate.now().toString()
            val summary = repository.getDailySummary(today)
            
            if (summary.events.isNotEmpty() || summary.isFerie || summary.isConge) {
                sendNotification(summary)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun sendNotification(summary: com.smartagenda.data.model.DailySummary) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val title = buildNotificationTitle(summary)
        val content = buildNotificationContent(summary)
        
        val notification = NotificationCompat.Builder(applicationContext, "smartagenda_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(1, notification)
    }
    
    private fun buildNotificationTitle(summary: com.smartagenda.data.model.DailySummary): String {
        val eventCount = summary.events.size
        return when {
            eventCount == 0 -> "SmartAgenda"
            eventCount == 1 -> "1 événement aujourd'hui"
            else -> "$eventCount événements aujourd'hui"
        }
    }
    
    private fun buildNotificationContent(summary: com.smartagenda.data.model.DailySummary): String {
        val parts = mutableListOf<String>()
        
        val dateStr = try {
            val date = java.time.LocalDate.parse(summary.date)
            date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", java.util.Locale.FRENCH))
        } catch (e: Exception) {
            summary.date
        }
        parts.add(dateStr)
        
        if (summary.isFerie) {
            parts.add("🎉 ${summary.ferieName}")
        }
        
        if (summary.isConge) {
            parts.add("🎒 ${summary.congeName}")
        }
        
        summary.weatherData?.let { weather ->
            parts.add("🌤️ ${weather.tempMin}°/${weather.tempMax}°C")
        }
        
        summary.uvData?.let { uv ->
            parts.add("☀️ UV ${uv.uvIndex}")
        }
        
        if (summary.events.isNotEmpty()) {
            val firstThree = summary.events.take(3)
            firstThree.forEach { event ->
                val time = event.time ?: "??:??"
                parts.add("📅 $time - ${event.title}")
            }
            
            if (summary.events.size > 3) {
                parts.add("... et ${summary.events.size - 3} autre(s)")
            }
        }
        
        return parts.joinToString("\n")
    }
}
