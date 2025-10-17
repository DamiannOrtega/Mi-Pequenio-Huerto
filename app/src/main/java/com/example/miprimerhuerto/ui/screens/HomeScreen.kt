package com.example.miprimerhuerto.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.data.model.*
import com.example.miprimerhuerto.ui.components.*
import com.example.miprimerhuerto.sensors.ShakeSensor
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel
import com.example.miprimerhuerto.ui.viewmodel.UiEvent
import com.example.miprimerhuerto.utils.DebugConfig
import com.example.miprimerhuerto.utils.getBackgroundForTime
import java.util.*

fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Buenos días"
        in 12..19 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToShop: () -> Unit,
    onNavigateToPlantInfo: () -> Unit,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by gameViewModel.gameState.collectAsState()
    val uiEvent by gameViewModel.uiEvent.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showPlantDialog by remember { mutableStateOf(false) }
    var showHarvestDialog by remember { mutableStateOf(false) }
    
    // Sensor de sacudida para regar la planta
    ShakeSensor(
        enabled = gameState.hasPlant(),
        onShake = {
            gameViewModel.waterPlant()
        }
    )
    
    // Manejar eventos con duración extendida usando DebugConfig
    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is UiEvent.PlantWatered -> {
                snackbarHostState.showSnackbar(
                    message = "🌱 ¡Planta regada! +${event.pointsEarned} puntos ⭐",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.FertilizerApplied -> {
                snackbarHostState.showSnackbar(
                    message = "🌿 ¡Fertilizante aplicado! +${event.pointsEarned} puntos ⭐",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PestRemoved -> {
                snackbarHostState.showSnackbar(
                    message = "🐛 ¡Plaga eliminada! +${event.pointsEarned} puntos ⭐",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PlantHarvested -> {
                snackbarHostState.showSnackbar(
                    message = "🎉 ¡Cosecha exitosa! +${event.pointsEarned} puntos ⭐",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS + 2000)
                snackbarHostState.currentSnackbarData?.dismiss()
                showHarvestDialog = true
            }
            is UiEvent.SeedPlanted -> {
                snackbarHostState.showSnackbar(
                    message = "🌱 ¡Semilla plantada!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PlantDied -> {
                snackbarHostState.showSnackbar(
                    message = "💀 Tu planta ha muerto :(",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS + 2000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PestAppeared -> {
                snackbarHostState.showSnackbar(
                    message = "🐛 ¡Una plaga apareció!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PlantReadyToHarvest -> {
                snackbarHostState.showSnackbar(
                    message = "🎉 ¡Tu planta está lista para cosechar!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.Overwatered -> {
                snackbarHostState.showSnackbar(
                    message = "⚠️ ¡CUIDADO! Estás sobreregando la planta. Está perdiendo vida 💔",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS + 2000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.StageChanged -> {
                val stageNames = mapOf(
                    PlantStage.SEMILLA to "Semilla",
                    PlantStage.GERMINACION to "Germinación",
                    PlantStage.PLANTULA to "Plántula",
                    PlantStage.JOVEN to "Joven",
                    PlantStage.MADURO to "Maduro",
                    PlantStage.COSECHABLE to "Cosechable",
                    PlantStage.FLORECIMIENTO to "Florecimiento"
                )
                snackbarHostState.showSnackbar(
                    message = "🌱 ¡Tu planta creció! Ahora está en etapa: ${stageNames[event.newStage]}",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(DebugConfig.SNACKBAR_DURATION_MS)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            else -> {}
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${getGreeting()}, ${gameState.user?.name ?: "Jardinero"}",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToShop) {
                        Icon(Icons.Default.ShoppingCart, "Tienda")
                    }
                    if (gameState.currentPlant != null) {
                        IconButton(onClick = onNavigateToPlantInfo) {
                            Icon(Icons.Default.Info, "Info de planta")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(getBackgroundForTime())
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stats display
                StatsDisplay(
                    points = gameState.points
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Área de la planta
                PlantArea(
                    gameState = gameState,
                    onPlantClick = { showPlantDialog = true }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Controles de acción
                ActionControls(
                    gameState = gameState,
                    gameViewModel = gameViewModel,
                    onFertilize = { gameViewModel.applyFertilizer() },
                    onRemovePest = { gameViewModel.removePest() },
                    onHarvest = { gameViewModel.harvestPlant() },
                    onRemovePlant = { gameViewModel.removePlant() }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Inventario rápido
                QuickInventory(
                    fertilizers = gameState.fertilizers,
                    pesticides = gameState.pesticides
                )
                
                // Botón de prueba de notificaciones (solo en modo DEBUG)
                if (DebugConfig.DEBUG_MODE) {
                    val context = LocalContext.current
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            // Llamar directamente a la función estática del Worker
                            com.example.miprimerhuerto.notifications.PlantNotificationWorker.sendTestNotification(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🧪 Prueba de Notificación (DEBUG)")
                    }
                    
                    // Botones adicionales de debug
                    Button(
                        onClick = {
                            gameViewModel.triggerDebugPest()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800).copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.BugReport, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🐛 Simular Plaga")
                    }
                    
                    Button(
                        onClick = {
                            gameViewModel.triggerDebugLowWater()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3).copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("💧 Simular Agua Baja")
                    }
                    
                    Button(
                        onClick = {
                            gameViewModel.triggerDebugStageChange()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🌱 Simular Cambio de Etapa")
                    }
                }
            }
        }
    }
    
    // Diálogo para plantar semilla
    if (showPlantDialog) {
        PlantSeedDialog(
            gameState = gameState,
            onDismiss = { showPlantDialog = false },
            onPlant = { plantType ->
                gameViewModel.plantSeed(plantType)
                showPlantDialog = false
            }
        )
    }
}

@Composable
fun PlantArea(
    gameState: GameState,
    onPlantClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Maceta con planta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                // Maceta (fondo)
                PlantPot(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    hasPlant = gameState.currentPlant != null
                )
                
                // Planta superpuesta
                if (gameState.currentPlant != null) {
                    PlantVisualization(
                        plantType = gameState.currentPlant.type,
                        stage = gameState.currentPlant.stage,
                        health = gameState.currentPlant.health,
                        hasPest = gameState.currentPlant.hasPest,
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = (-20).dp) // Ajustar para que la base esté en la maceta
                    )
                }
                
                // Contador de tiempo para siguiente etapa
                if (gameState.currentPlant != null && !gameState.currentPlant.isDead()) {
                    TimeToNextStage(
                        plant = gameState.currentPlant,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información de la planta o botón para plantar
            if (gameState.currentPlant != null) {
                PlantInfo(plant = gameState.currentPlant)
            } else {
                Button(
                    onClick = onPlantClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, "Plantar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Plantar Semilla")
                }
            }
        }
    }
}

@Composable
fun PlantInfo(plant: Plant) {
    val plantInfo = PlantTypeData.getInfo(plant.type)
    val stageInfo = PlantStageData.getStageInfo(plant.stage, plantInfo.isHarvestable)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = plantInfo.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = GreenDark
        )
        
        Text(
            text = stageInfo?.name ?: "Desconocido",
            fontSize = 12.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Barras de estado
        HealthBar(
            health = plant.health,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
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
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WaterButton(
    enabled: Boolean,
    onWater: () -> Unit,
    onWateringStart: () -> Unit,
    onWateringStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "waterButtonScale"
    )
    
    Column(
        modifier = modifier
            .scale(scale)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        val released = tryAwaitRelease()
                        isPressed = false
                        if (released) {
                            onWateringStop()
                        }
                    },
                    onLongPress = {
                        // Mantener presionado: iniciar riego continuo
                        onWateringStart()
                    },
                    onTap = {
                        // Clic simple: regar 10%
                        onWater()
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) WaterBlue else Color.Gray.copy(alpha = 0.5f)
                )
                .border(
                    width = 2.dp,
                    color = if (enabled) WaterBlue.copy(alpha = 0.3f) else Color.Gray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Water,
                contentDescription = "Regar",
                tint = if (enabled) Color.White else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Regar",
            fontSize = 12.sp,
            color = if (enabled) GreenDark else Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActionControls(
    gameState: GameState,
    gameViewModel: GameViewModel,
    onFertilize: () -> Unit,
    onRemovePest: () -> Unit,
    onHarvest: () -> Unit,
    onRemovePlant: () -> Unit
) {
    val plant = gameState.currentPlant
    val hasPlant = plant != null && !plant.isDead()
    
    // Estados para el botón de regar con presionar y mantener
    var isWatering by remember { mutableStateOf(false) }
    
    // LaunchedEffect para riego continuo cuando se mantiene presionado
    LaunchedEffect(isWatering) {
        while (isWatering) {
            gameViewModel.waterPlant(amount = 2f) // 2% cada 100ms cuando se mantiene presionado
            kotlinx.coroutines.delay(100)
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Acciones",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de regar personalizado con clic y mantener presionado
                WaterButton(
                    enabled = hasPlant,
                    onWater = {
                        gameViewModel.waterPlant(amount = 10f) // 10% por clic
                    },
                    onWateringStart = { isWatering = true },
                    onWateringStop = { isWatering = false }
                )
                
                ActionButton(
                    icon = Icons.Default.Grass,
                    label = "Fertilizar",
                    onClick = onFertilize,
                    enabled = hasPlant && gameState.fertilizers > 0,
                    backgroundColor = BrownPrimary
                )
                
                ActionButton(
                    icon = Icons.Default.BugReport,
                    label = "Anti-Plaga",
                    onClick = onRemovePest,
                    enabled = hasPlant && plant?.hasPest == true && gameState.pesticides > 0,
                    backgroundColor = Color(0xFFFF5722)
                )
                
                if (plant?.canHarvest() == true) {
                    ActionButton(
                        icon = Icons.Default.EnergySavingsLeaf,
                        label = "Cosechar",
                        onClick = onHarvest,
                        enabled = true,
                        backgroundColor = SunYellow
                    )
                } else if (plant?.isDead() == true) {
                    ActionButton(
                        icon = Icons.Default.Delete,
                        label = "Limpiar",
                        onClick = onRemovePlant,
                        enabled = true,
                        backgroundColor = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun QuickInventory(
    fertilizers: Int,
    pesticides: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InventoryItem(
                icon = Icons.Default.Grass,
                label = "Fertilizantes",
                count = fertilizers
            )
            
            InventoryItem(
                icon = Icons.Default.BugReport,
                label = "Pesticidas",
                count = pesticides
            )
        }
    }
}

@Composable
fun InventoryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GreenPrimary,
                modifier = Modifier.size(32.dp)
            )
            
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .background(Color.Red, shape = androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun PlantSeedDialog(
    gameState: GameState,
    onDismiss: () -> Unit,
    onPlant: (PlantType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Elige una semilla",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                gameState.ownedSeeds.forEach { (plantType, count) ->
                    if (count > 0) {
                        val plantInfo = PlantTypeData.getInfo(plantType)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = { onPlant(plantType) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = plantInfo.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = plantInfo.description,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Text(
                                    text = "x$count",
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun TimeToNextStage(
    plant: Plant,
    modifier: Modifier = Modifier
) {
    val plantInfo = PlantTypeData.getInfo(plant.type)
    val stages = PlantStageData.getStagesForPlant(plantInfo.isHarvestable)
    val currentStageInfo = stages.find { it.stage == plant.stage }
    
    // Si es la última etapa o no encontramos info, no mostramos nada
    if (currentStageInfo == null || plant.stage == PlantStage.COSECHABLE || 
        plant.stage == PlantStage.FLORECIMIENTO) {
        return
    }
    
    // Calcular tiempo restante para siguiente etapa
    val stageDuration = (plantInfo.growthDuration * currentStageInfo.durationPercentage).toLong()
    val timeInCurrentStage = plant.getTimeInCurrentStage()
    val timeRemaining = (stageDuration - timeInCurrentStage).coerceAtLeast(0)
    
    // Convertir a horas y minutos
    val hoursRemaining = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(timeRemaining)
    val minutesRemaining = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
    
    // Actualizar cada segundo
    var currentTime by remember { mutableStateOf(timeRemaining) }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            currentTime = (stageDuration - plant.getTimeInCurrentStage()).coerceAtLeast(0)
        }
    }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenPrimary.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Tiempo",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            
            Column {
                Text(
                    text = "Siguiente etapa",
                    fontSize = 9.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (hoursRemaining > 0) {
                        "${hoursRemaining}h ${minutesRemaining}m"
                    } else {
                        "${minutesRemaining}m"
                    },
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

