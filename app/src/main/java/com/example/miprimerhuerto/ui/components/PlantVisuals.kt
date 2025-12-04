package com.example.miprimerhuerto.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image // <-- Import necesario
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.miprimerhuerto.R
import com.example.miprimerhuerto.data.model.PlantStage
import com.example.miprimerhuerto.data.model.PlantType

@Composable
fun PlantVisualization(
    plantType: PlantType,
    stage: PlantStage,
    health: Float,
    hasPest: Boolean,
    modifier: Modifier = Modifier
) {
    // Animación de balanceo
    val infiniteTransition = rememberInfiniteTransition(label = "sway")
    val swayAngle by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    // --- INICIO DEL CAMBIO #2 ---
    // Obtener el ID del recurso de la imagen, pasando ahora 'hasPest'
    val imageResId = getPlantImageResource(plantType, stage, health, hasPest)
    // --- FIN DEL CAMBIO #2 ---

    Box(modifier = modifier.size(200.dp)) {
        // 1. Imagen de la planta
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Planta en etapa $stage",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Aplicar animación de balanceo (excepto a semilla o muerta)
                    if (stage != PlantStage.MUERTA && stage != PlantStage.SEMILLA) {
                        rotationZ = swayAngle
                    }
                },
            contentScale = ContentScale.Fit
        )

        // --- INICIO DEL CAMBIO #1 ---
        // 2. Canvas superpuesto ELIMINADO.
        // La lógica de 'hasPest' ahora está dentro de 'getPlantImageResource'
        // y la imagen principal 'imageResId' ya será la versión con plaga.
        /*
        if (hasPest && stage != PlantStage.MUERTA) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                ...
            }
        }
        */
        // --- FIN DEL CAMBIO #1 ---
    }
}

/**
 * Determina qué recurso de imagen (Drawable) usar según la etapa,
 * el tipo, la salud y LAS PLAGAS de la planta.
 */
