package com.example.miprimerhuerto.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.data.model.Gender
import com.example.miprimerhuerto.ui.components.GradientButton
import com.example.miprimerhuerto.ui.theme.*
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel

@Composable
fun RegisterScreen(
    onRegisterComplete: () -> Unit,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var showError by remember { mutableStateOf(false) }
    
    val uiEvent by gameViewModel.uiEvent.collectAsState()
    
    LaunchedEffect(uiEvent) {
        if (uiEvent is com.example.miprimerhuerto.ui.viewmodel.UiEvent.UserRegistered) {
            onRegisterComplete()
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SkyBlue, GreenLight.copy(alpha = 0.3f))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Bienvenido a tu huerto!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Vamos a conocernos mejor",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Campo de nombre
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "¿Cómo te llamas?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            showError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tu nombre", color = Color.Gray) },
                        singleLine = true,
                        isError = showError && name.isBlank(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = GreenPrimary
                        )
                    )
                    
                    if (showError && name.isBlank()) {
                        Text(
                            text = "Por favor ingresa tu nombre",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Selección de género
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Elige tu personaje",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Jardinero
                        CharacterCard(
                            gender = Gender.JARDINERO,
                            isSelected = selectedGender == Gender.JARDINERO,
                            onClick = {
                                selectedGender = Gender.JARDINERO
                                showError = false
                            }
                        )
                        
                        // Jardinera
                        CharacterCard(
                            gender = Gender.JARDINERA,
                            isSelected = selectedGender == Gender.JARDINERA,
                            onClick = {
                                selectedGender = Gender.JARDINERA
                                showError = false
                            }
                        )
                    }
                    
                    if (showError && selectedGender == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Por favor elige un personaje",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Botón de continuar
            GradientButton(
                text = "¡Comenzar!",
                onClick = {
                    if (name.isNotBlank() && selectedGender != null) {
                        gameViewModel.registerUser(name, selectedGender!!)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun CharacterCard(
    gender: Gender,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) GreenLight.copy(alpha = 0.2f) else Color.Transparent
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) GreenPrimary else Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCharacter(gender)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = if (gender == Gender.JARDINERO) "Jardinero" else "Jardinera",
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) GreenPrimary else Color.Gray
        )
    }
}

private fun DrawScope.drawCharacter(gender: Gender) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Cabeza
    drawCircle(
        color = Color(0xFFFFDBAC),
        radius = 20f,
        center = Offset(centerX, centerY - 15f)
    )
    
    // Cuerpo
    drawCircle(
        color = if (gender == Gender.JARDINERO) Color(0xFF42A5F5) else Color(0xFFEC407A),
        radius = 25f,
        center = Offset(centerX, centerY + 20f)
    )
    
    // Sombrero/Gorro
    val hatPath = Path().apply {
        if (gender == Gender.JARDINERO) {
            // Sombrero de jardinero
            moveTo(centerX - 25f, centerY - 35f)
            lineTo(centerX + 25f, centerY - 35f)
            lineTo(centerX + 20f, centerY - 30f)
            lineTo(centerX - 20f, centerY - 30f)
            close()
        } else {
            // Gorro con flor
            moveTo(centerX, centerY - 35f)
            lineTo(centerX - 20f, centerY - 30f)
            lineTo(centerX - 15f, centerY - 25f)
            lineTo(centerX + 15f, centerY - 25f)
            lineTo(centerX + 20f, centerY - 30f)
            close()
        }
    }
    drawPath(hatPath, BrownPrimary)
    
    // Herramienta (pala)
    drawLine(
        color = BrownPrimary,
        start = Offset(centerX - 30f, centerY + 30f),
        end = Offset(centerX - 35f, centerY + 50f),
        strokeWidth = 3f
    )
    
    // Punta de la pala
    drawCircle(
        color = Color.Gray,
        radius = 5f,
        center = Offset(centerX - 35f, centerY + 50f)
    )
    
    // Detalles
    if (gender == Gender.JARDINERA) {
        // Flor en el gorro
        drawCircle(
            color = Color(0xFFFF4081),
            radius = 6f,
            center = Offset(centerX + 15f, centerY - 32f)
        )
    }
}

