package com.smartagenda.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartagenda.data.local.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Receiver appelé au démarrage du téléphone
 * Réinitialise les tâches planifiées
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Vérifier si l'application est configurée
            if (preferencesManager.isConfigured() && preferencesManager.isNotificationsEnabled()) {
                // Replanifier la synchronisation quotidienne
                val hour = preferencesManager.getNotificationHour()
                val minute = preferencesManager.getNotificationMinute()
                
                DailySyncWorker.schedule(context, hour, minute)
            }
        }
    }
}
