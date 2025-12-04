package com.example.miprimerhuerto.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val user: User? = null,
    val currentPlant: Plant? = null, // Mantener para compatibilidad
    val plantPots: List<PlantPot> = listOf(
        PlantPot(id = 0, isUnlocked = true) // Primera maceta desbloqueada por defecto
    ),
    val points: Int = 0,
    val unlockedPlants: List<PlantType> = listOf(
        PlantType.FRIJOL,
        /*PlantType.RABANO,
        PlantType.LECHUGA*/
    ),
    val ownedSeeds: Map<PlantType, Int> = mapOf(
        PlantType.FRIJOL to 3,
        /*PlantType.RABANO to 1,
        PlantType.LECHUGA to 1*/
    ),
    val fertilizers: Int = 0,
    val pesticides: Int = 0,
    val totalPlantsHarvested: Int = 0,
    val totalPlantsDied: Int = 0,
    val isFirstTime: Boolean = true,
    val lastNotifiedStage: PlantStage? = null // Para notificaciones de cambio de etapa
) {
    fun hasPlant(): Boolean = currentPlant != null && currentPlant.isDead().not()
    
    fun canPlant(plantType: PlantType): Boolean {
        return !hasPlant() && (ownedSeeds[plantType] ?: 0) > 0
    }
    
    fun canBuySeed(plantType: PlantType): Boolean {
        val plantInfo = PlantTypeData.getInfo(plantType)
        return points >= plantInfo.basePrice
    }
    
    fun canBuyFertilizer(): Boolean {
        return points >= FERTILIZER_COST
    }
    
    fun canBuyPesticide(): Boolean {
        return points >= PESTICIDE_COST
    }
    
    fun canBuyPlantPot(): Boolean {
        return points >= PlantPot.POT_COST && plantPots.size < PlantPot.MAX_POTS
    }
    
    fun getUnlockedPots(): List<PlantPot> {
        return plantPots.filter { it.isUnlocked }
    }
    
    fun getPotById(id: Int): PlantPot? {
        return plantPots.find { it.id == id }
    }
    
    fun getAvailablePot(): PlantPot? {
        return plantPots.find { it.isUnlocked && !it.hasPlant() }
    }
    
    companion object {
        const val FERTILIZER_COST = 10
        const val PESTICIDE_COST = 15
        const val WATER_POINTS = 5
        const val FERTILIZE_POINTS = 10
        const val PEST_CONTROL_POINTS = 15
        
        // Puntos especiales para plantas ornamentales (no cosechables)
        const val ORNAMENTAL_WATER_POINTS = 15  // 3x más que plantas normales
        const val ORNAMENTAL_FERTILIZE_POINTS = 30  // 3x más que plantas normales
        const val ORNAMENTAL_PEST_CONTROL_POINTS = 45  // 3x más que plantas normales
        const val ORNAMENTAL_CARE_POINTS = 25  // Puntos por cuidar plantas ornamentales en etapa máxima
    }
}

