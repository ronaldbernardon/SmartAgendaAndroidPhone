package com.smartagenda.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.smartagenda.MainActivity
import com.smartagenda.R
import com.smartagenda.SmartAgendaApplication
import com.smartagenda.repository.SmartAgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Worker pour synchroniser les événements quotidiennement
 */
@HiltWorker
class DailySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: SmartAgendaRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Récupérer les données du jour
            val today = LocalDate.now()
            
            repository.getDailySummary(today).collect { result ->
                result.onSuccess { summary ->
                    // Envoyer la notification
                    sendDailyNotification(summary.events.size, summary)
                    
                    // Nettoyer le cache ancien
                    repository.cleanOldCache()
                }.onFailure {
                    // En cas d'erreur, essayer avec le cache
                    sendErrorNotification()
                }
            }

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun sendDailyNotification(eventCount: Int, summary: com.smartagenda.data.model.DailySummary) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Formater le message
        val title = if (eventCount == 0) {
            "📅 Pas d'événement aujourd'hui"
        } else {
            "📅 $eventCount événement${if (eventCount > 1) "s" else ""} aujourd'hui"
        }

        val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", java.util.Locale.FRENCH)
        val dateFormatted = LocalDate.now().format(dateFormatter)

        val message = buildString {
            append(dateFormatted.replaceFirstChar { it.uppercase() })
            
            // Ajouter info jour férié
            if (summary.ferieNom != null) {
                append("\n🎉 ${summary.ferieNom}")
            }
            
            // Ajouter info congés
            if (summary.congeNom != null) {
                append("\n🎒 ${summary.congeNom}")
            }
            
            // Ajouter météo
            if (summary.weatherData != null) {
                append("\n${summary.weatherData.icon} ${summary.weatherData.tempMin.toInt()}°C / ${summary.weatherData.tempMax.toInt()}°C")
            }
            
            // Ajouter UV
            if (summary.uvData != null) {
                append("\n☀️ UV: ${summary.uvData.uvIndex} (${summary.uvData.levelInfo.level})")
            }
            
            // Ajouter les 3 premiers événements
            if (eventCount > 0) {
                append("\n\nProchains événements:")
                summary.events.take(3).forEach { event ->
                    append("\n• ${event.time ?: "Toute la journée"} - ${event.title}")
                }
                
                if (eventCount > 3) {
                    append("\n... et ${eventCount - 3} autre${if (eventCount - 3 > 1) "s" else ""}")
                }
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, SmartAgendaApplication.setChannelId("smartagenda_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun sendErrorNotification() {
        val notification = NotificationCompat.Builder(applicationContext, SmartAgendaApplication.setChannelId("smartagenda_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⚠️ Synchronisation échouée")
            .setContentText("Impossible de récupérer les événements. Vérifiez votre connexion VPN.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val WORK_NAME = "daily_sync_work"
        private const val NOTIFICATION_ID = 1001

        /**
         * Planifie la synchronisation quotidienne
         */
        fun schedule(context: Context, hour: Int = 7, minute: Int = 0) {
            val currentTime = Calendar.getInstance()
            val targetTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Si l'heure est déjà passée aujourd'hui, planifier pour demain
            if (targetTime.before(currentTime)) {
                targetTime.add(Calendar.DAY_OF_MONTH, 1)
            }

            val delay = targetTime.timeInMillis - currentTime.timeInMillis

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(
                24, TimeUnit.HOURS,
                15, TimeUnit.MINUTES
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    workRequest
                )
        }

        /**
         * Annule la synchronisation planifiée
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
