package com.example.miprimerhuerto.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miprimerhuerto.data.model.*
import com.example.miprimerhuerto.data.repository.GameRepository
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
        val timeSinceLastWater = plant.getTimeSinceLastWater()
        
        // Disminuir agua con el tiempo (basado en el consumo de la planta)
        val hoursPassedSinceWater = timeSinceLastWater / (1000f * 60 * 60)
        val waterDecrement = plantInfo.waterConsumptionRate * (hoursPassedSinceWater / 60) // Por minuto
        
        var newWaterLevel = (plant.waterLevel - waterDecrement).coerceIn(0f, 100f)
        var newHealth = plant.health
        
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
        
        if (newStage != plant.stage && newStage == PlantStage.COSECHABLE) {
            emitEvent(UiEvent.PlantReadyToHarvest)
        }
        
        if (plant.stage != PlantStage.MUERTA && newStage == PlantStage.MUERTA) {
            emitEvent(UiEvent.PlantDied)
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
                ownedSeeds = updatedSeeds
            )
            saveGameState(newState)
            emitEvent(UiEvent.SeedPlanted(plantType))
        }
    }
    
    fun waterPlant() {
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
                // Riego normal
                val wateredPlant = plant.copy(
                    waterLevel = 100f,
                    lastWatered = System.currentTimeMillis()
                )
                
                val newState = currentState.copy(
                    currentPlant = wateredPlant,
                    points = currentState.points + GameState.WATER_POINTS
                )
                saveGameState(newState)
                emitEvent(UiEvent.PlantWatered(GameState.WATER_POINTS))
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
            val harvestCoins = harvestPoints / 2
            
            val newState = currentState.copy(
                currentPlant = null,
                points = currentState.points + harvestPoints,
                coins = currentState.coins + harvestCoins,
                totalPlantsHarvested = currentState.totalPlantsHarvested + 1
            )
            saveGameState(newState)
            emitEvent(UiEvent.PlantHarvested(harvestPoints, harvestCoins))
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
                coins = currentState.coins - plantInfo.basePrice
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
                coins = currentState.coins - GameState.FERTILIZER_COST
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
                coins = currentState.coins - GameState.PESTICIDE_COST
            )
            saveGameState(newState)
            emitEvent(UiEvent.PesticidePurchased)
        }
    }
    
    private suspend fun saveGameState(newState: GameState) {
        _gameState.value = newState
        repository.saveGameState(newState)
    }
    
    private fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.value = event
            delay(100) // Pequeño delay para asegurar que el evento se consuma
            _uiEvent.value = null
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
    data class PlantHarvested(val pointsEarned: Int, val coinsEarned: Int) : UiEvent()
    data object PlantRemoved : UiEvent()
    data class SeedPurchased(val plantType: PlantType) : UiEvent()
    data object FertilizerPurchased : UiEvent()
    data object PesticidePurchased : UiEvent()
    data object PestAppeared : UiEvent()
    data object PlantReadyToHarvest : UiEvent()
    data object PlantDied : UiEvent()
    data object Overwatered : UiEvent()
}

