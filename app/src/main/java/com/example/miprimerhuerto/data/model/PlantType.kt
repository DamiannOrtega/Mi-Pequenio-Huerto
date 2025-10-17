package com.example.miprimerhuerto.data.model

import com.example.miprimerhuerto.utils.DebugConfig
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
    val description: String, // Descripción corta para selección y tienda
    val detailedDescription: String, // Descripción detallada para "Más información"
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
            detailedDescription = "🌱 **¡Tu primera planta!**\n\n" +
                    "**¿Sabías que?** Los frijoles son súper especiales porque pueden crecer muy rápido. ¡En solo 10 días ya puedes cosecharlos!\n\n" +
                    "**Características especiales:**\n" +
                    "• 🌱 Crece muy rápido (perfecto para principiantes)\n" +
                    "• 💧 No necesita mucha agua\n" +
                    "• 🥗 Te da frijoles deliciosos para comer\n" +
                    "• 🌿 Sus hojas son verdes y brillantes\n\n" +
                    "**¡Dato curioso!** Los frijoles son ricos en proteínas, ¡como la carne pero de las plantas!",
            growthDuration = DebugConfig.getAdjustedDuration(10 * 60 * 60 * 1000L), // 10 horas en producción, 10 min en debug
            isHarvestable = true,
            waterConsumptionRate = 5f * DebugConfig.TIME_MULTIPLIER, // Ajustado por velocidad
            basePrice = 0, // Gratis al inicio
            harvestPoints = 50
        ),
        PlantType.RABANO to PlantTypeInfo(
            type = PlantType.RABANO,
            name = "Rábano",
            description = "Crece rápido y es fácil de cuidar",
            detailedDescription = "🥕 **¡El más rápido del huerto!**\n\n" +
                    "**¿Sabías que?** Los rábanos son los campeones de velocidad. ¡Solo necesitan 7 días para estar listos!\n\n" +
                    "**Características especiales:**\n" +
                    "• ⚡ Es el más rápido en crecer\n" +
                    "• 🔴 Su raíz es roja y crujiente\n" +
                    "• 💧 Le gusta el agua regular\n" +
                    "• 🥗 Perfecto para ensaladas\n" +
                    "• 🌱 Sus hojas también se pueden comer\n\n" +
                    "**¡Dato curioso!** Los rábanos son como pequeños cohetes que crecen hacia abajo en la tierra. ¡Pum!",
            growthDuration = DebugConfig.getAdjustedDuration(7 * 60 * 60 * 1000L), // 7 horas en producción, 7 min en debug
            isHarvestable = true,
            waterConsumptionRate = 6f * DebugConfig.TIME_MULTIPLIER,
            basePrice = 20,
            harvestPoints = 40
        ),
        PlantType.LECHUGA to PlantTypeInfo(
            type = PlantType.LECHUGA,
            name = "Lechuga",
            description = "Una verdura fresca y saludable",
            detailedDescription = "🥬 **¡La reina de las ensaladas!**\n\n" +
                    "**¿Sabías que?** La lechuga es como una rosa de hojas verdes que se abre poco a poco. ¡Es súper fresca!\n\n" +
                    "**Características especiales:**\n" +
                    "• 🥬 Sus hojas son grandes y suaves\n" +
                    "• 💚 Verde como la hierba del jardín\n" +
                    "• 💧 Le gusta mucho el agua\n" +
                    "• 🥗 Es la base de todas las ensaladas\n" +
                    "• 🌱 Crece en capas como un acordeón\n\n" +
                    "**¡Dato curioso!** La lechuga tiene 95% de agua, ¡casi es agua con forma de planta!",
            growthDuration = DebugConfig.getAdjustedDuration(12 * 60 * 60 * 1000L), // 12 horas en producción, 12 min en debug
            isHarvestable = true,
            waterConsumptionRate = 7f * DebugConfig.TIME_MULTIPLIER,
            basePrice = 30,
            harvestPoints = 60
        ),
        PlantType.GIRASOL to PlantTypeInfo(
            type = PlantType.GIRASOL,
            name = "Girasol",
            description = "Una hermosa flor que sigue al sol",
            detailedDescription = "🌻 **¡El gigante del jardín!**\n\n" +
                    "**¿Sabías que?** Los girasoles son como pequeños soles que siguen al sol durante todo el día. ¡Son súper altos!\n\n" +
                    "**Características especiales:**\n" +
                    "• 🌻 Su flor es amarilla como el sol\n" +
                    "• 📏 Puede crecer muy alto (¡hasta 3 metros!)\n" +
                    "• ☀️ Siempre mira hacia el sol\n" +
                    "• 🌱 Sus semillas son deliciosas\n" +
                    "• 🐝 Atrae abejas y mariposas\n\n" +
                    "**¡Dato curioso!** Un girasol puede tener hasta 2000 semillas en su centro. ¡Es como un banco de semillas!",
            growthDuration = DebugConfig.getAdjustedDuration(15 * 60 * 60 * 1000L), // 15 horas en producción, 15 min en debug
            isHarvestable = false,
            waterConsumptionRate = 8f * DebugConfig.TIME_MULTIPLIER,
            basePrice = 100,
            harvestPoints = 100
        ),
        PlantType.ROSA to PlantTypeInfo(
            type = PlantType.ROSA,
            name = "Rosa",
            description = "La flor del amor y la belleza",
            detailedDescription = "🌹 **¡La princesa del jardín!**\n\n" +
                    "**¿Sabías que?** Las rosas son las flores más famosas del mundo. ¡Huelen súper rico y son muy elegantes!\n\n" +
                    "**Características especiales:**\n" +
                    "• 🌹 Sus pétalos son suaves como seda\n" +
                    "• 🌸 Puede ser roja, rosa, blanca o amarilla\n" +
                    "• 🌿 Tiene espinas para protegerse\n" +
                    "• 🌺 Huele delicioso\n" +
                    "• 💕 Es símbolo del amor y la amistad\n\n" +
                    "**¡Dato curioso!** Las rosas pueden vivir muchos años. ¡Algunas tienen más de 100 años!",
            growthDuration = DebugConfig.getAdjustedDuration(20 * 60 * 60 * 1000L), // 20 horas en producción, 20 min en debug
            isHarvestable = false,
            waterConsumptionRate = 6f * DebugConfig.TIME_MULTIPLIER,
            basePrice = 150,
            harvestPoints = 150
        ),
        PlantType.TOMATE to PlantTypeInfo(
            type = PlantType.TOMATE,
            name = "Tomate",
            description = "Jugoso y delicioso",
            detailedDescription = "🍅 **¡El rey de la cocina!**\n\n" +
                    "**¿Sabías que?** Los tomates son en realidad frutas, ¡no verduras! Son súper jugosos y deliciosos.\n\n" +
                    "**Características especiales:**\n" +
                    "• 🍅 Sus frutos son rojos y brillantes\n" +
                    "• 💧 Necesita mucha agua para crecer\n" +
                    "• 🥗 Se puede comer crudo o cocido\n" +
                    "• 🌱 Sus hojas tienen un olor especial\n" +
                    "• 🍝 Es la base de la salsa de pasta\n\n" +
                    "**¡Dato curioso!** Los tomates vienen de América. ¡Los españoles los trajeron a Europa hace muchos años!",
            growthDuration = DebugConfig.getAdjustedDuration(14 * 60 * 60 * 1000L), // 14 horas en producción, 14 min en debug
            isHarvestable = true,
            waterConsumptionRate = 9f * DebugConfig.TIME_MULTIPLIER,
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

