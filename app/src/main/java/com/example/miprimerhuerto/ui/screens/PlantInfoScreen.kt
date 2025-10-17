package com.example.miprimerhuerto.ui.screens

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
import com.example.miprimerhuerto.data.model.PlantStageData
import com.example.miprimerhuerto.data.model.PlantTypeData
import com.example.miprimerhuerto.ui.components.HealthBar
import com.example.miprimerhuerto.ui.components.InfoCard
import com.example.miprimerhuerto.ui.components.PlantVisualization
import com.example.miprimerhuerto.ui.components.WaterBar
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantInfoScreen(
    onNavigateBack: () -> Unit,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by gameViewModel.gameState.collectAsState()
    val plant = gameState.currentPlant
    
    if (plant == null) {
        // Si no hay planta, volver
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }
    
    val plantInfo = PlantTypeData.getInfo(plant.type)
    val stageInfo = PlantStageData.getStageInfo(plant.stage, plantInfo.isHarvestable)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información de ${plantInfo.name}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SkyBlue, GreenLight.copy(alpha = 0.3f))
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Visualización de la planta
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        PlantVisualization(
                            plantType = plant.type,
                            stage = plant.stage,
                            health = plant.health,
                            hasPest = plant.hasPest
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Estado actual
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Estado Actual",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Etapa",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = stageInfo?.name ?: "Desconocido",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Edad",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = formatDuration(plant.getPlantAge()),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        HealthBar(
                            health = plant.health,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        WaterBar(
                            waterLevel = plant.waterLevel,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        if (plant.hasPest) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Red.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Alerta",
                                        tint = Color.Red
                                    )
                                    Text(
                                        text = "¡Tu planta tiene plagas!",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información de la planta
                InfoCard(
                    title = "Sobre ${plantInfo.name}",
                    content = plantInfo.description,
                    icon = Icons.Default.Info
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Cuidados
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = "Cuidados",
                                tint = GreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Cuidados",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        CareItem(
                            icon = Icons.Default.Water,
                            title = "Último riego",
                            value = formatTimeAgo(plant.lastWatered)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
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
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        CareItem(
                            icon = Icons.Default.Schedule,
                            title = "Consumo de agua",
                            value = "${plantInfo.waterConsumptionRate}% por hora"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Etapas de crecimiento
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Etapas",
                                tint = GreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Etapas de Crecimiento",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val stages = PlantStageData.getStagesForPlant(plantInfo.isHarvestable)
                        stages.forEach { stage ->
                            val isCurrentStage = stage.stage == plant.stage
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentStage) Icons.Default.CheckCircle else Icons.Default.Circle,
                                        contentDescription = null,
                                        tint = if (isCurrentStage) GreenPrimary else Color.LightGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    
                                    Column {
                                        Text(
                                            text = stage.name,
                                            fontSize = 16.sp,
                                            fontWeight = if (isCurrentStage) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrentStage) GreenPrimary else Color.Gray
                                        )
                                        Text(
                                            text = "${(stage.durationPercentage * 100).toInt()}% del tiempo total",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = WaterBlue,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )
    }
}

fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "Recién plantada"
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    
    return when {
        days > 0 -> "Hace ${days}d"
        hours > 0 -> "Hace ${hours}h"
        minutes > 0 -> "Hace ${minutes}m"
        else -> "Ahora mismo"
    }
}

