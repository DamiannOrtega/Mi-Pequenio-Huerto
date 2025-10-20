package com.example.miprimerhuerto.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlantPot(
    val id: Int,
    val isUnlocked: Boolean = false,
    val plant: Plant? = null,
    val unlockedAt: Long? = null
) {
    fun hasPlant(): Boolean = plant != null && !plant.isDead()
    
    fun canPlant(plantType: PlantType, ownedSeeds: Map<PlantType, Int>): Boolean {
        return isUnlocked && !hasPlant() && (ownedSeeds[plantType] ?: 0) > 0
    }
    
    companion object {
        const val POT_COST = 500 // Costo en estrellas para desbloquear una maceta
        const val MAX_POTS = 5 // Máximo número de macetas
    }
}
