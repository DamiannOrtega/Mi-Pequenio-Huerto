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
        val plant = currentState.currentPlant ?: return
        
        if (plant.isDead()) return
        
        val plantInfo = PlantTypeData.getInfo(plant.type)
        
        // Calcular consumo de agua por segundo
        // waterConsumptionRate es % por hora, lo dividimos entre 3600 segundos
        val waterPerSecond = plantInfo.waterConsumptionRate / 3600f
        
        var newWaterLevel = (plant.waterLevel - waterPerSecond).coerceIn(0f, 100f)
        var newHealth = plant.health
        
        DebugConfig.log("Agua: ${newWaterLevel.toInt()}% | Salud: ${newHealth.toInt()}% | Consumo/s: $waterPerSecond")
        
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
        
        saveGameState(currentState.copy(currentPlant = updatedPlant))
    }
    
    private fun checkStageProgression(plant: Plant, plantInfo: PlantTypeInfo): PlantStage {
        if (plant.stage == PlantStage.MUERTA) return PlantStage.MUERTA
        
        val timeInStage = plant.getTimeInCurrentStage()
        val stages = PlantStageData.getStagesForPlant(plantInfo.isHarvestable)
        val currentStageInfo = stages.find { it.stage == plant.stage } ?: return plant.stage
        
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
    
    fun waterPlant(amount: Float = 10f) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val plant = currentState.currentPlant ?: return@launch
            
            if (plant.isDead()) return@launch
            
            // Verificar si la planta ya está al 100% de agua (sobreriego)
            if (plant.waterLevel >= 100f) {
                // Sobreriego: la planta pierde vida
                val overwateredPlant = plant.copy(
                    health = (plant.health - 10f).coerceAtLeast(0f), // Pierde 10% de salud
                    lastWatered = System.currentTimeMillis()
                )
                
                val newState = currentState.copy(
                    currentPlant = overwateredPlant,
                    // No se ganan puntos por sobrerregar
                )
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
                val pointsToAdd = if (actualIncrement > 0) GameState.WATER_POINTS else 0
                
                val newState = currentState.copy(
                    currentPlant = wateredPlant,
                    points = currentState.points + pointsToAdd
                )
                saveGameState(newState)
                
                if (pointsToAdd > 0) {
                    emitEvent(UiEvent.PlantWatered(pointsToAdd))
                }
            }
        }
    }
    
    fun applyFertilizer() {
        viewModelScope.launch {
            val currentState = _gameState.value
            val plant = currentState.currentPlant ?: return@launch
            
            if (plant.isDead() || currentState.fertilizers <= 0) return@launch
            
            val fertilizedPlant = plant.copy(
                health = (plant.health + 30f).coerceAtMost(100f),
                lastFertilized = System.currentTimeMillis()
            )
            
            val newState = currentState.copy(
                currentPlant = fertilizedPlant,
                fertilizers = currentState.fertilizers - 1,
                points = currentState.points + GameState.FERTILIZE_POINTS
            )
            saveGameState(newState)
            emitEvent(UiEvent.FertilizerApplied(GameState.FERTILIZE_POINTS))
        }
    }
    
    fun removePest() {
        viewModelScope.launch {
            val currentState = _gameState.value
            val plant = currentState.currentPlant ?: return@launch
            
            if (!plant.hasPest || currentState.pesticides <= 0) return@launch
            
            val treatedPlant = plant.copy(hasPest = false)
            
            val newState = currentState.copy(
                currentPlant = treatedPlant,
                pesticides = currentState.pesticides - 1,
                points = currentState.points + GameState.PEST_CONTROL_POINTS
            )
            saveGameState(newState)
            emitEvent(UiEvent.PestRemoved(GameState.PEST_CONTROL_POINTS))
        }
    }
    
    fun harvestPlant() {
        viewModelScope.launch {
            val currentState = _gameState.value
            val plant = currentState.currentPlant ?: return@launch
            
            if (!plant.canHarvest()) return@launch
            
            val plantInfo = PlantTypeData.getInfo(plant.type)
            val harvestPoints = plantInfo.harvestPoints
            
            val newState = currentState.copy(
                currentPlant = null,
                points = currentState.points + harvestPoints,
                totalPlantsHarvested = currentState.totalPlantsHarvested + 1
            )
            saveGameState(newState)
            emitEvent(UiEvent.PlantHarvested(harvestPoints))
        }
    }
    
    fun removePlant() {
        viewModelScope.launch {
            val currentState = _gameState.value
            val plant = currentState.currentPlant ?: return@launch
            
            val newState = if (plant.isDead()) {
                currentState.copy(
                    currentPlant = null,
                    totalPlantsDied = currentState.totalPlantsDied + 1
                )
            } else {
                currentState.copy(currentPlant = null)
            }
            
            saveGameState(newState)
            emitEvent(UiEvent.PlantRemoved)
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
        val plant = currentState.currentPlant ?: return
        
        val updatedPlant = plant.copy(hasPest = true)
        val newState = currentState.copy(currentPlant = updatedPlant)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("🐛 DEBUG: Plaga simulada")
        }
    }
    
    fun triggerDebugLowWater() {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        val plant = currentState.currentPlant ?: return
        
        val updatedPlant = plant.copy(waterLevel = 20f) // Agua baja
        val newState = currentState.copy(currentPlant = updatedPlant)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("💧 DEBUG: Agua baja simulada (20%)")
        }
    }
    
    fun triggerDebugStageChange() {
        if (!DebugConfig.DEBUG_MODE) return
        
        val currentState = _gameState.value
        val plant = currentState.currentPlant ?: return
        
        // Avanzar a la siguiente etapa
        val currentStage = plant.stage
        val nextStage = when (currentStage) {
            PlantStage.SEMILLA -> PlantStage.GERMINACION
            PlantStage.GERMINACION -> PlantStage.PLANTULA
            PlantStage.PLANTULA -> PlantStage.JOVEN
            PlantStage.JOVEN -> PlantStage.MADURO
            PlantStage.MADURO -> if (PlantTypeData.getInfo(plant.type).isHarvestable) PlantStage.COSECHABLE else PlantStage.FLORECIMIENTO
            PlantStage.COSECHABLE -> PlantStage.SEMILLA // Reiniciar
            PlantStage.FLORECIMIENTO -> PlantStage.SEMILLA // Reiniciar
            PlantStage.MUERTA -> PlantStage.SEMILLA // Reiniciar
        }
        
        val updatedPlant = plant.copy(
            stage = nextStage,
            stageStartedAt = System.currentTimeMillis()
        )
        val newState = currentState.copy(currentPlant = updatedPlant)
        
        viewModelScope.launch {
            saveGameState(newState)
            DebugConfig.log("🌱 DEBUG: Etapa cambiada de $currentStage a $nextStage")
            
            // Enviar notificación de cambio de etapa
            sendStageChangeNotification(plant.type, nextStage)
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
    data class SeedPurchased(val plantType: PlantType) : UiEvent()
    data object FertilizerPurchased : UiEvent()
    data object PesticidePurchased : UiEvent()
    data object PestAppeared : UiEvent()
    data object PlantReadyToHarvest : UiEvent()
    data object PlantDied : UiEvent()
    data object Overwatered : UiEvent()
    data class StageChanged(val oldStage: PlantStage, val newStage: PlantStage) : UiEvent()
}

