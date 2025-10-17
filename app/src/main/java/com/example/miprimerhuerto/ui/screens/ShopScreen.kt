package com.example.miprimerhuerto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.miprimerhuerto.data.model.GameState
import com.example.miprimerhuerto.data.model.PlantType
import com.example.miprimerhuerto.data.model.PlantTypeData
import com.example.miprimerhuerto.ui.components.StatsDisplay
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel
import com.example.miprimerhuerto.ui.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    onNavigateBack: () -> Unit,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by gameViewModel.gameState.collectAsState()
    val uiEvent by gameViewModel.uiEvent.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is UiEvent.SeedPurchased -> {
                val plantInfo = PlantTypeData.getInfo(event.plantType)
                snackbarHostState.showSnackbar("¡${plantInfo.name} comprado!")
            }
            is UiEvent.FertilizerPurchased -> {
                snackbarHostState.showSnackbar("¡Fertilizante comprado!")
            }
            is UiEvent.PesticidePurchased -> {
                snackbarHostState.showSnackbar("¡Pesticida comprado!")
            }
            else -> {}
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tienda", fontWeight = FontWeight.Bold) },
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
                    .padding(16.dp)
            ) {
                StatsDisplay(
                    points = gameState.points
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sección de semillas
                    item {
                        Text(
                            text = "Semillas",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(PlantTypeData.getAllPlants()) { plantInfo ->
                        SeedShopItem(
                            plantInfo = plantInfo,
                            owned = gameState.ownedSeeds[plantInfo.type] ?: 0,
                            canAfford = gameState.points >= plantInfo.basePrice,
                            onBuy = {
                                gameViewModel.buySeed(plantInfo.type)
                            }
                        )
                    }
                    
                    // Sección de herramientas
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Herramientas",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    item {
                        ToolShopItem(
                            icon = Icons.Default.Grass,
                            name = "Fertilizante",
                            description = "Mejora la salud de tu planta rápidamente",
                            price = GameState.FERTILIZER_COST,
                            owned = gameState.fertilizers,
                            canAfford = gameState.points >= GameState.FERTILIZER_COST,
                            onBuy = { gameViewModel.buyFertilizer() }
                        )
                    }
                    
                    item {
                        ToolShopItem(
                            icon = Icons.Default.BugReport,
                            name = "Pesticida",
                            description = "Elimina las plagas de tu planta",
                            price = GameState.PESTICIDE_COST,
                            owned = gameState.pesticides,
                            canAfford = gameState.points >= GameState.PESTICIDE_COST,
                            onBuy = { gameViewModel.buyPesticide() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeedShopItem(
    plantInfo: com.example.miprimerhuerto.data.model.PlantTypeInfo,
    owned: Int,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = plantInfo.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                    
                    if (owned > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenLight.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "x$owned",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = plantInfo.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Tiempo",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${plantInfo.growthDuration / (1000 * 60 * 60)} horas",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Puntos",
                        tint = SunYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+${plantInfo.harvestPoints}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (plantInfo.basePrice == 0) {
                    // Semilla gratis (inicial)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GreenLight.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "🎁 Inicial",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Puntos",
                            tint = SunYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = plantInfo.basePrice.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) GreenPrimary else Color.Red
                        )
                    }
                    
                    Button(
                        onClick = onBuy,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary
                        )
                    ) {
                        Text("Comprar")
                    }
                }
            }
        }
    }
}

@Composable
fun ToolShopItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    description: String,
    price: Int,
    owned: Int,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreenLight.copy(alpha = 0.2f),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = name,
                            tint = GreenPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                        
                        if (owned > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GreenLight.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "x$owned",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Puntos",
                        tint = SunYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = price.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) GreenPrimary else Color.Red
                    )
                }
                
                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    )
                ) {
                    Text("Comprar")
                }
            }
        }
    }
}

