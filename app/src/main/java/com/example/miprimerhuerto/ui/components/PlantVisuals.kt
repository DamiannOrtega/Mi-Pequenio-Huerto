package com.example.miprimerhuerto.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.example.miprimerhuerto.data.model.PlantStage
import com.example.miprimerhuerto.data.model.PlantType
import com.example.miprimerhuerto.ui.theme.*
import kotlin.math.sin

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
    
    Box(modifier = modifier.size(200.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height
            
            when (stage) {
                PlantStage.SEMILLA -> {
                    drawSeed(centerX, centerY - 20f, health)
                }
                PlantStage.GERMINACION -> {
                    drawGermination(centerX, centerY, health, swayAngle)
                }
                PlantStage.PLANTULA -> {
                    drawSeedling(centerX, centerY, health, swayAngle, plantType)
                }
                PlantStage.JOVEN -> {
                    drawYoungPlant(centerX, centerY, health, swayAngle, plantType)
                }
                PlantStage.MADURO -> {
                    drawMaturePlant(centerX, centerY, health, swayAngle, plantType)
                }
                PlantStage.COSECHABLE -> {
                    drawHarvestablePlant(centerX, centerY, health, swayAngle, plantType)
                }
                PlantStage.FLORECIMIENTO -> {
                    drawFloweringPlant(centerX, centerY, health, swayAngle, plantType)
                }
                PlantStage.MUERTA -> {
                    drawDeadPlant(centerX, centerY)
                }
            }
            
            // Dibujar plagas si las hay
            if (hasPest && stage != PlantStage.MUERTA) {
                drawPests(centerX, centerY - 50f)
            }
        }
    }
}

private fun DrawScope.drawSeed(x: Float, y: Float, health: Float) {
    val seedColor = if (health > 50f) BrownPrimary else BrownDark
    
    // Tierra
    drawCircle(
        color = BrownPrimary,
        radius = 40f,
        center = Offset(x, y + 20f)
    )
    
    // Semilla
    drawCircle(
        color = seedColor,
        radius = 15f,
        center = Offset(x, y)
    )
}

private fun DrawScope.drawGermination(x: Float, y: Float, health: Float, sway: Float) {
    // Tierra
    drawCircle(
        color = BrownPrimary,
        radius = 50f,
        center = Offset(x, y - 20f)
    )
    
    // Brote pequeño
    val stemColor = if (health > 50f) GreenPrimary else GreenDark.copy(alpha = 0.6f)
    
    drawLine(
        color = stemColor,
        start = Offset(x, y - 20f),
        end = Offset(x + sway, y - 60f),
        strokeWidth = 4f
    )
    
    // Hoja pequeña
    drawCircle(
        color = stemColor,
        radius = 8f,
        center = Offset(x + sway, y - 60f)
    )
}

private fun DrawScope.drawSeedling(x: Float, y: Float, health: Float, sway: Float, plantType: PlantType) {
    val stemColor = getPlantColor(health)
    
    // Tallo
    drawLine(
        color = stemColor,
        start = Offset(x, y),
        end = Offset(x + sway * 2, y - 80f),
        strokeWidth = 6f
    )
    
    // Hojas pequeñas
    drawLeaf(Offset(x + sway * 2 - 20f, y - 50f), 20f, stemColor)
    drawLeaf(Offset(x + sway * 2 + 20f, y - 50f), 20f, stemColor)
    drawLeaf(Offset(x + sway * 2, y - 80f), 15f, stemColor)
}

private fun DrawScope.drawYoungPlant(x: Float, y: Float, health: Float, sway: Float, plantType: PlantType) {
    val stemColor = getPlantColor(health)
    
    // Tallo principal
    drawLine(
        color = stemColor,
        start = Offset(x, y),
        end = Offset(x + sway * 2, y - 120f),
        strokeWidth = 8f
    )
    
    // Hojas medianas
    val leafPositions = listOf(
        Offset(x + sway * 2 - 30f, y - 40f),
        Offset(x + sway * 2 + 30f, y - 60f),
        Offset(x + sway * 2 - 25f, y - 80f),
        Offset(x + sway * 2 + 25f, y - 100f)
    )
    
    leafPositions.forEach { pos ->
        drawLeaf(pos, 25f, stemColor)
    }
}

private fun DrawScope.drawMaturePlant(x: Float, y: Float, health: Float, sway: Float, plantType: PlantType) {
    val stemColor = getPlantColor(health)
    
    // Tallo principal
    drawLine(
        color = stemColor,
        start = Offset(x, y),
        end = Offset(x + sway * 2, y - 150f),
        strokeWidth = 10f
    )
    
    // Hojas grandes
    val leafPositions = listOf(
        Offset(x + sway * 2 - 40f, y - 50f),
        Offset(x + sway * 2 + 40f, y - 70f),
        Offset(x + sway * 2 - 35f, y - 90f),
        Offset(x + sway * 2 + 35f, y - 110f),
        Offset(x + sway * 2 - 30f, y - 130f),
        Offset(x + sway * 2 + 30f, y - 140f)
    )
    
    leafPositions.forEach { pos ->
        drawLeaf(pos, 30f, stemColor)
    }
}

