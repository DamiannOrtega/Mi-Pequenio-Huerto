package com.example.miprimerhuerto.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miprimerhuerto.data.model.*
import com.example.miprimerhuerto.data.repository.GameRepository
import com.example.miprimerhuerto.notifications.PlantNotificationWorker
import com.example.miprimerhuerto.utils.DebugConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _uiEvent = MutableStateFlow<UiEvent?>(null)
    val uiEvent: StateFlow<UiEvent?> = _uiEvent.asStateFlow()
    
    init {
        loadGameState()
        startGameLoop()
    }
    
    private fun loadGameState() {
        viewModelScope.launch {
            repository.gameStateFlow.collect { state ->
                _gameState.value = state
            }
        }
    }
    
    // Game loop - actualiza el estado de la planta cada segundo
    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // Actualizar cada segundo
                updatePlantState()
            }
        }
    }
    
    private suspend fun updatePlantState() {
        val currentState = _gameState.value
        
        // Actualizar todas las plantas en todas las macetas
        val updatedPots = currentState.plantPots.map { pot ->
            if (pot.plant != null && !pot.plant.isDead()) {
                val updatedPlant = updateSinglePlant(pot.plant)
                pot.copy(plant = updatedPlant)
            } else {
                pot
            }
        }
        
        val newState = currentState.copy(plantPots = updatedPots)
        saveGameState(newState)
    }
    
    private suspend fun updateSinglePlant(plant: Plant): Plant {
        val plantInfo = PlantTypeData.getInfo(plant.type)
        
        // Calcular consumo de agua por segundo
        // waterConsumptionRate es % por hora, lo dividimos entre 3600 segundos
        val waterPerSecond = plantInfo.waterConsumptionRate / 3600f
        
        var newWaterLevel = (plant.waterLevel - waterPerSecond).coerceIn(0f, 100f)
        var newHealth = plant.health
        
        DebugConfig.log("Planta ${plant.type}: Agua: ${newWaterLevel.toInt()}% | Salud: ${newHealth.toInt()}% | Consumo/s: $waterPerSecond")
        
        // Si el agua está baja, disminuir la salud
        if (newWaterLevel < 25f) {
            newHealth -= 0.5f // Disminuir 0.5% de salud por segundo cuando el agua es crítica
        } else if (newWaterLevel < 50f) {
            newHealth -= 0.1f // Disminuir 0.1% de salud por segundo cuando el agua es baja
        }
        
        // Si tiene plaga, disminuir la salud
        if (plant.hasPest) {
            newHealth -= 0.3f // Disminuir 0.3% de salud por segundo con plaga
        }
        
        // Si el agua está bien y no hay plaga, recuperar salud lentamente
        if (newWaterLevel >= 50f && !plant.hasPest && newHealth < 100f) {
            newHealth += 0.05f // Recuperar 0.05% de salud por segundo
        }
        
        newHealth = newHealth.coerceIn(0f, 100f)
        
        // Verificar si la planta debe morir
        val newStage = if (newHealth <= 0f) {
            PlantStage.MUERTA
        } else {
            checkStageProgression(plant, plantInfo)
        }
        
        // Generar plaga aleatoriamente (5% de probabilidad cada minuto si no tiene plaga)
        val shouldGeneratePest = !plant.hasPest && 
                                 newHealth < 80f && 
                                 Math.random() < 0.0008 // ~5% por minuto
        
        if (shouldGeneratePest) {
            emitEvent(UiEvent.PestAppeared)
        }
        
        val updatedPlant = plant.copy(
            waterLevel = newWaterLevel,
            health = newHealth,
            stage = newStage,
            hasPest = plant.hasPest || shouldGeneratePest,
            stageStartedAt = if (newStage != plant.stage) System.currentTimeMillis() else plant.stageStartedAt
        )
        
        // Notificar cambios de etapa
        if (newStage != plant.stage) {
            // Enviar notificación inmediata cuando cambia de etapa
            sendStageChangeNotification(plant.type, newStage)
            
            when (newStage) {
                PlantStage.COSECHABLE -> emitEvent(UiEvent.PlantReadyToHarvest)
                PlantStage.MUERTA -> emitEvent(UiEvent.PlantDied)
                else -> emitEvent(UiEvent.StageChanged(plant.stage, newStage))
            }
        }
        
        return updatedPlant
    }
    
    private fun checkStageProgression(plant: Plant, plantInfo: PlantTypeInfo): PlantStage {
        if (plant.stage == PlantStage.MUERTA) return PlantStage.MUERTA
        
        val timeInStage = plant.getTimeInCurrentStage()
        val stages = PlantStageData.getStagesForPlant(plantInfo.isHarvestable)
        val currentStageInfo = stages.find { it.stage == plant.stage }
        
        if (currentStageInfo == null) {
            return plant.stage
        }
        
        val stageDuration = (plantInfo.growthDuration * currentStageInfo.durationPercentage).toLong()
        
        if (timeInStage >= stageDuration) {
            return PlantStageData.getNextStage(plant.stage, plantInfo.isHarvestable) ?: plant.stage
        }
        
        return plant.stage
    }
    
    // Actions del usuario
    fun registerUser(name: String, gender: Gender) {
        viewModelScope.launch {
            val user = User(name = name, gender = gender)
            val newState = _gameState.value.copy(
                user = user,
                isFirstTime = false
            )
            saveGameState(newState)
            emitEvent(UiEvent.UserRegistered)
        }
    }
    
    fun plantSeed(plantType: PlantType) {
        viewModelScope.launch {
            val currentState = _gameState.value
            if (!currentState.canPlant(plantType)) return@launch
            
            val newPlant = Plant(type = plantType)
            val updatedSeeds = currentState.ownedSeeds.toMutableMap()
            updatedSeeds[plantType] = (updatedSeeds[plantType] ?: 0) - 1
            
            val newState = currentState.copy(
                currentPlant = newPlant,
                ownedSeeds = updatedSeeds,
                lastNotifiedStage = null // Resetear para permitir notificaciones de esta nueva planta
            )
            saveGameState(newState)
            emitEvent(UiEvent.SeedPlanted(plantType))
        }
    }
    
    fun plantSeedInPot(plantType: PlantType, potId: Int) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId)
            if (pot == null || !pot.canPlant(plantType, currentState.ownedSeeds)) return@launch
            
            val newPlant = Plant(type = plantType)
            val updatedSeeds = currentState.ownedSeeds.toMutableMap()
            updatedSeeds[plantType] = (updatedSeeds[plantType] ?: 0) - 1
            
            val updatedPots = currentState.plantPots.map { p ->
                if (p.id == potId) p.copy(plant = newPlant) else p
            }
            
            val newState = currentState.copy(
                plantPots = updatedPots,
                ownedSeeds = updatedSeeds,
                lastNotifiedStage = null
            )
            saveGameState(newState)
            emitEvent(UiEvent.SeedPlanted(plantType))
        }
    }
    
    fun buyPlantPot() {
        viewModelScope.launch {
            val currentState = _gameState.value
            if (!currentState.canBuyPlantPot()) return@launch
            
            val newPotId = currentState.plantPots.size
            val newPot = PlantPot(
                id = newPotId,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
            
            val newState = currentState.copy(
                plantPots = currentState.plantPots + newPot,
                points = currentState.points - PlantPot.POT_COST
            )
            saveGameState(newState)
            emitEvent(UiEvent.PlantPotPurchased)
        }
    }
    
    fun waterPlant(amount: Float = 10f, potId: Int = 0) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId) ?: return@launch
            val plant = pot.plant ?: return@launch
            
            if (plant.isDead()) return@launch
            
            // Verificar si la planta ya está al 100% de agua (sobreriego)
            if (plant.waterLevel >= 100f) {
                // Sobreriego: la planta pierde vida
                val overwateredPlant = plant.copy(
                    health = (plant.health - 10f).coerceAtLeast(0f), // Pierde 10% de salud
                    lastWatered = System.currentTimeMillis()
                )
                
                val updatedPots = currentState.plantPots.map { p ->
                    if (p.id == potId) p.copy(plant = overwateredPlant) else p
                }
                
                val newState = currentState.copy(plantPots = updatedPots)
                saveGameState(newState)
                emitEvent(UiEvent.Overwatered)
            } else {
                // Riego progresivo: incrementar por la cantidad especificada
                val newWaterLevel = (plant.waterLevel + amount).coerceIn(0f, 100f)
                val actualIncrement = newWaterLevel - plant.waterLevel
                
                val wateredPlant = plant.copy(
                    waterLevel = newWaterLevel,
                    lastWatered = System.currentTimeMillis()
                )
                
                // Solo dar puntos si realmente se incrementó el agua
                val plantInfo = PlantTypeData.getInfo(plant.type)
                val pointsToAdd = if (actualIncrement > 0) {
                    if (plantInfo.isHarvestable) {
                        GameState.WATER_POINTS
                    } else {
                        GameState.ORNAMENTAL_WATER_POINTS
                    }
                } else 0
                
                val updatedPots = currentState.plantPots.map { p ->
                    if (p.id == potId) p.copy(plant = wateredPlant) else p
                }
                
                val newState = currentState.copy(
                    plantPots = updatedPots,
                    points = currentState.points + pointsToAdd
                )
                saveGameState(newState)
                
                if (pointsToAdd > 0) {
                    emitEvent(UiEvent.PlantWatered(pointsToAdd))
                }
            }
        }
    }
    
    fun applyFertilizer(potId: Int = 0) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId) ?: return@launch
            val plant = pot.plant ?: return@launch
            
            if (plant.isDead() || currentState.fertilizers <= 0) return@launch
            
            val fertilizedPlant = plant.copy(
                health = (plant.health + 30f).coerceAtMost(100f),
                lastFertilized = System.currentTimeMillis()
            )
            
            val plantInfo = PlantTypeData.getInfo(plant.type)
            val fertilizerPoints = if (plantInfo.isHarvestable) {
                GameState.FERTILIZE_POINTS
            } else {
                GameState.ORNAMENTAL_FERTILIZE_POINTS
            }
            
            val updatedPots = currentState.plantPots.map { p ->
                if (p.id == potId) p.copy(plant = fertilizedPlant) else p
            }
            
            val newState = currentState.copy(
                plantPots = updatedPots,
                fertilizers = currentState.fertilizers - 1,
                points = currentState.points + fertilizerPoints
            )
            saveGameState(newState)
            emitEvent(UiEvent.FertilizerApplied(fertilizerPoints))
        }
    }
    
    fun removePest(potId: Int = 0) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId) ?: return@launch
            val plant = pot.plant ?: return@launch
            
            if (!plant.hasPest || currentState.pesticides <= 0) return@launch
            
            val treatedPlant = plant.copy(hasPest = false)
            
            val plantInfo = PlantTypeData.getInfo(plant.type)
            val pestControlPoints = if (plantInfo.isHarvestable) {
                GameState.PEST_CONTROL_POINTS
            } else {
                GameState.ORNAMENTAL_PEST_CONTROL_POINTS
            }
            
            val updatedPots = currentState.plantPots.map { p ->
                if (p.id == potId) p.copy(plant = treatedPlant) else p
            }
            
            val newState = currentState.copy(
                plantPots = updatedPots,
                pesticides = currentState.pesticides - 1,
                points = currentState.points + pestControlPoints
            )
            saveGameState(newState)
            emitEvent(UiEvent.PestRemoved(pestControlPoints))
        }
    }
    
    fun harvestPlant(potId: Int = 0) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId) ?: return@launch
            val plant = pot.plant ?: return@launch
            
            if (!plant.canHarvest()) return@launch
            
            val plantInfo = PlantTypeData.getInfo(plant.type)
            val harvestPoints = plantInfo.harvestPoints
            
            val updatedPots = currentState.plantPots.map { p ->
                if (p.id == potId) p.copy(plant = null) else p
            }
            
            val newState = currentState.copy(
                plantPots = updatedPots,
                points = currentState.points + harvestPoints,
                totalPlantsHarvested = currentState.totalPlantsHarvested + 1
            )
            saveGameState(newState)
            emitEvent(UiEvent.PlantHarvested(harvestPoints))
        }
    }
    
    fun removePlant(potId: Int = 0) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId) ?: return@launch
            val plant = pot.plant ?: return@launch
            
            val updatedPots = currentState.plantPots.map { p ->
                if (p.id == potId) p.copy(plant = null) else p
            }
            
            val newState = if (plant.isDead()) {
                currentState.copy(
                    plantPots = updatedPots,
                    totalPlantsDied = currentState.totalPlantsDied + 1
                )
            } else {
                currentState.copy(plantPots = updatedPots)
            }
            
            saveGameState(newState)
            emitEvent(UiEvent.PlantRemoved)
        }
    }
    
    fun cleanDeadPlant(potId: Int = 0) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId) ?: return@launch
            val plant = pot.plant ?: return@launch
            
            // Solo permitir limpiar plantas muertas
            if (!plant.isDead()) return@launch
            
            val updatedPots = currentState.plantPots.map { p ->
                if (p.id == potId) p.copy(plant = null) else p
            }
            
            val newState = currentState.copy(
                plantPots = updatedPots,
                points = (currentState.points - 50).coerceAtLeast(0), // Quitar 50 puntos como penalización
                totalPlantsDied = currentState.totalPlantsDied + 1
            )
            
            saveGameState(newState)
            emitEvent(UiEvent.DeadPlantCleaned(50)) // Notificar la penalización
        }
    }
    
    fun cutOrnamentalPlant(potId: Int = 0) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val pot = currentState.getPotById(potId) ?: return@launch
            val plant = pot.plant ?: return@launch
            
            val plantInfo = PlantTypeData.getInfo(plant.type)
            if (plantInfo.isHarvestable) return@launch // Solo para plantas ornamentales
            
            // Dar puntos especiales por cortar una planta ornamental en su mejor momento
            val cutPoints = GameState.ORNAMENTAL_CARE_POINTS
            
            val updatedPots = currentState.plantPots.map { p ->
                if (p.id == potId) p.copy(plant = null) else p
            }
            
            val newState = currentState.copy(
                plantPots = updatedPots,
                points = currentState.points + cutPoints
            )
            
            saveGameState(newState)
            emitEvent(UiEvent.OrnamentalPlantCut(cutPoints))
        }
    }
    
    fun buySeed(plantType: PlantType) {
        viewModelScope.launch {
            val currentState = _gameState.value
            if (!currentState.canBuySeed(plantType)) return@launch
            
            val plantInfo = PlantTypeData.getInfo(plantType)
            val updatedSeeds = currentState.ownedSeeds.toMutableMap()
            updatedSeeds[plantType] = (updatedSeeds[plantType] ?: 0) + 1
            
            val newState = currentState.copy(
                ownedSeeds = updatedSeeds,
                points = currentState.points - plantInfo.basePrice
            )
            saveGameState(newState)
            emitEvent(UiEvent.SeedPurchased(plantType))
        }
    }
    
    fun buyFertilizer() {
        viewModelScope.launch {
            val currentState = _gameState.value
            if (!currentState.canBuyFertilizer()) return@launch
            
            val newState = currentState.copy(
                fertilizers = currentState.fertilizers + 1,
                points = currentState.points - GameState.FERTILIZER_COST
            )
            saveGameState(newState)
            emitEvent(UiEvent.FertilizerPurchased)
        }
    }
    
    fun buyPesticide() {
        viewModelScope.launch {
            val currentState = _gameState.value
            if (!currentState.canBuyPesticide()) return@launch
            
            val newState = currentState.copy(
                pesticides = currentState.pesticides + 1,
                points = currentState.points - GameState.PESTICIDE_COST
            )
            saveGameState(newState)
            emitEvent(UiEvent.PesticidePurchased)
        }
    }
    
    private suspend fun saveGameState(newState: GameState) {
        _gameState.value = newState
        repository.saveGameState(newState)
    }
    
    private fun sendStageChangeNotification(plantType: PlantType, newStage: PlantStage) {
        try {
            val plantInfo = PlantTypeData.getInfo(plantType)
            val stageNames = mapOf(
                PlantStage.GERMINACION to "Germinación",
                PlantStage.PLANTULA to "Plántula",
                PlantStage.JOVEN to "Joven",
                PlantStage.MADURO to "Maduro",
                PlantStage.COSECHABLE to "Cosechable",
                PlantStage.FLORECIMIENTO to "Florecimiento"
            )
            
            val title = when (newStage) {
                PlantStage.COSECHABLE -> "🎉 ¡Lista para cosechar!"
                PlantStage.FLORECIMIENTO -> "🌸 ¡Tu planta floreció!"
                else -> "🌱 ¡Tu planta creció!"
            }
            
            val message = "Tu ${plantInfo.name} está ahora en etapa: ${stageNames[newStage]}"
            
            DebugConfig.log("Enviando notificación inmediata de cambio de etapa: $title")
            
            // Enviar notificación directamente usando el Worker
            PlantNotificationWorker.sendStageChangeNotification(
                context = getApplication(),
                title = title,
                message = message
            )
        } catch (e: Exception) {
            DebugConfig.log("Error enviando notificación de cambio de etapa: ${e.message}")
        }
    }
    
    
    private fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.value = event
            delay(100) // Pequeño delay para asegurar que el evento se consuma
            _uiEvent.value = null
        }
    }
    
    // Métodos de debug (solo en modo DEBUG)
    fun triggerDebugPest() {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        
        // Aplicar plaga a todas las plantas en todas las macetas
        val updatedPots = currentState.plantPots.map { pot ->
            if (pot.plant != null && !pot.plant.isDead()) {
                pot.copy(plant = pot.plant.copy(hasPest = true))
            } else {
                pot
            }
        }
        
        val newState = currentState.copy(plantPots = updatedPots)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("🐛 DEBUG: Plaga simulada en todas las plantas")
        }
    }
    
    fun triggerDebugLowWater() {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        
        // Aplicar agua baja a todas las plantas en todas las macetas
        val updatedPots = currentState.plantPots.map { pot ->
            if (pot.plant != null && !pot.plant.isDead()) {
                pot.copy(plant = pot.plant.copy(waterLevel = 20f)) // Agua baja
            } else {
                pot
            }
        }
        
        val newState = currentState.copy(plantPots = updatedPots)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("💧 DEBUG: Agua baja simulada (20%) en todas las plantas")
        }
    }
    
    fun triggerDebugStageChange() {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        
        // Avanzar etapa de todas las plantas en todas las macetas
        val updatedPots = currentState.plantPots.map { pot ->
            if (pot.plant != null && !pot.plant.isDead()) {
                val plant = pot.plant
                val currentStage = plant.stage
                val plantInfo = PlantTypeData.getInfo(plant.type)
                val nextStage = PlantStageData.getNextStage(currentStage, plantInfo.isHarvestable) 
                    ?: PlantStage.SEMILLA // Reiniciar si no hay siguiente etapa
                
                val updatedPlant = plant.copy(
                    stage = nextStage,
                    stageStartedAt = System.currentTimeMillis()
                )
                
                pot.copy(plant = updatedPlant)
            } else {
                pot
            }
        }
        
        val newState = currentState.copy(plantPots = updatedPots)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("🌱 DEBUG: Etapa cambiada en todas las plantas")
            
            // Enviar notificación de cambio de etapa para cada planta
            updatedPots.forEach { pot ->
                if (pot.plant != null && !pot.plant.isDead()) {
                    sendStageChangeNotification(pot.plant.type, pot.plant.stage)
                }
            }
        }
    }
    
    fun addDebugStars(amount: Int) {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        val newState = currentState.copy(
            points = currentState.points + amount
        )
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("⭐ DEBUG: +$amount estrellas añadidas")
        }
    }
    
    fun triggerDebugWaterAll() {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        
        // Regar todas las plantas en todas las macetas
        val updatedPots = currentState.plantPots.map { pot ->
            if (pot.plant != null && !pot.plant.isDead()) {
                val plant = pot.plant
                val plantInfo = PlantTypeData.getInfo(plant.type)
                val waterPoints = if (plantInfo.isHarvestable) {
                    GameState.WATER_POINTS
                } else {
                    GameState.ORNAMENTAL_WATER_POINTS
                }
                
                val wateredPlant = plant.copy(
                    waterLevel = 100f, // Agua al máximo
                    lastWatered = System.currentTimeMillis()
                )
                
                pot.copy(plant = wateredPlant)
            } else {
                pot
            }
        }
        
        val newState = currentState.copy(plantPots = updatedPots)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("💧 DEBUG: Todas las plantas regadas al máximo")
        }
    }
    
    fun triggerDebugLowerHealth() {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        
        // Bajar salud de todas las plantas en todas las macetas
        val updatedPots = currentState.plantPots.map { pot ->
            if (pot.plant != null && !pot.plant.isDead()) {
                val plant = pot.plant
                val newHealth = (plant.health - 10f).coerceAtLeast(0f)
                pot.copy(plant = plant.copy(health = newHealth))
            } else {
                pot
            }
        }
        
        val newState = currentState.copy(plantPots = updatedPots)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("❤️ DEBUG: Salud reducida (-10%) en todas las plantas")
        }
    }
    
    fun clearEvent() {
        _uiEvent.value = null
    }
}

sealed class UiEvent {
    data object UserRegistered : UiEvent()
    data class SeedPlanted(val plantType: PlantType) : UiEvent()
    data class PlantWatered(val pointsEarned: Int) : UiEvent()
    data class FertilizerApplied(val pointsEarned: Int) : UiEvent()
    data class PestRemoved(val pointsEarned: Int) : UiEvent()
    data class PlantHarvested(val pointsEarned: Int) : UiEvent()
    data object PlantRemoved : UiEvent()
    data class DeadPlantCleaned(val pointsLost: Int) : UiEvent()
    data class OrnamentalPlantCut(val pointsEarned: Int) : UiEvent()
    data class SeedPurchased(val plantType: PlantType) : UiEvent()
    data object FertilizerPurchased : UiEvent()
    data object PesticidePurchased : UiEvent()
    data object PlantPotPurchased : UiEvent()
    data object PestAppeared : UiEvent()
    data object PlantReadyToHarvest : UiEvent()
    data object PlantDied : UiEvent()
    data object Overwatered : UiEvent()
    data class StageChanged(val oldStage: PlantStage, val newStage: PlantStage) : UiEvent()
}

