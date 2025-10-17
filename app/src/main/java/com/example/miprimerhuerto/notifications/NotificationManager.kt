package com.example.miprimerhuerto.notifications

import android.content.Context
import androidx.work.*
import com.example.miprimerhuerto.utils.DebugConfig
import java.util.concurrent.TimeUnit

object PlantNotificationManager {
    
    fun schedulePeriodicNotifications(context: Context) {
        // Cancelar cualquier trabajo previo
        WorkManager.getInstance(context).cancelUniqueWork("plant_check_work")
        
        // Configuración más permisiva para que funcione en background
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        
        // En modo DEBUG, usar intervalo mínimo de 15 minutos (limitación de Android)
        val intervalMinutes = if (DebugConfig.DEBUG_MODE) 15L else 15L
        
        val notificationWork = PeriodicWorkRequestBuilder<PlantNotificationWorker>(
            intervalMinutes,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(0, TimeUnit.SECONDS) // Ejecutar inmediatamente
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "plant_check_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWork
        )
        
        DebugConfig.log("Notificaciones programadas - Intervalo: $intervalMinutes minutos")
        DebugConfig.log("Primera ejecución: inmediata")
        DebugConfig.log("Restricciones: Sin batería baja, sin carga, sin idle")
        
        // En modo DEBUG, también programar verificaciones más frecuentes con OneTimeWork
        if (DebugConfig.DEBUG_MODE) {
            scheduleDebugNotificationCheck(context)
        }
    }
    
    private fun scheduleDebugNotificationCheck(context: Context) {
        val debugWork = OneTimeWorkRequestBuilder<PlantNotificationWorker>()
            .setInitialDelay(30, TimeUnit.SECONDS) // Primera verificación a los 30 segundos
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "debug_notification_check",
            ExistingWorkPolicy.REPLACE,
            debugWork
        )
        
        DebugConfig.log("Verificación DEBUG de notificaciones programada en 30 segundos")
    }
    
    fun cancelNotifications(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("plant_check_work")
        WorkManager.getInstance(context).cancelUniqueWork("debug_notification_check")
    }
    
    fun testNotification(context: Context) {
        // Enviar notificación de prueba directamente
        PlantNotificationWorker.sendTestNotification(context)
    }
}

