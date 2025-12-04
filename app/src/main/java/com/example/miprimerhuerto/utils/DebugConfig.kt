package com.example.miprimerhuerto.utils

/**
 * Configuración de DEBUG para pruebas
 * 
 * IMPORTANTE: Cambiar DEBUG_MODE a false antes de publicar la app
 */
object DebugConfig {
    /**
     * Modo de depuración
     * true = Tiempos acelerados para pruebas
     * false = Tiempos reales para producción
     */
    const val DEBUG_MODE = false // Cambiar a false para producción
    
    /**
     * Multiplicador de tiempo en modo DEBUG
     * Si DEBUG_MODE = true:
     *   - 1 minuto real = 1 hora del juego (60x más rápido)
     *   - Las notificaciones se verifican cada 1 minuto en lugar de 15
     * 
     * Si DEBUG_MODE = false:
     *   - 1 hora real = 1 día del juego (tiempo normal)
     *   - Las notificaciones se verifican cada 15 minutos
     */
    val TIME_MULTIPLIER = if (DEBUG_MODE) 60 else 1 // 60x más rápido en debug
    
    /**
     * Intervalo de notificaciones en minutos
     */
    val NOTIFICATION_INTERVAL_MINUTES = if (DEBUG_MODE) 1L else 15L
    
    /**
     * Duración de los Snackbar en milisegundos
     */
    val SNACKBAR_DURATION_MS = if (DEBUG_MODE) 8000L else 5000L // 8 segundos en debug
    
    /**
     * Función para obtener la duración real basada en el modo
     */
    fun getAdjustedDuration(normalDurationMs: Long): Long {
        return if (DEBUG_MODE) {
            normalDurationMs / TIME_MULTIPLIER
        } else {
            normalDurationMs
        }
    }
    
    /**
     * Log para debug
     */
    fun log(message: String) {
        if (DEBUG_MODE) {
            println("🌱 DEBUG: $message")
        }
    }
}

