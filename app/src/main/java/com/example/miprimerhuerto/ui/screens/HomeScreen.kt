package com.example.miprimerhuerto.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.data.model.*
import com.example.miprimerhuerto.ui.components.*
import com.example.miprimerhuerto.sensors.ShakeSensor
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel
import com.example.miprimerhuerto.ui.viewmodel.UiEvent
import com.example.miprimerhuerto.utils.getBackgroundForTime
import java.util.*

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
    
    // Manejar eventos con duración extendida
    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is UiEvent.PlantWatered -> {
                snackbarHostState.showSnackbar(
                    message = "🌱 ¡Planta regada! +${event.pointsEarned} puntos ⭐",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(4000) // Esperar 4 segundos
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.FertilizerApplied -> {
                snackbarHostState.showSnackbar(
                    message = "🌿 ¡Fertilizante aplicado! +${event.pointsEarned} puntos ⭐",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(4000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PestRemoved -> {
                snackbarHostState.showSnackbar(
                    message = "🐛 ¡Plaga eliminada! +${event.pointsEarned} puntos ⭐",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(4000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PlantHarvested -> {
                snackbarHostState.showSnackbar(
                    message = "🎉 ¡Cosecha exitosa! +${event.pointsEarned} puntos ⭐ +${event.coinsEarned} monedas 💰",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(5000)
                snackbarHostState.currentSnackbarData?.dismiss()
                showHarvestDialog = true
            }
            is UiEvent.SeedPlanted -> {
                snackbarHostState.showSnackbar(
                    message = "🌱 ¡Semilla plantada!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(3000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PlantDied -> {
                snackbarHostState.showSnackbar(
                    message = "💀 Tu planta ha muerto :(",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(5000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PestAppeared -> {
                snackbarHostState.showSnackbar(
                    message = "🐛 ¡Una plaga apareció!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(4000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.PlantReadyToHarvest -> {
                snackbarHostState.showSnackbar(
                    message = "🎉 ¡Tu planta está lista para cosechar!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(4000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            is UiEvent.Overwatered -> {
                snackbarHostState.showSnackbar(
                    message = "⚠️ ¡CUIDADO! Estás sobreregando la planta. Está perdiendo vida 💔",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                kotlinx.coroutines.delay(5000)
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
                        text = "Hola, ${gameState.user?.name ?: "Jardinero"}",
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
                    points = gameState.points,
                    coins = gameState.coins
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
                    onWater = { gameViewModel.waterPlant() },
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
fun ActionControls(
    gameState: GameState,
    onWater: () -> Unit,
    onFertilize: () -> Unit,
    onRemovePest: () -> Unit,
    onHarvest: () -> Unit,
    onRemovePlant: () -> Unit
) {
    val plant = gameState.currentPlant
    val hasPlant = plant != null && !plant.isDead()
    
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
                ActionButton(
                    icon = Icons.Default.Water,
                    label = "Regar",
                    onClick = onWater,
                    enabled = hasPlant,
                    backgroundColor = WaterBlue
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

