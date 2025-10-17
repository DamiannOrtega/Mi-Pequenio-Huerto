package com.example.miprimerhuerto.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.example.miprimerhuerto.ui.theme.*
import java.util.*

@Composable
fun getBackgroundForTime(): Brush {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    
    return when (currentHour) {
        in 6..11 -> {
            // Mañana (6 AM - 11 AM)
            Brush.verticalGradient(
                colors = listOf(
                    SkyMorning,
                    SkyMorning.copy(alpha = 0.7f),
                    GreenLight.copy(alpha = 0.3f)
                )
            )
        }
        in 12..17 -> {
            // Tarde (12 PM - 5 PM)
            Brush.verticalGradient(
                colors = listOf(
                    SkyAfternoon,
                    SkyAfternoon.copy(alpha = 0.7f),
                    GreenLight.copy(alpha = 0.3f)
                )
            )
        }
        in 18..20 -> {
            // Atardecer (6 PM - 8 PM)
            Brush.verticalGradient(
                colors = listOf(
                    SkyEvening,
                    SkyEvening.copy(alpha = 0.6f),
                    BrownPrimary.copy(alpha = 0.4f)
                )
            )
        }
        else -> {
            // Noche (9 PM - 5 AM)
            Brush.verticalGradient(
                colors = listOf(
                    SkyNight,
                    SkyNight.copy(alpha = 0.8f),
                    GreenDark.copy(alpha = 0.5f)
                )
            )
        }
    }
}

fun getTimeOfDayString(): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    
    return when (currentHour) {
        in 6..11 -> "Buenos días"
        in 12..17 -> "Buenas tardes"
        in 18..20 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}