@DrawableRes
private fun getPlantImageResource(
    plantType: PlantType,
    stage: PlantStage,
    health: Float,
    hasPest: Boolean // <-- AÑADIDO
): Int {

    // --- INICIO DEL CAMBIO #3 ---

    // 1. Etapa MUERTA (máxima prioridad, nunca tiene plaga)
    if (stage == PlantStage.MUERTA) return R.drawable.dead

    // 2. Lógica de Plagas (segunda prioridad)
    // Si hay plaga, mostramos la imagen con plaga e ignoramos la salud (isSad)
    // Asumimos que tienes drawables como: germination_bug, b_seed_bug, b_mature_bug, etc.
    if (hasPest) {
        return when (stage) {
            // Generales con plaga
            PlantStage.GERMINACION -> R.drawable.germination_bug
            PlantStage.PLANTULA -> R.drawable.seedling_bug
            PlantStage.JOVEN -> R.drawable.young_bug

            // Específicas (por tipo) con plaga
            PlantStage.SEMILLA -> when (plantType) {
                PlantType.FRIJOL -> R.drawable.b_seed_bug
                PlantType.ROSA -> R.drawable.r_seed_bug
                PlantType.TOMATE -> R.drawable.t_seed_bug
                PlantType.GIRASOL -> R.drawable.g_seed_bug
                else -> R.drawable.b_seed_bug // Fallback
            }
            PlantStage.MADURO -> when (plantType) {
                PlantType.FRIJOL -> R.drawable.b_mature_bug
                PlantType.TOMATE -> R.drawable.t_mature_bug
                PlantType.ROSA -> R.drawable.r_mature_bug
                PlantType.GIRASOL -> R.drawable.g_mature_bug
                else -> R.drawable.young_bug // Fallback
            }
            PlantStage.COSECHABLE -> when (plantType) {
                PlantType.FRIJOL -> R.drawable.b_hervestable_bug
                PlantType.TOMATE -> R.drawable.t_hervestable_bug
                else -> R.drawable.young_bug // Fallback
            }
            PlantStage.FLORECIMIENTO -> when (plantType) {
                PlantType.ROSA -> R.drawable.r_flowering_bug
                PlantType.GIRASOL -> R.drawable.g_flowering_bug // (Asegúrate que exista)
                else -> R.drawable.young_bug // Fallback
            }
            // 'MUERTA' ya se manejó
            else -> R.drawable.young
        }
    }
    // --- FIN DEL CAMBIO #3 ---

    // 3. Lógica de Salud (isSad) - Se ejecuta SOLO si no está 'MUERTA' y no 'hasPest'
    val isSad = health <= 30f

    // 3.a. Etapas Generales (de img_general)
    // Estas son iguales para todas las plantas.
    when (stage) {
        // PlantStage.MUERTA -> return R.drawable.dead // <-- MOVIDO ARRIBA
        PlantStage.GERMINACION -> return if (isSad) R.drawable.germination_sad else R.drawable.germination
        PlantStage.PLANTULA -> return if (isSad) R.drawable.seedling_sad else R.drawable.seedling
        PlantStage.JOVEN -> return if (isSad) R.drawable.young_sad else R.drawable.young
        else -> { /* No es una etapa general, continuar a lógicas específicas */ }
    }

    // 3.b. Etapas Específicas (varían por tipo de planta)
    return when (stage) {
        PlantStage.SEMILLA -> when (plantType) {
            PlantType.FRIJOL -> if (isSad) R.drawable.b_seed_sad else R.drawable.b_seed
            PlantType.ROSA -> if(isSad) R.drawable.r_seed_sad else R.drawable.r_seed
            PlantType.TOMATE -> if(isSad) R.drawable.t_seed_sad else R.drawable.t_seed
            PlantType.GIRASOL -> if(isSad) R.drawable.g_seed_sad else R.drawable.g_seed
            else -> R.drawable.b_seed // Fallback para Rabano, Lechuga, etc.
        }
        PlantStage.MADURO -> when (plantType) {
            PlantType.FRIJOL -> if (isSad) R.drawable.b_mature_sad else R.drawable.b_mature
            PlantType.TOMATE -> if (isSad) R.drawable.t_mature_sad else R.drawable.t_mature
            PlantType.ROSA -> if (isSad) R.drawable.r_mature_sad else R.drawable.r_mature
            PlantType.GIRASOL -> if (isSad) R.drawable.g_mature_sad else R.drawable.g_mature
            PlantType.ROSA -> if (isSad) R.drawable.r_mature_sad else R.drawable.r_mature
            else -> if (isSad) R.drawable.young_sad else R.drawable.young // Fallback
        }
        PlantStage.COSECHABLE -> when (plantType) {
            PlantType.FRIJOL -> if (isSad) R.drawable.b_hervestable_sad else R.drawable.b_hervestable
            PlantType.TOMATE -> if (isSad) R.drawable.t_hervestable_sad else R.drawable.t_hervestable
            PlantType.GIRASOL -> if (isSad) R.drawable.g_fh_sad else R.drawable.g_hervesrable // Caso especial Girasol Sad
            else -> if (isSad) R.drawable.young_sad else R.drawable.young // Fallback
        }
        PlantStage.FLORECIMIENTO -> when (plantType) {
            PlantType.ROSA -> if (isSad) R.drawable.r_flowering_sad else R.drawable.r_flowering
            PlantType.GIRASOL -> if (isSad) R.drawable.g_fh_sad else R.drawable.g_flowering // Caso especial Girasol Sad
            else -> if (isSad) R.drawable.young_sad else R.drawable.young // Fallback
        }
        // Fallback general si alguna combinación no se encuentra
        else -> if (isSad) R.drawable.young_sad else R.drawable.young
    }
}

// --- INICIO DEL CAMBIO #4 ---
// Esta función se mantiene sin cambios, ya que sigue usando Canvas
// ELIMINADA
/*
private fun DrawScope.drawPests(x: Float, y: Float) {
    // Dibujar pequeños bichos
    for (i in 0..2) {
        val offsetX = (i - 1) * 15f
        drawCircle(
            color = Color.Black,
            radius = 4f,
            center = Offset(x + offsetX, y + sin(i * 45.0).toFloat() * 10f)
        )
    }
}
*/
// --- FIN DEL CAMBIO #4 ---