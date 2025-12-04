# Guía de Desarrollo - Mi Primer Huerto

## 🏗️ Arquitectura del Proyecto

### Patrón MVVM (Model-View-ViewModel)

#### Model (Modelos de Datos)
- **PlantType**: Enum con todos los tipos de plantas disponibles
- **PlantTypeInfo**: Información detallada de cada planta (duración, precio, puntos)
- **PlantStage**: Enum con las etapas de crecimiento
- **Plant**: Estado actual de una planta específica
- **User**: Información del usuario (nombre, género)
- **GameState**: Estado completo del juego (usuario, planta actual, puntos, inventario)

#### View (Pantallas y Componentes)
**Pantallas:**
- `LoadingScreen`: Pantalla de carga inicial
- `RegisterScreen`: Registro de usuario
- `HomeScreen`: Pantalla principal del juego
- `ShopScreen`: Tienda de semillas y herramientas
- `PlantInfoScreen`: Información detallada de la planta

**Componentes Personalizados:**
- `GardenProgressBar`: Barra de progreso con hojas animadas
- `HealthBar/WaterBar`: Barras de estado con colores dinámicos
- `PlantVisualization`: Dibuja la planta según su tipo y etapa
- `ActionButton`: Botones de acción con animaciones
- `StatsDisplay`: Muestra puntos y monedas

#### ViewModel (Lógica de Negocio)
- **GameViewModel**: Gestiona todo el estado del juego
  - Game Loop: Actualiza el estado cada segundo
  - Acciones del usuario: regar, fertilizar, cosechar, etc.
  - Emisión de eventos de UI

### Repository Pattern
- **GameRepository**: Maneja la persistencia con DataStore
  - Serialización/deserialización con Kotlinx Serialization
  - Flow reactivo para actualizaciones automáticas

## ⚙️ Sistemas Principales

### 1. Sistema de Crecimiento de Plantas

```kotlin
// El GameLoop se ejecuta cada segundo
private fun startGameLoop() {
    viewModelScope.launch {
        while (true) {
            delay(1000)
            updatePlantState()
        }
    }
}
```

**Mecánicas:**
- Consumo de agua basado en el tipo de planta
- Pérdida de salud cuando el agua está baja
- Pérdida de salud por plagas
- Recuperación lenta de salud cuando las condiciones son buenas
- Progresión automática entre etapas

### 2. Sistema de Tiempo

**Conversión:** 1 hora real = 1 día del juego

```kotlin
growthDuration = 10 * 60 * 60 * 1000L  // 10 horas = 10 días
```

**Fondos Dinámicos:**
- 6-11 AM: Mañana (azul claro)
- 12-17 PM: Tarde (azul cielo)
- 18-20 PM: Atardecer (naranja)
- 21-5 AM: Noche (azul oscuro)

### 3. Sistema de Notificaciones

**WorkManager** ejecuta verificaciones cada 15 minutos:
- Nivel de agua < 50%: Notificación normal
- Nivel de agua < 25%: Notificación urgente
- Plaga detectada: Notificación de alerta
- Lista para cosechar: Notificación de éxito
- Planta muerta: Notificación crítica

### 4. Sistema de Sensores

**Acelerómetro:**
- Detecta sacudidas del dispositivo
- Umbral: 15 m/s²
- Cooldown: 1 segundo entre sacudidas
- Acción: Riega la planta automáticamente

**Sensor de Luz:**
- Mide el nivel de luz ambiente en lux
- Preparado para mecánicas futuras (ej: crecimiento más rápido con luz)

### 5. Sistema de Puntos y Economía

**Ganar Puntos:**
- Regar: +5 puntos
- Fertilizar: +10 puntos
- Eliminar plaga: +15 puntos
- Cosechar: Variable según planta (40-150 puntos)

**Ganar Monedas:**
- Cosechar: 50% de los puntos ganados

**Gastar Monedas:**
- Semillas: 0-150 monedas
- Fertilizante: 10 monedas
- Pesticida: 15 monedas

## 🎨 Sistema de Gráficos

### Canvas Drawing

Todas las plantas se dibujan usando Canvas:

```kotlin
@Composable
fun PlantVisualization(
    plantType: PlantType,
    stage: PlantStage,
    health: Float,
    hasPest: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Dibujar según la etapa
        when (stage) {
            PlantStage.SEMILLA -> drawSeed()
            PlantStage.GERMINACION -> drawGermination()
            // ... etc
        }
    }
}
```

