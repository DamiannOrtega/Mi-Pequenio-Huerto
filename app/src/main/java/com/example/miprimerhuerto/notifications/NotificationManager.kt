package com.example.miprimerhuerto.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object PlantNotificationManager {
    
    fun schedulePeriodicNotifications(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()
        
        val notificationWork = PeriodicWorkRequestBuilder<PlantNotificationWorker>(
            15, // Verificar cada 15 minutos
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PlantNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWork
        )
    }
    
    fun cancelNotifications(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PlantNotificationWorker.WORK_NAME)
    }
}

