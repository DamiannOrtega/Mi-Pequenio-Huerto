package com.example.miprimerhuerto.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.miprimerhuerto.MainActivity
import com.example.miprimerhuerto.R

class PlantNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val CHANNEL_ID = "plant_notifications"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "plant_check_work"
        
        /**
         * Envía una notificación de cambio de etapa directamente
         */
        fun sendStageChangeNotification(context: Context, title: String, message: String) {
            try {
                com.example.miprimerhuerto.utils.DebugConfig.log("Enviando notificación de cambio de etapa: $title")
                
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
                // Crear canal
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        "Notificaciones de Plantas",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Notificaciones sobre el estado de tus plantas"
                        enableVibration(true)
                        enableLights(true)
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                
                // Intent
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                
                // Notificación
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(0, 250, 250, 250))
                    .build()
                
                notificationManager.notify(1002, notification)
                com.example.miprimerhuerto.utils.DebugConfig.log("✅ Notificación de cambio de etapa enviada!")
            } catch (e: Exception) {
                com.example.miprimerhuerto.utils.DebugConfig.log("❌ Error en notificación de cambio de etapa: ${e.message}")
                e.printStackTrace()
            }
        }
        
        /**
         * Envía una notificación de prueba directamente
         */
        fun sendTestNotification(context: Context) {
            try {
                com.example.miprimerhuerto.utils.DebugConfig.log("🧪 Enviando notificación de prueba...")
                
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
                // Crear canal
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        "Notificaciones de Plantas",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Notificaciones sobre el estado de tus plantas"
                        enableVibration(true)
                        enableLights(true)
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                
                // Intent
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                
                // Notificación de prueba
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("🧪 Prueba de Notificación")
                    .setContentText("¡Las notificaciones funcionan correctamente!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(0, 250, 250, 250))
                    .build()
                
                notificationManager.notify(1003, notification)
                com.example.miprimerhuerto.utils.DebugConfig.log("✅ Notificación de prueba enviada!")
            } catch (e: Exception) {
                com.example.miprimerhuerto.utils.DebugConfig.log("❌ Error en notificación de prueba: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            com.example.miprimerhuerto.utils.DebugConfig.log("Worker de notificaciones ejecutándose...")
            
            // Por ahora, solo loguear que el worker se ejecutó
            // Las notificaciones de cambio de etapa se manejan desde el ViewModel
            com.example.miprimerhuerto.utils.DebugConfig.log("Worker ejecutado correctamente")
            
            Result.success()
        } catch (e: Exception) {
            com.example.miprimerhuerto.utils.DebugConfig.log("Error en Worker de notificaciones: ${e.message}")
            e.printStackTrace()
            Result.failure()
        }
    }
}