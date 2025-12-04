package com.example.miprimerhuerto.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.miprimerhuerto.data.model.GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_data")

class GameRepository(private val context: Context) {
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    companion object {
        private val GAME_STATE_KEY = stringPreferencesKey("game_state")
    }
    
    val gameStateFlow: Flow<GameState> = context.dataStore.data.map { preferences ->
        val gameStateJson = preferences[GAME_STATE_KEY]
        if (gameStateJson != null) {
            try {
                json.decodeFromString<GameState>(gameStateJson)
            } catch (e: Exception) {
                GameState()
            }
        } else {
            GameState()
        }
    }
    
    suspend fun saveGameState(gameState: GameState) {
        context.dataStore.edit { preferences ->
            val gameStateJson = json.encodeToString(gameState)
            preferences[GAME_STATE_KEY] = gameStateJson
        }
    }
    
    suspend fun updateGameState(update: (GameState) -> GameState) {
        context.dataStore.edit { preferences ->
            val currentStateJson = preferences[GAME_STATE_KEY]
            val currentState = if (currentStateJson != null) {
                try {
                    json.decodeFromString<GameState>(currentStateJson)
                } catch (e: Exception) {
                    GameState()
                }
            } else {
                GameState()
            }
            
            val newState = update(currentState)
            val newStateJson = json.encodeToString(newState)
            preferences[GAME_STATE_KEY] = newStateJson
        }
    }
    
    suspend fun clearGameData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