**Elementos Dibujados:**
- Tallo con grosor proporcional a la etapa
- Hojas con Path bezier curves
- Frutos/flores según el tipo de planta
- Plagas (pequeños puntos negros animados)
- Efecto visual según la salud

### Animaciones

**Spring Animations:**
- Botones con efecto de presión
- Escalado suave de elementos seleccionados

**Infinite Animations:**
- Balanceo de plantas (efecto de viento)
- Shimmer en barras de carga
- Flotación de elementos decorativos

**Tween Animations:**
- Transiciones de pantalla
- Cambios de valor en barras de progreso

## 📊 Flujo de Datos

```
Usuario Acción
    ↓
HomeScreen (Composable)
    ↓
GameViewModel
    ↓
GameRepository
    ↓
DataStore (Persistencia)
    ↓
Flow Updates
    ↓
GameState collectAsState()
    ↓
UI Recomposición
```

## 🔄 Ciclo de Vida

### Inicialización de la App

1. **MainActivity.onCreate()**
   - Solicita permisos de notificaciones
   - Programa notificaciones periódicas
   - Crea NavController y GameViewModel

2. **GameViewModel.init()**
   - Carga el estado del juego desde DataStore
   - Inicia el Game Loop
   - Observa cambios de estado

3. **LoadingScreen**
   - Muestra animación de carga
   - Lee isFirstTime del GameState
   - Navega a Register o Home

### Durante el Juego

1. Usuario realiza acción (ej: regar)
2. ViewModel actualiza el estado
3. Repository guarda en DataStore
4. Flow emite nuevo estado
5. UI se recompone automáticamente
6. Game Loop continúa actualizando cada segundo

### Notificaciones en Background

1. WorkManager dispara cada 15 minutos
2. PlantNotificationWorker se ejecuta
3. Lee el estado actual desde DataStore
4. Verifica condiciones de la planta
5. Envía notificación si es necesario

## 🧪 Puntos de Extensión

### Agregar Nueva Planta

1. Agregar tipo en `PlantType` enum
2. Agregar info en `PlantTypeData.plantInfoMap`
3. Agregar colores de fruto/flor en `PlantVisuals.kt`
4. Agregar sprite drawing si es necesario

### Agregar Nueva Pantalla

1. Crear archivo en `ui/screens/`
2. Agregar ruta en `Screen.kt`
3. Agregar composable en `NavGraph.kt`
4. Vincular navegación desde pantallas existentes

### Agregar Nueva Mecánica

1. Actualizar `GameState` con nuevos campos
2. Agregar métodos en `GameViewModel`
3. Crear UI en componentes o pantallas
4. Actualizar Repository si necesita persistencia

## 🐛 Debugging

### Ver Estado del Juego en Tiempo Real

```kotlin
// En cualquier Composable
val gameState by gameViewModel.gameState.collectAsState()
Text("Debug: ${gameState}") 
```

### Forzar Actualización de Planta

```kotlin
// En GameViewModel
suspend fun debugForcePlantGrowth() {
    val currentState = _gameState.value
    val plant = currentState.currentPlant ?: return
    val nextStage = PlantStageData.getNextStage(plant.stage, ...)
    // Actualizar directamente
}
```

### Limpiar DataStore

```kotlin
// En GameViewModel o Repository
suspend fun clearGameData() {
    repository.clearGameData()
}
```

## 📦 Dependencias Importantes

```kotlin
// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Navigation
implementation("androidx.navigation:navigation-compose")

// DataStore
implementation("androidx.datastore:datastore-preferences")

// WorkManager
implementation("androidx.work:work-runtime-ktx")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
```

## ✅ Checklist de Nuevas Features

Antes de agregar una feature:
- [ ] ¿Necesita estado persistente? → Actualizar GameState
- [ ] ¿Necesita lógica? → Agregar en ViewModel
- [ ] ¿Necesita UI? → Crear Composable
- [ ] ¿Necesita navegación? → Actualizar NavGraph
- [ ] ¿Afecta notificaciones? → Actualizar Worker
- [ ] ¿Usa sensores? → Actualizar SensorManager

## 🚀 Optimizaciones Futuras

1. **Lazy Loading de Gráficos**: Cargar sprites bajo demanda
2. **Caché de Composables**: remember para cálculos costosos
3. **Work Constraints**: Optimizar cuándo ejecutar notificaciones
4. **Compression**: Comprimir datos de GameState si crece mucho
5. **Animations**: Usar derivedStateOf para animaciones complejas

---

¡Feliz desarrollo! 🌱

