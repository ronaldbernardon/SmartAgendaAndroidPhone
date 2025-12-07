package com.smartagenda.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartagenda.R
import com.smartagenda.repository.SmartAgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class DailySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SmartAgendaRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val today = LocalDate.now()
            val summaryResult = repository.getDailySummary(today).first()
            
            summaryResult.onSuccess { summary ->
                if (summary.events.isNotEmpty()) {
                    sendNotification(summary.events.size)
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun sendNotification(eventCount: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val title = when {
            eventCount == 1 -> "1 événement aujourd'hui"
            else -> "$eventCount événements aujourd'hui"
        }
        
        val notification = NotificationCompat.Builder(applicationContext, "smartagenda_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText("Ouvrez SmartAgenda pour voir vos événements")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(1, notification)
    }
}
