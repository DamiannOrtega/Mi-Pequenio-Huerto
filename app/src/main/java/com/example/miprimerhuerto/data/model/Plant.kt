package com.example.miprimerhuerto.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Plant(
    val type: PlantType,
    val stage: PlantStage = PlantStage.SEMILLA,
    val health: Float = 100f, // 0-100
    val waterLevel: Float = 100f, // 0-100
    val hasPest: Boolean = false,
    val plantedAt: Long = System.currentTimeMillis(),
    val lastWatered: Long = System.currentTimeMillis(),
    val lastFertilized: Long? = null,
    val stageStartedAt: Long = System.currentTimeMillis()
) {
    fun isDead(): Boolean = health <= 0f || stage == PlantStage.MUERTA
    
    fun needsWater(): Boolean = waterLevel < 50f
    
    fun needsUrgentWater(): Boolean = waterLevel < 25f
    
    fun isHealthy(): Boolean = health > 70f && waterLevel > 50f && !hasPest
    
    fun canHarvest(): Boolean {
        val plantInfo = PlantTypeData.getInfo(type)
        return plantInfo.isHarvestable && stage == PlantStage.COSECHABLE && !isDead()
    }
    
    fun getTimeInCurrentStage(): Long {
        return System.currentTimeMillis() - stageStartedAt
    }
    
    fun getPlantAge(): Long {
        return System.currentTimeMillis() - plantedAt
    }
    
    fun getTimeSinceLastWater(): Long {
        return System.currentTimeMillis() - lastWatered
    }
    
    fun getTimeSinceLastFertilizer(): Long? {
        return lastFertilized?.let { System.currentTimeMillis() - it }
    }
}

