package com.example.miprimerhuerto.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val user: User? = null,
    val currentPlant: Plant? = null,
    val points: Int = 0,
    val coins: Int = 0,
    val unlockedPlants: List<PlantType> = listOf(
        PlantType.FRIJOL,
        PlantType.RABANO,
        PlantType.LECHUGA
    ),
    val ownedSeeds: Map<PlantType, Int> = mapOf(
        PlantType.FRIJOL to 3,
        PlantType.RABANO to 1,
        PlantType.LECHUGA to 1
    ),
    val fertilizers: Int = 0,
    val pesticides: Int = 0,
    val totalPlantsHarvested: Int = 0,
    val totalPlantsDied: Int = 0,
    val isFirstTime: Boolean = true
) {
    fun hasPlant(): Boolean = currentPlant != null && currentPlant.isDead().not()
    
    fun canPlant(plantType: PlantType): Boolean {
        return !hasPlant() && (ownedSeeds[plantType] ?: 0) > 0
    }
    
    fun canBuySeed(plantType: PlantType): Boolean {
        val plantInfo = PlantTypeData.getInfo(plantType)
        return coins >= plantInfo.basePrice
    }
    
    fun canBuyFertilizer(): Boolean {
        return coins >= FERTILIZER_COST
    }
    
    fun canBuyPesticide(): Boolean {
        return coins >= PESTICIDE_COST
    }
    
    companion object {
        const val FERTILIZER_COST = 10
        const val PESTICIDE_COST = 15
        const val WATER_POINTS = 5
        const val FERTILIZE_POINTS = 10
        const val PEST_CONTROL_POINTS = 15
    }
}

