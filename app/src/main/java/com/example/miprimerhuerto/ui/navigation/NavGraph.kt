package com.example.miprimerhuerto.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.miprimerhuerto.ui.screens.HomeScreen
import com.example.miprimerhuerto.ui.screens.LoadingScreen
import com.example.miprimerhuerto.ui.screens.PlantInfoScreen
import com.example.miprimerhuerto.ui.screens.RegisterScreen
import com.example.miprimerhuerto.ui.screens.ShopScreen
import com.example.miprimerhuerto.ui.viewmodel.GameViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    gameViewModel: GameViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {
        composable(Screen.Loading.route) {
            LoadingScreen(
                onLoadingComplete = { isFirstTime ->
                    if (isFirstTime) {
                        navController.navigate(Screen.Register.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }
                },
                gameViewModel = gameViewModel
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                gameViewModel = gameViewModel
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToShop = {
                    navController.navigate(Screen.Shop.route)
                },
                onNavigateToPlantInfo = {
                    navController.navigate(Screen.PlantInfo.route)
                },
                gameViewModel = gameViewModel
            )
        }
        
        composable(Screen.Shop.route) {
            ShopScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                gameViewModel = gameViewModel
            )
        }
        
        composable(Screen.PlantInfo.route) {
            PlantInfoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                gameViewModel = gameViewModel
            )
        }
    }
}

