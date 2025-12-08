package com.smartagenda.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartagenda.data.local.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val hour = preferencesManager.notificationHour.first()
                val minute = preferencesManager.notificationMinute.first()
                WorkManagerScheduler.scheduleDailySync(context, hour, minute)
            }
        }
    }
}
