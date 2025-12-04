package com.example.miprimerhuerto.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.miprimerhuerto.MainActivity
import com.example.miprimerhuerto.R
import com.example.miprimerhuerto.utils.DebugConfig
import kotlinx.coroutines.*

class PlantNotificationService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false
    
    companion object {
        const val NOTIFICATION_ID = 1003
        const val CHANNEL_ID = "plant_service_channel"
        const val ACTION_START = "START_PLANT_SERVICE"
        const val ACTION_STOP = "STOP_PLANT_SERVICE"
        
        fun startService(context: Context) {
            val intent = Intent(context, PlantNotificationService::class.java).apply {
                action = ACTION_START
            }
            context.startForegroundService(intent)
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, PlantNotificationService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForegroundService()
            }
            ACTION_STOP -> {
                stopForegroundService()
            }
        }
        return START_STICKY // Reiniciar el servicio si se mata
    }
    
    private fun startForegroundService() {
        if (isRunning) return
        
        isRunning = true
        DebugConfig.log("🌱 Servicio de notificaciones iniciado")
        
        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // Iniciar verificación periódica
        serviceScope.launch {
            while (isRunning) {
                try {
                    // Ejecutar el Worker manualmente
                    val workRequest = OneTimeWorkRequestBuilder<PlantNotificationWorker>()
                        .build()
                    
                    WorkManager.getInstance(this@PlantNotificationService)
                        .enqueue(workRequest)
                    
                    DebugConfig.log("🌱 Verificación de notificaciones ejecutada desde servicio")
                    
                    // Esperar 1 minuto en modo DEBUG, 5 minutos en producción
                    delay(if (com.example.miprimerhuerto.utils.DebugConfig.DEBUG_MODE) 60000L else 300000L)
                } catch (e: Exception) {
                    DebugConfig.log("❌ Error en servicio de notificaciones: ${e.message}")
                    delay(30000) // Esperar 30 segundos antes de reintentar
                }
            }
        }
    }
    
    private fun stopForegroundService() {
        isRunning = false
        serviceScope.cancel()
        stopForeground(true)
        stopSelf()
        DebugConfig.log("🌱 Servicio de notificaciones detenido")
    }
    
    private fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🌱 Mi Primer Huerto")
            .setContentText("Monitoreando tu planta...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Servicio de Plantas",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantiene las notificaciones de plantas activas"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }
}
