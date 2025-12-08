package com.smartagenda.worker

import android.content.Context
import androidx.work.*
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {
    
    fun scheduleDailySync(context: Context, hour: Int, minute: Int) {
        val currentTime = LocalTime.now()
        val targetTime = LocalTime.of(hour, minute)
        
        val initialDelay = if (targetTime.isAfter(currentTime)) {
            Duration.between(currentTime, targetTime).toMinutes()
        } else {
            Duration.between(currentTime, targetTime.plusHours(24)).toMinutes()
        }
        
        val workRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_sync",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
