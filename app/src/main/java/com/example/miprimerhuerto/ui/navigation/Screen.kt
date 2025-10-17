package com.example.miprimerhuerto.ui.navigation

sealed class Screen(val route: String) {
    data object Loading : Screen("loading")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Shop : Screen("shop")
    data object PlantInfo : Screen("plant_info")
}

