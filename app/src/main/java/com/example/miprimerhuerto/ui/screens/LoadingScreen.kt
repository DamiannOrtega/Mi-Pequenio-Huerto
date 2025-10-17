package com.example.miprimerhuerto.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.ui.components.AnimatedLoadingBar
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onLoadingComplete: (Boolean) -> Unit,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by gameViewModel.gameState.collectAsState()
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    
    // Animación del logo
    val infiniteTransition = rememberInfiniteTransition(label = "logo_animation")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Efecto de carga
    LaunchedEffect(Unit) {
        // Simular carga
        for (i in 1..100) {
            delay(20)
            loadingProgress = i / 100f
        }
        delay(500)
        onLoadingComplete(gameState.isFirstTime)
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SkyBlue, GreenLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Logo animado
            Box(
                modifier = Modifier
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Dibujar logo del juego (un jardín simplificado)
                    drawGameLogo(logoScale)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Título
            Text(
                text = "Mi Primer Huerto",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Aprende cultivando",
                fontSize = 16.sp,
                color = GreenPrimary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Barra de progreso personalizada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "Cargando...",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                AnimatedLoadingBar(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Plantas decorativas flotantes
        FloatingPlants()
    }
}

@Composable
fun FloatingPlants() {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    // Planta 1
    val offset1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )
    
    // Planta 2
    val offset2Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Hoja flotante 1
        Canvas(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.TopStart)
                .offset(x = 30.dp, y = (100 + offset1Y).dp)
        ) {
            drawLeaf(Offset(size.width / 2, size.height / 2), size.width / 2, GreenLight.copy(alpha = 0.6f))
        }
        
        // Hoja flotante 2
        Canvas(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-40).dp, y = (150 + offset2Y).dp)
        ) {
            drawLeaf(Offset(size.width / 2, size.height / 2), size.width / 2, GreenPrimary.copy(alpha = 0.5f))
        }
        
        // Hoja flotante 3
        Canvas(
            modifier = Modifier
                .size(45.dp)
                .align(Alignment.BottomStart)
                .offset(x = 50.dp, y = (-100 + offset1Y).dp)
        ) {
            drawLeaf(Offset(size.width / 2, size.height / 2), size.width / 2, GreenDark.copy(alpha = 0.4f))
        }
    }
}

private fun DrawScope.drawGameLogo(scale: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Sol
    drawCircle(
        color = SunYellow,
        radius = 30f * scale,
        center = Offset(centerX, centerY - 80f)
    )
    
    // Rayos del sol
    for (i in 0..7) {
        val angle = (i * 45).toDouble()
        val startX = centerX + (35f * scale * kotlin.math.cos(Math.toRadians(angle))).toFloat()
        val startY = centerY - 80f + (35f * scale * kotlin.math.sin(Math.toRadians(angle))).toFloat()
        val endX = centerX + (50f * scale * kotlin.math.cos(Math.toRadians(angle))).toFloat()
        val endY = centerY - 80f + (50f * scale * kotlin.math.sin(Math.toRadians(angle))).toFloat()
        
        drawLine(
            color = SunYellow,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 4f
        )
    }
    
    // Maceta
    val potPath = Path().apply {
        moveTo(centerX - 50f, centerY + 20f)
        lineTo(centerX - 60f, centerY + 80f)
        lineTo(centerX + 60f, centerY + 80f)
        lineTo(centerX + 50f, centerY + 20f)
        close()
    }
    drawPath(potPath, BrownPrimary)
    
    // Tierra
    drawCircle(
        color = BrownDark,
        radius = 55f,
        center = Offset(centerX, centerY + 25f)
    )
    
    // Planta en el centro
    // Tallo
    drawLine(
        color = GreenPrimary,
        start = Offset(centerX, centerY + 20f),
        end = Offset(centerX, centerY - 40f),
        strokeWidth = 8f * scale
    )
    
    // Hojas
    drawLeaf(Offset(centerX - 30f, centerY - 10f), 25f * scale, GreenPrimary)
    drawLeaf(Offset(centerX + 30f, centerY - 10f), 25f * scale, GreenPrimary)
    drawLeaf(Offset(centerX, centerY - 40f), 20f * scale, GreenLight)
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
    drawPath(path, color)
}

