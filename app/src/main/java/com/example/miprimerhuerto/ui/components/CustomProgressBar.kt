package com.example.miprimerhuerto.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.ui.theme.GreenDark
import com.example.miprimerhuerto.ui.theme.GreenLight
import com.example.miprimerhuerto.ui.theme.GreenPrimary

@Composable
fun GardenProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    backgroundColor: Color = Color.LightGray,
    progressColor: Color = GreenPrimary,
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background
            drawRoundRect(
                color = backgroundColor,
                size = size,
                cornerRadius = CornerRadius(size.height / 2, size.height / 2)
            )
            
            // Progress bar con gradiente
            if (animatedProgress > 0f) {
                val progressWidth = size.width * animatedProgress
                val gradient = Brush.horizontalGradient(
                    colors = listOf(GreenLight, GreenPrimary, GreenDark),
                    startX = 0f,
                    endX = progressWidth
                )
                
                drawRoundRect(
                    brush = gradient,
                    size = Size(progressWidth, size.height),
                    cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                )
            }
            
            // Decoraciones de hojas en la barra
            if (animatedProgress > 0.1f) {
                val leafPositions = listOf(0.2f, 0.5f, 0.8f)
                leafPositions.forEach { pos ->
                    if (animatedProgress > pos) {
                        val x = size.width * pos
                        drawLeaf(
                            center = Offset(x, size.height / 2),
                            size = size.height * 0.6f,
                            color = GreenDark.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
        
        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = if (animatedProgress > 0.5f) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeaf(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - size / 2)
        cubicTo(
            center.x + size / 3, center.y - size / 4,
            center.x + size / 3, center.y + size / 4,
            center.x, center.y + size / 2
        )
        cubicTo(
            center.x - size / 3, center.y + size / 4,
            center.x - size / 3, center.y - size / 4,
            center.x, center.y - size / 2
        )
        close()
    }
    drawPath(path, color)
}

@Composable
fun HealthBar(
    health: Float,
    modifier: Modifier = Modifier,
    label: String = "Vida"
) {
    val healthColor = when {
        health > 70f -> Color(0xFF66BB6A)
        health > 40f -> Color(0xFFFFEB3B)
        else -> Color(0xFFEF5350)
    }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${health.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = healthColor
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background
                drawRoundRect(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    size = size,
                    cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                )
                
                // Health bar
                val healthProgress = (health / 100f).coerceIn(0f, 1f)
                if (healthProgress > 0f) {
                    drawRoundRect(
                        color = healthColor,
                        size = Size(size.width * healthProgress, size.height),
                        cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                    )
                }
            }
        }
    }
}

@Composable
fun WaterBar(
    waterLevel: Float,
    modifier: Modifier = Modifier,
    label: String = "Agua"
) {
    val waterColor = when {
        waterLevel > 50f -> Color(0xFF42A5F5)
        waterLevel > 25f -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${waterLevel.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = waterColor
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background
                drawRoundRect(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    size = size,
                    cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                )
                
                // Water bar con efecto de olas
                val waterProgress = (waterLevel / 100f).coerceIn(0f, 1f)
                if (waterProgress > 0f) {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                waterColor.copy(alpha = 0.7f),
                                waterColor,
                                waterColor.copy(alpha = 0.7f)
                            )
                        ),
                        size = Size(size.width * waterProgress, size.height),
                        cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedLoadingBar(
    modifier: Modifier = Modifier,
    height: Dp = 40.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
    
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background
            drawRoundRect(
                color = Color.LightGray.copy(alpha = 0.3f),
                size = size,
                cornerRadius = CornerRadius(size.height / 2, size.height / 2)
            )
            
            // Progress
            val progressWidth = size.width * progress
            val gradient = Brush.linearGradient(
                colors = listOf(
                    GreenLight,
                    GreenPrimary,
                    GreenDark,
                    GreenPrimary,
                    GreenLight
                ),
                start = Offset(size.width * shimmer - size.width, 0f),
                end = Offset(size.width * shimmer, 0f)
            )
            
            drawRoundRect(
                brush = gradient,
                size = Size(progressWidth, size.height),
                cornerRadius = CornerRadius(size.height / 2, size.height / 2)
            )
            
            // Plantas decorativas
            val plantCount = 5
            for (i in 0 until plantCount) {
                if (progress > (i.toFloat() / plantCount)) {
                    val x = size.width * (i.toFloat() / plantCount)
                    drawLeaf(
                        center = Offset(x, size.height / 2),
                        size = size.height * 0.5f,
                        color = GreenDark.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

