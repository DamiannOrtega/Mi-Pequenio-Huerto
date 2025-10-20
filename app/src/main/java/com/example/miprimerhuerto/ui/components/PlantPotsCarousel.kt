package com.example.miprimerhuerto.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.data.model.*
import com.example.miprimerhuerto.ui.screens.TimeToNextStage
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel

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
    onPlantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasPlant = pot.hasPlant()
    val plant = pot.plant
    
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
            } else {
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
            containerColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de maceta
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BrownLight.copy(alpha = 0.3f),
                                BrownPrimary.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = BrownPrimary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nueva maceta",
                    modifier = Modifier.size(32.dp),
                    tint = BrownPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Nueva Maceta",
                style = MaterialTheme.typography.titleMedium,
                color = GreenDark,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Desbloquea una nueva maceta para plantar más semillas",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de compra
            Button(
                onClick = onBuyPot,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SunYellow
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
            text = stageInfo?.name ?: "Desconocido",
            style = MaterialTheme.typography.bodySmall,
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
