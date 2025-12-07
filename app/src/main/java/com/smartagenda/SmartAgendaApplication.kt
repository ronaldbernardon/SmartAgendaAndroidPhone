package com.smartagenda

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartAgendaApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SmartAgenda Notifications"
            val descriptionText = "Notifications quotidiennes pour vos événements"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("smartagenda_channel", name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