private fun DrawScope.drawHarvestablePlant(x: Float, y: Float, health: Float, sway: Float, plantType: PlantType) {
    val stemColor = getPlantColor(health)
    val fruitColor = getFruitColor(plantType)
    
    // Tallo principal
    drawLine(
        color = stemColor,
        start = Offset(x, y),
        end = Offset(x + sway * 2, y - 160f),
        strokeWidth = 12f
    )
    
    // Hojas
    val leafPositions = listOf(
        Offset(x + sway * 2 - 40f, y - 60f),
        Offset(x + sway * 2 + 40f, y - 80f),
        Offset(x + sway * 2 - 35f, y - 100f),
        Offset(x + sway * 2 + 35f, y - 120f)
    )
    
    leafPositions.forEach { pos ->
        drawLeaf(pos, 30f, stemColor)
    }
    
    // Frutos/Vegetales
    val fruitPositions = listOf(
        Offset(x + sway * 2 - 20f, y - 140f),
        Offset(x + sway * 2 + 20f, y - 150f),
        Offset(x + sway * 2, y - 160f)
    )
    
    fruitPositions.forEach { pos ->
        drawCircle(
            color = fruitColor,
            radius = 15f,
            center = pos
        )
        // Brillo en el fruto
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = 5f,
            center = Offset(pos.x - 5f, pos.y - 5f)
        )
    }
}

private fun DrawScope.drawFloweringPlant(x: Float, y: Float, health: Float, sway: Float, plantType: PlantType) {
    val stemColor = getPlantColor(health)
    val flowerColor = getFlowerColor(plantType)
    
    // Tallo principal
    drawLine(
        color = stemColor,
        start = Offset(x, y),
        end = Offset(x + sway * 2, y - 150f),
        strokeWidth = 10f
    )
    
    // Hojas
    val leafPositions = listOf(
        Offset(x + sway * 2 - 35f, y - 50f),
        Offset(x + sway * 2 + 35f, y - 70f),
        Offset(x + sway * 2 - 30f, y - 90f),
        Offset(x + sway * 2 + 30f, y - 110f)
    )
    
    leafPositions.forEach { pos ->
        drawLeaf(pos, 28f, stemColor)
    }
    
    // Flor en la parte superior
    drawFlower(Offset(x + sway * 2, y - 150f), 40f, flowerColor)
}

private fun DrawScope.drawDeadPlant(x: Float, y: Float) {
    val deadColor = Color.Gray.copy(alpha = 0.5f)
    
    // Tallo muerto caído
    drawLine(
        color = deadColor,
        start = Offset(x, y),
        end = Offset(x + 60f, y - 30f),
        strokeWidth = 8f
    )
    
    // Hojas marchitas
    drawCircle(
        color = deadColor,
        radius = 15f,
        center = Offset(x + 40f, y - 20f)
    )
    drawCircle(
        color = deadColor,
        radius = 12f,
        center = Offset(x + 60f, y - 30f)
    )
}

private fun DrawScope.drawLeaf(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size / 2)
        cubicTo(
            center.x + size / 2, center.y - size / 4,
            center.x + size / 2, center.y + size / 4,
            center.x, center.y + size / 2
        )
        cubicTo(
            center.x - size / 2, center.y + size / 4,
            center.x - size / 2, center.y - size / 4,
            center.x, center.y - size / 2
        )
        close()
    }
    drawPath(path, color = color)
    
    // Vena de la hoja
    drawLine(
        color = color.copy(alpha = 0.5f),
        start = Offset(center.x, center.y - size / 2),
        end = Offset(center.x, center.y + size / 2),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawFlower(center: Offset, size: Float, color: Color) {
    // Pétalos
    val petalCount = 6
    val angleStep = 360f / petalCount
    
    for (i in 0 until petalCount) {
        val angle = Math.toRadians((angleStep * i).toDouble())
        val petalX = center.x + (size / 2 * kotlin.math.cos(angle)).toFloat()
        val petalY = center.y + (size / 2 * kotlin.math.sin(angle)).toFloat()
        
        drawCircle(
            color = color,
            radius = size / 3,
            center = Offset(petalX, petalY)
        )
    }
    
    // Centro de la flor
    drawCircle(
        color = SunYellow,
        radius = size / 4,
        center = center
    )
}

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

private fun getPlantColor(health: Float): Color {
    return when {
        health > 70f -> GreenPrimary
        health > 40f -> GreenLight.copy(alpha = 0.8f)
        else -> GreenDark.copy(alpha = 0.6f)
    }
}

private fun getFruitColor(plantType: PlantType): Color {
    return when (plantType) {
        PlantType.FRIJOL -> Color(0xFF8B4513)
        PlantType.RABANO -> Color(0xFFE91E63)
        PlantType.LECHUGA -> GreenLight
        PlantType.TOMATE -> Color(0xFFF44336)
        else -> Color(0xFF4CAF50)
    }
}

private fun getFlowerColor(plantType: PlantType): Color {
    return when (plantType) {
        PlantType.GIRASOL -> SunYellow
        PlantType.ROSA -> Color(0xFFE91E63)
        else -> Color(0xFFFF69B4)
    }
}

