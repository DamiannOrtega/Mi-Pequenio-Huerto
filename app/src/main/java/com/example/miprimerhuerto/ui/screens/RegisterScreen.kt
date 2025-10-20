package com.example.miprimerhuerto.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miprimerhuerto.R
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
                        style = MaterialTheme.typography.headlineLarge,
                        color = GreenDark,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Vamos a conocernos mejor",
                        style = MaterialTheme.typography.bodyLarge,
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
                        style = MaterialTheme.typography.titleMedium,
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
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
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
                        style = MaterialTheme.typography.titleMedium,
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
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red
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
            Image(
                painter = painterResource(
                    id = if (gender == Gender.JARDINERO) R.drawable.jardinero else R.drawable.jardinera
                ),
                contentDescription = if (gender == Gender.JARDINERO) "Jardinero" else "Jardinera",
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = if (gender == Gender.JARDINERO) "Jardinero" else "Jardinera",
            style = if (isSelected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
            color = if (isSelected) GreenPrimary else Color.Gray
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MiPrimerHuertoTheme {
        // Para el Preview, creamos una versión simplificada sin ViewModel
        RegisterScreenContent()
    }
}

@Composable
fun RegisterScreenContent() {
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var showError by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
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
                        style = MaterialTheme.typography.headlineLarge,
                        color = GreenDark,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Vamos a conocernos mejor",
                        style = MaterialTheme.typography.bodyLarge,
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
                        style = MaterialTheme.typography.titleMedium,
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
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
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
                        style = MaterialTheme.typography.titleMedium,
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
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red
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
                        // En el preview no hacemos nada
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

@Preview(showBackground = true)
@Composable
fun CharacterCardPreview() {
    MiPrimerHuertoTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CharacterCard(
                gender = Gender.JARDINERO,
                isSelected = true,
                onClick = { }
            )
            CharacterCard(
                gender = Gender.JARDINERA,
                isSelected = false,
                onClick = { }
            )
        }
    }
}


