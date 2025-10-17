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
import com.example.miprimerhuerto.data.repository.GameRepository
import kotlinx.coroutines.flow.first

class PlantNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = GameRepository(applicationContext)
        val gameState = repository.gameStateFlow.first()
        
        val plant = gameState.currentPlant ?: return Result.success()
        
        // Verificar si necesita notificación
        when {
            plant.isDead() -> {
                sendNotification(
                    title = "❌ Tu planta ha muerto",
                    message = "Tu ${com.example.miprimerhuerto.data.model.PlantTypeData.getInfo(plant.type).name} necesitaba más cuidados.",
                    priority = NotificationCompat.PRIORITY_HIGH
                )
            }
            plant.needsUrgentWater() -> {
                sendNotification(
                    title = "🚨 ¡Agua urgente!",
                    message = "Tu ${com.example.miprimerhuerto.data.model.PlantTypeData.getInfo(plant.type).name} necesita agua AHORA. Nivel: ${plant.waterLevel.toInt()}%",
                    priority = NotificationCompat.PRIORITY_HIGH
                )
            }
            plant.needsWater() -> {
                sendNotification(
                    title = "💧 Necesita agua",
                    message = "Tu ${com.example.miprimerhuerto.data.model.PlantTypeData.getInfo(plant.type).name} necesita agua. Nivel: ${plant.waterLevel.toInt()}%",
                    priority = NotificationCompat.PRIORITY_DEFAULT
                )
            }
            plant.hasPest -> {
                sendNotification(
                    title = "🐛 ¡Plaga detectada!",
                    message = "Tu ${com.example.miprimerhuerto.data.model.PlantTypeData.getInfo(plant.type).name} tiene plagas. Usa un pesticida.",
                    priority = NotificationCompat.PRIORITY_HIGH
                )
            }
            plant.canHarvest() -> {
                sendNotification(
                    title = "🎉 ¡Lista para cosechar!",
                    message = "Tu ${com.example.miprimerhuerto.data.model.PlantTypeData.getInfo(plant.type).name} está lista. ¡Es hora de cosechar!",
                    priority = NotificationCompat.PRIORITY_HIGH
                )
            }
        }
        
        return Result.success()
    }
    
    private fun sendNotification(title: String, message: String, priority: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Crear canal de notificación (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones de Plantas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre el estado de tus plantas"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Intent para abrir la app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Construir notificación
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    companion object {
        const val CHANNEL_ID = "plant_notifications"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "plant_check_work"
    }
}

