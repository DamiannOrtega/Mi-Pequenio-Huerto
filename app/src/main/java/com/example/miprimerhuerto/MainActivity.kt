package com.example.miprimerhuerto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.miprimerhuerto.notifications.PlantNotificationManager
import com.example.miprimerhuerto.ui.navigation.NavGraph
import com.example.miprimerhuerto.ui.theme.MiPrimerHuertoTheme
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido - inicializar notificaciones
            PlantNotificationManager.schedulePeriodicNotifications(this)
        } else {
            // Permiso denegado - igual programamos las notificaciones
            PlantNotificationManager.schedulePeriodicNotifications(this)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar notificaciones periódicas
        PlantNotificationManager.schedulePeriodicNotifications(this)
        
        // Solicitar permiso de notificaciones si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        setContent {
            MiPrimerHuertoTheme(dynamicColor = false) {
                val navController = rememberNavController()
                val gameViewModel: GameViewModel = viewModel()
                
                NavGraph(
                    navController = navController,
                    gameViewModel = gameViewModel
                )
            }
        }
    }
}