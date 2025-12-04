package com.example.miprimerhuerto.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.data.model.*
import com.example.miprimerhuerto.ui.screens.TimeToNextStage
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel

@Composable
fun PlantInfo(plant: Plant) {
    val plantInfo = PlantTypeData.getInfo(plant.type)
    val stageInfo = PlantStageData.getStageInfo(plant.stage, plantInfo.isHarvestable)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = plantInfo.name,
                style = MaterialTheme.typography.titleSmall,
                color = GreenDark,
                fontWeight = FontWeight.Bold
            )
            
            // Indicador especial para plantas ornamentales
            if (!plantInfo.isHarvestable) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE91E63).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "✨ +3x",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        
        Text(
            text = if (plant.isDead()) "💀 Muerto" else (stageInfo?.name ?: "Desconocido"),
            style = MaterialTheme.typography.bodySmall,
            color = if (plant.isDead()) Color.Red else Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Barras de estado
        HealthBar(
            health = plant.health,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        WaterBar(
            waterLevel = plant.waterLevel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        
        if (plant.hasPest) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⚠️ ¡Tiene plagas!",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlantPotActions(
    pot: PlantPot,
    plant: Plant,
    gameState: GameState,
    gameViewModel: GameViewModel
) {
    val plantInfo = PlantTypeData.getInfo(plant.type)
    
    // Estados para el botón de regar con presionar y mantener
    var isWatering by remember { mutableStateOf(false) }
    
    // LaunchedEffect para riego continuo cuando se mantiene presionado
    LaunchedEffect(isWatering) {
        while (isWatering) {
            gameViewModel.waterPlant(amount = 2f, potId = pot.id)
            kotlinx.coroutines.delay(100)
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Botón de regar
        Button(
            onClick = {
                gameViewModel.waterPlant(amount = 10f, potId = pot.id)
            },
            enabled = !plant.isDead(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isWatering = true
                            tryAwaitRelease()
                            isWatering = false
                        }
                    )
                }
        ) {
            Icon(Icons.Default.WaterDrop, "Regar", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Regar", fontSize = 12.sp)
        }
        
        // Botones de fertilizar y anti-plaga en una fila
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Botón de fertilizar
            Button(
                onClick = {
                    gameViewModel.applyFertilizer(pot.id)
                },
                enabled = !plant.isDead() && gameState.fertilizers > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Eco, "Fertilizar", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Fert.", fontSize = 10.sp)
            }
            
            // Botón de anti-plaga
            Button(
                onClick = {
                    gameViewModel.removePest(pot.id)
                },
                enabled = plant.hasPest && gameState.pesticides > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.BugReport, "Anti-plaga", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Plaga", fontSize = 10.sp)
            }
        }
        
        // Botón de cosechar/cortar/limpiar
        if (plant.isDead()) {
            // Planta muerta - mostrar botón de limpiar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mensaje de planta muerta
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Red.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💀 Planta muerta",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
                
                // Botón para limpiar (penalización de 50 puntos)
                Button(
                    onClick = {
                        gameViewModel.cleanDeadPlant(pot.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CleaningServices, "Limpiar", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar", fontSize = 12.sp)
                }
                
                // Aviso de penalización
                Text(
                    text = "⚠️ Se quitarán 50⭐ por descuido",
                    color = Color.Red,
                    fontSize = 10.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        } else if (plant.canHarvest()) {
            Button(
                onClick = {
                    gameViewModel.harvestPlant(pot.id)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SunYellow
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.EnergySavingsLeaf, "Cosechar", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cosechar", fontSize = 12.sp)
            }
        } else if (!plantInfo.isHarvestable && plant.stage == PlantStage.FLORECIMIENTO) {
            // Botón "Cortar" para plantas ornamentales en etapa máxima
            Button(
                onClick = {
                    gameViewModel.cutOrnamentalPlant(pot.id)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ContentCut, "Cortar", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cortar", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PlantPotsCarousel(
    gameState: GameState,
    gameViewModel: GameViewModel,
    onPlantClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedPots = gameState.getUnlockedPots()
    val canBuyMore = gameState.canBuyPlantPot()
    val hasMultiplePots = unlockedPots.size > 1 || canBuyMore
    
    Box(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
        items(unlockedPots) { pot ->
            PlantPotCard(
                pot = pot,
                gameState = gameState,
                gameViewModel = gameViewModel,
                onPlantClick = { onPlantClick(pot.id) },
                modifier = Modifier.width(300.dp)
            )
        }
        
        // Card para comprar nueva maceta
        if (gameState.canBuyPlantPot()) {
            item {
                BuyPotCard(
                    onBuyPot = { gameViewModel.buyPlantPot() },
                    cost = PlantPot.POT_COST,
                    modifier = Modifier.width(300.dp)
                )
            }
        }
    }

        // Indicadores de navegación
        if (hasMultiplePots) {
            // Flecha derecha
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { /* Scroll to next */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Siguiente",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun PlantPotCard(
    pot: PlantPot,
    gameState: GameState,
    gameViewModel: GameViewModel,
    onPlantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasPlant = pot.hasPlant()
    val hasLivingPlant = pot.hasLivingPlant()
    val plant = pot.plant
    var isFlipped by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    
    // Animación de giro más suave
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "card_rotation"
    )
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ícono de información/regreso (solo si hay planta)
            if (hasPlant && plant != null) {
                IconButton(
                    onClick = { isFlipped = !isFlipped },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                        .background(
                            if (isFlipped) Color.Red.copy(alpha = 0.1f) else Color.Blue.copy(alpha = 0.1f),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Icon(
                        if (isFlipped) Icons.Default.ArrowBack else Icons.Default.Info,
                        contentDescription = if (isFlipped) "Volver a la maceta" else "Información de la planta",
                        tint = if (isFlipped) Color.Red else Color.Blue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Contenido con animación limpia
            AnimatedContent(
                targetState = isFlipped,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if (isFlipped) it else -it },
                        animationSpec = tween(400, easing = EaseInOutCubic)
                    ) + fadeIn(animationSpec = tween(400)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { if (isFlipped) -it else it },
                        animationSpec = tween(400, easing = EaseInOutCubic)
                    ) + fadeOut(animationSpec = tween(400))
                },
                modifier = Modifier.fillMaxWidth()
            ) { flipped ->
                if (flipped && hasPlant && plant != null) {
                    // Reverso: Información detallada de la planta
                    PlantInfoCard(plant = plant)
                } else {
                    // Frente: Vista normal de la maceta
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Número de maceta
                        Text(
                            text = "Maceta ${pot.id + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Área de la maceta
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Maceta (fondo)
                            PlantPot(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                hasPlant = hasPlant
                            )
                            
                            // Planta superpuesta
                            if (hasPlant && plant != null) {
                                PlantVisualization(
                                    plantType = plant.type,
                                    stage = plant.stage,
                                    health = plant.health,
                                    hasPest = plant.hasPest,
                                    modifier = Modifier
                                        .size(160.dp)
                                        .align(Alignment.BottomCenter)
                                        .offset(y = (-15).dp)
                                )
                            }
                            
                            // Contador de tiempo para siguiente etapa
                            if (hasPlant && plant != null && !plant.isDead()) {
                                TimeToNextStage(
                                    plant = plant,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Información de la planta o botón para plantar
                        if (hasPlant && plant != null) {
                            PlantInfo(plant = plant)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Controles de acción para esta maceta
                            PlantPotActions(
                                pot = pot,
                                plant = plant,
                                gameState = gameState,
                                gameViewModel = gameViewModel
                            )
                        } else if (!pot.hasLivingPlant()) {
                            // Solo mostrar botón de plantar si no hay planta viva
                            Button(
                                onClick = onPlantClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, "Plantar", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Plantar Semilla", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BuyPotCard(
    onBuyPot: () -> Unit,
    cost: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono de maceta
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GreenLight.copy(alpha = 0.2f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.Default.LocalFlorist,
                        contentDescription = "Nueva Maceta",
                        tint = GreenPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Nueva Maceta",
                style = MaterialTheme.typography.titleMedium,
                color = GreenDark,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Desbloquea un nuevo espacio para plantar",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onBuyPot,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Star, "Estrellas", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("$cost ⭐", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun PlantInfoCard(plant: Plant) {
    val plantInfo = PlantTypeData.getInfo(plant.type)
    val stageInfo = PlantStageData.getStageInfo(plant.stage, plantInfo.isHarvestable)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "Sobre ${plantInfo.name}",
            style = MaterialTheme.typography.headlineSmall,
            color = GreenDark,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Etapa actual
        Text(
            text = stageInfo?.name ?: "Desconocido",
            style = MaterialTheme.typography.titleMedium,
            color = GreenPrimary,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estado actual
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Estado Actual",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Barras de estado
            HealthBar(
                health = plant.health,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            WaterBar(
                waterLevel = plant.waterLevel,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (plant.hasPest) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ ¡Tiene plagas!",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Información detallada de la planta
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Información",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Parsear y mostrar el contenido con formato
            val lines = plantInfo.detailedDescription.split("\n")
            lines.forEach { line ->
                when {
                    line.startsWith("**") && line.endsWith("**") -> {
                        // Título principal
                        Text(
                            text = line.removeSurrounding("**"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    line.startsWith("•") -> {
                        // Lista de características
                        Row(
                            modifier = Modifier.padding(vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "  ",
                                fontSize = 12.sp
                            )
                            Text(
                                text = line,
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                    line.startsWith("**") -> {
                        // Subtítulo
                        Text(
                            text = line.removeSurrounding("**"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    line.isNotBlank() -> {
                        // Texto normal
                        Text(
                            text = line,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                    else -> {
                        // Línea vacía
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Cuidados
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Cuidados",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CareItem(
                icon = Icons.Default.Water,
                title = "Último riego",
                value = formatTimeAgo(plant.lastWatered)
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            if (plant.lastFertilized != null) {
                CareItem(
                    icon = Icons.Default.Grass,
                    title = "Último fertilizante",
                    value = formatTimeAgo(plant.lastFertilized)
                )
            } else {
                CareItem(
                    icon = Icons.Default.Grass,
                    title = "Fertilizante",
                    value = "Nunca aplicado"
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            CareItem(
                icon = Icons.Default.Schedule,
                title = "Consumo de agua",
                value = "${plantInfo.waterConsumptionRate}% por hora"
            )
        }
    }
}

@Composable
fun CareItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = WaterBlue,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diff)
    val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff)
    
    return when {
        days > 0 -> "Hace ${days}d"
        hours > 0 -> "Hace ${hours}h"
        minutes > 0 -> "Hace ${minutes}m"
        else -> "Ahora mismo"
    }
}