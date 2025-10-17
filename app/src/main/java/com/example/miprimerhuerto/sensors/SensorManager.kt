package com.example.miprimerhuerto.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager as AndroidSensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

class SensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as AndroidSensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    
    var onShakeDetected: (() -> Unit)? = null
    var onLightLevelChanged: ((Float) -> Unit)? = null
    
    private var lastShakeTime = 0L
    private val shakeThreshold = 15f
    private val shakeTimeLapse = 1000L // 1 segundo entre sacudidas
    
    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, AndroidSensorManager.SENSOR_DELAY_UI)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, AndroidSensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    fun stopListening() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    handleAccelerometer(it.values)
                }
                Sensor.TYPE_LIGHT -> {
                    handleLightSensor(it.values[0])
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesitamos hacer nada aquí
    }
    
    private fun handleAccelerometer(values: FloatArray) {
        val x = values[0]
        val y = values[1]
        val z = values[2]
        
        val acceleration = sqrt(x * x + y * y + z * z) - AndroidSensorManager.GRAVITY_EARTH
        
        if (acceleration > shakeThreshold) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > shakeTimeLapse) {
                lastShakeTime = currentTime
                onShakeDetected?.invoke()
            }
        }
    }
    
    private fun handleLightSensor(lightLevel: Float) {
        onLightLevelChanged?.invoke(lightLevel)
    }
}

@Composable
fun rememberSensorManager(
    onShakeDetected: () -> Unit = {},
    onLightLevelChanged: (Float) -> Unit = {}
): SensorManager {
    val context = LocalContext.current
    val sensorManager = remember { SensorManager(context) }
    
    DisposableEffect(Unit) {
        sensorManager.onShakeDetected = onShakeDetected
        sensorManager.onLightLevelChanged = onLightLevelChanged
        sensorManager.startListening()
        
        onDispose {
            sensorManager.stopListening()
        }
    }
    
    return sensorManager
}

@Composable
fun ShakeSensor(
    enabled: Boolean = true,
    onShake: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember { SensorManager(context) }
    
    DisposableEffect(enabled) {
        if (enabled) {
            sensorManager.onShakeDetected = onShake
            sensorManager.startListening()
        }
        
        onDispose {
            if (enabled) {
                sensorManager.stopListening()
            }
        }
    }
}

@Composable
fun LightLevelSensor(
    enabled: Boolean = true,
    onLightLevelChanged: (Float) -> Unit
): Float {
    val context = LocalContext.current
    var lightLevel by remember { mutableStateOf(0f) }
    val sensorManager = remember { SensorManager(context) }
    
    DisposableEffect(enabled) {
        if (enabled) {
            sensorManager.onLightLevelChanged = { level ->
                lightLevel = level
                onLightLevelChanged(level)
            }
            sensorManager.startListening()
        }
        
        onDispose {
            if (enabled) {
                sensorManager.stopListening()
            }
        }
    }
    
    return lightLevel
}

