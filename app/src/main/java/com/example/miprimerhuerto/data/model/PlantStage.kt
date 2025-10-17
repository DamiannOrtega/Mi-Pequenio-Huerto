package com.example.miprimerhuerto.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class PlantStage {
    SEMILLA,      // Seed
    GERMINACION,  // Germination
    PLANTULA,     // Seedling
    JOVEN,        // Young
    MADURO,       // Mature
    FLORECIMIENTO,// Flowering (para plantas no cosechables)
    COSECHABLE,   // Harvestable
    MUERTA        // Dead
}

data class PlantStageInfo(
    val stage: PlantStage,
    val name: String,
    val description: String,
    val durationPercentage: Float // Porcentaje del tiempo total de crecimiento
)

object PlantStageData {
    private val harvestableStages = listOf(
        PlantStageInfo(
            stage = PlantStage.SEMILLA,
            name = "Semilla",
            description = "Una pequeña semilla plantada en la tierra",
            durationPercentage = 0.1f // 10% del tiempo total
        ),
        PlantStageInfo(
            stage = PlantStage.GERMINACION,
            name = "Germinación",
            description = "La semilla está comenzando a germinar",
            durationPercentage = 0.15f // 15%
        ),
        PlantStageInfo(
            stage = PlantStage.PLANTULA,
            name = "Plántula",
            description = "Aparecen las primeras hojas",
            durationPercentage = 0.20f // 20%
        ),
        PlantStageInfo(
            stage = PlantStage.JOVEN,
            name = "Joven",
            description = "La planta está creciendo fuerte",
            durationPercentage = 0.25f // 25%
        ),
        PlantStageInfo(
            stage = PlantStage.MADURO,
            name = "Maduro",
            description = "La planta está casi lista",
            durationPercentage = 0.20f // 20%
        ),
        PlantStageInfo(
            stage = PlantStage.COSECHABLE,
            name = "Cosechable",
            description = "¡La planta está lista para cosechar!",
            durationPercentage = 0.10f // 10%
        )
    )
    
    private val decorativeStages = listOf(
        PlantStageInfo(
            stage = PlantStage.SEMILLA,
            name = "Semilla",
            description = "Una pequeña semilla plantada en la tierra",
            durationPercentage = 0.1f
        ),
        PlantStageInfo(
            stage = PlantStage.GERMINACION,
            name = "Germinación",
            description = "La semilla está comenzando a germinar",
            durationPercentage = 0.15f
        ),
        PlantStageInfo(
            stage = PlantStage.PLANTULA,
            name = "Plántula",
            description = "Aparecen las primeras hojas",
            durationPercentage = 0.20f
        ),
        PlantStageInfo(
            stage = PlantStage.JOVEN,
            name = "Joven",
            description = "La planta está creciendo fuerte",
            durationPercentage = 0.25f
        ),
        PlantStageInfo(
            stage = PlantStage.FLORECIMIENTO,
            name = "Florecimiento",
            description = "¡La planta está floreciendo hermosamente!",
            durationPercentage = 0.30f
        )
    )
    
    fun getStagesForPlant(isHarvestable: Boolean): List<PlantStageInfo> {
        return if (isHarvestable) harvestableStages else decorativeStages
    }
    
    fun getStageInfo(stage: PlantStage, isHarvestable: Boolean): PlantStageInfo? {
        return getStagesForPlant(isHarvestable).find { it.stage == stage }
    }
    
    fun getNextStage(currentStage: PlantStage, isHarvestable: Boolean): PlantStage? {
        val stages = getStagesForPlant(isHarvestable)
        val currentIndex = stages.indexOfFirst { it.stage == currentStage }
        return if (currentIndex >= 0 && currentIndex < stages.size - 1) {
            stages[currentIndex + 1].stage
        } else {
            null
        }
    }
}

