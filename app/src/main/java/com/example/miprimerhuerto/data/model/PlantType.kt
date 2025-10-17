package com.example.miprimerhuerto.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class PlantType {
    FRIJOL,      // Bean
    RABANO,      // Radish
    LECHUGA,     // Lettuce
    GIRASOL,     // Sunflower (futuro)
    ROSA,        // Rose (futuro)
    TOMATE       // Tomato (futuro)
}

// Información sobre cada tipo de planta
data class PlantTypeInfo(
    val type: PlantType,
    val name: String,
    val description: String,
    val growthDuration: Long, // Duración total en milisegundos (en el juego)
    val isHarvestable: Boolean, // Si se puede cosechar o es decorativa
    val waterConsumptionRate: Float, // Velocidad a la que consume agua (% por hora)
    val basePrice: Int, // Precio base para comprar la semilla
    val harvestPoints: Int // Puntos al cosechar
)

object PlantTypeData {
    private val plantInfoMap = mapOf(
        PlantType.FRIJOL to PlantTypeInfo(
            type = PlantType.FRIJOL,
            name = "Frijol",
            description = "Una planta rápida de cultivar, ideal para principiantes",
            growthDuration = 10 * 60 * 60 * 1000L, // 10 horas = 10 días en el juego
            isHarvestable = true,
            waterConsumptionRate = 5f, // 5% por hora
            basePrice = 0, // Gratis al inicio
            harvestPoints = 50
        ),
        PlantType.RABANO to PlantTypeInfo(
            type = PlantType.RABANO,
            name = "Rábano",
            description = "Crece rápido y es fácil de cuidar",
            growthDuration = 7 * 60 * 60 * 1000L, // 7 horas = 7 días
            isHarvestable = true,
            waterConsumptionRate = 6f,
            basePrice = 20,
            harvestPoints = 40
        ),
        PlantType.LECHUGA to PlantTypeInfo(
            type = PlantType.LECHUGA,
            name = "Lechuga",
            description = "Una verdura fresca y saludable",
            growthDuration = 12 * 60 * 60 * 1000L, // 12 horas = 12 días
            isHarvestable = true,
            waterConsumptionRate = 7f,
            basePrice = 30,
            harvestPoints = 60
        ),
        PlantType.GIRASOL to PlantTypeInfo(
            type = PlantType.GIRASOL,
            name = "Girasol",
            description = "Una hermosa flor que sigue al sol",
            growthDuration = 15 * 60 * 60 * 1000L, // 15 horas = 15 días
            isHarvestable = false,
            waterConsumptionRate = 8f,
            basePrice = 100,
            harvestPoints = 100
        ),
        PlantType.ROSA to PlantTypeInfo(
            type = PlantType.ROSA,
            name = "Rosa",
            description = "La flor del amor y la belleza",
            growthDuration = 20 * 60 * 60 * 1000L, // 20 horas = 20 días
            isHarvestable = false,
            waterConsumptionRate = 6f,
            basePrice = 150,
            harvestPoints = 150
        ),
        PlantType.TOMATE to PlantTypeInfo(
            type = PlantType.TOMATE,
            name = "Tomate",
            description = "Jugoso y delicioso",
            growthDuration = 14 * 60 * 60 * 1000L, // 14 horas = 14 días
            isHarvestable = true,
            waterConsumptionRate = 9f,
            basePrice = 50,
            harvestPoints = 70
        )
    )
    
    fun getInfo(type: PlantType): PlantTypeInfo {
        return plantInfoMap[type] ?: plantInfoMap[PlantType.FRIJOL]!!
    }
    
    fun getAllPlants(): List<PlantTypeInfo> {
        return plantInfoMap.values.toList()
    }
    
    fun getInitialPlants(): List<PlantTypeInfo> {
        return listOf(
            getInfo(PlantType.FRIJOL),
            getInfo(PlantType.RABANO),
            getInfo(PlantType.LECHUGA)
        )
    }
}

