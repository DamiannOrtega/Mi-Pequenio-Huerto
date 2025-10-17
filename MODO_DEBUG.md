# 🐛 Modo DEBUG - Guía de Pruebas

Este documento explica cómo usar el modo DEBUG para probar la aplicación "Mi Primer Huerto" de forma acelerada.

## 📋 ¿Qué es el Modo DEBUG?

El modo DEBUG es una configuración especial que acelera todos los tiempos del juego para facilitar las pruebas durante el desarrollo. En lugar de esperar horas para ver los cambios en las plantas, puedes verlos en minutos.

## 🔧 Cómo Activar/Desactivar el Modo DEBUG

### Activar el Modo DEBUG

1. Abre el archivo: `app/src/main/java/com/example/miprimerhuerto/utils/DebugConfig.kt`

2. Busca la línea:
   ```kotlin
   const val DEBUG_MODE = true
   ```

3. Si está en `false`, cámbialo a `true`:
   ```kotlin
   const val DEBUG_MODE = true  // ✅ Modo de prueba activado
   ```

### Desactivar el Modo DEBUG (para producción)

⚠️ **IMPORTANTE**: Antes de publicar la app en Google Play, DEBES desactivar el modo DEBUG.

1. Abre el archivo: `app/src/main/java/com/example/miprimerhuerto/utils/DebugConfig.kt`

2. Cambia la línea a:
   ```kotlin
   const val DEBUG_MODE = false  // ⏳ Tiempos reales para usuarios
   ```

## ⏱️ Diferencias entre Modo DEBUG y Modo Normal

### Modo DEBUG (DEBUG_MODE = true)

| Característica | Tiempo en Debug | Equivalente Real |
|----------------|-----------------|------------------|
| **Crecimiento de plantas** | 60x más rápido | 1 minuto = 1 hora |
| **Frijol (10 días)** | ~10 minutos | 10 horas |
| **Rábano (7 días)** | ~7 minutos | 7 horas |
| **Lechuga (12 días)** | ~12 minutos | 12 horas |
| **Girasol (15 días)** | ~15 minutos | 15 horas |
| **Rosa (20 días)** | ~20 minutos | 20 horas |
| **Tomate (14 días)** | ~14 minutos | 14 horas |
| **Notificaciones** | Cada 1 minuto | Cada 15 minutos |
| **Duración Snackbar** | 8 segundos | 5 segundos |
| **Consumo de agua** | 60x más rápido | Normal |

### Modo Normal (DEBUG_MODE = false)

| Característica | Tiempo Normal |
|----------------|---------------|
| **Crecimiento de plantas** | 1 hora real = 1 día del juego |
| **Frijol** | 10 horas reales |
| **Rábano** | 7 horas reales |
| **Lechuga** | 12 horas reales |
| **Girasol** | 15 horas reales |
| **Rosa** | 20 horas reales |
| **Tomate** | 14 horas reales |
| **Notificaciones** | Cada 15 minutos |
| **Duración Snackbar** | 5 segundos |

## 🧪 Cómo Probar Diferentes Escenarios

### 1. Probar Crecimiento de Plantas

En **modo DEBUG**, una planta de frijol crecerá completamente en ~10 minutos:

1. Activa el modo DEBUG
2. Planta una semilla de frijol
3. Riégala al 100%
4. Espera ~2 minutos para ver la primera transición de etapa
5. Observa las transiciones: Semilla → Germinación → Plántula → Joven → Maduro → Cosechable

### 2. Probar Notificaciones

En **modo DEBUG**, las notificaciones se revisan cada 1 minuto:

1. Activa el modo DEBUG
2. Planta una semilla y riégala
3. Deja que el agua baje al 50%
4. En ~1-2 minutos recibirás una notificación de que la planta necesita agua
5. Si la dejas sin agua hasta el 25%, recibirás otra notificación más urgente

### 3. Probar Muerte de Planta

1. Activa el modo DEBUG
2. Planta una semilla pero NO la riegues
3. El agua bajará rápidamente (60x más rápido)
4. Cuando el agua llegue a 0, la vida empezará a bajar
5. En ~3-5 minutos la planta morirá

### 4. Probar Sobreriego

1. Planta una semilla y riégala al 100%
2. Intenta regar de nuevo
3. Deberías ver el mensaje de advertencia: "⚠️ ¡CUIDADO! Estás sobreregando la planta. Está perdiendo vida 💔"
4. La vida de la planta disminuirá en 10%

### 5. Probar Plagas

1. Activa el modo DEBUG
2. Planta una semilla y cuídala normalmente
3. Las plagas aparecen aleatoriamente (30% de probabilidad cada actualización)
4. En modo DEBUG verás plagas aparecer más frecuentemente
5. Usa pesticida para eliminarlas y ganar puntos

### 6. Probar Compras en la Tienda

1. Activa el modo DEBUG
2. Riega plantas y elimina plagas para ganar puntos rápidamente
3. Ve a la tienda
4. Compra semillas (20-150 puntos dependiendo del tipo)
5. Compra fertilizante (10 puntos) o pesticida (15 puntos)

## 📊 Logs de DEBUG

Cuando el modo DEBUG está activado, verás mensajes en el Logcat de Android Studio que empiezan con "🌱 DEBUG:".

Para verlos:

1. Abre Android Studio
2. Ve a la pestaña "Logcat" (abajo)
3. Filtra por "DEBUG" o "🌱"

## ⚙️ Valores Configurables en DebugConfig

Puedes personalizar más valores en `DebugConfig.kt`:

```kotlin
object DebugConfig {
    // Activar/desactivar modo debug
    const val DEBUG_MODE = true
    
    // Multiplicador de tiempo (60x en debug)
    const val TIME_MULTIPLIER = if (DEBUG_MODE) 60 else 1
    
    // Intervalo de notificaciones en minutos
    const val NOTIFICATION_INTERVAL_MINUTES = if (DEBUG_MODE) 1L else 15L
    
    // Duración de Snackbar en milisegundos
    const val SNACKBAR_DURATION_MS = if (DEBUG_MODE) 8000L else 5000L
}
```

## 🚀 Flujo de Trabajo Recomendado

### Durante el Desarrollo

1. ✅ Mantén `DEBUG_MODE = true`
2. 🧪 Prueba todas las funcionalidades rápidamente
3. 🐛 Revisa los logs en Logcat
4. 🔄 Itera y mejora

### Antes de Publicar

1. ⚠️ Cambia `DEBUG_MODE = false`
2. 🧹 Limpia el proyecto: `./gradlew clean`
3. 🔨 Compila: `./gradlew build`
4. 📱 Prueba en un dispositivo real con tiempos normales
5. 📦 Genera el APK de release

## 🎯 Ejemplo: Prueba Completa del Ciclo de Vida

**Con DEBUG_MODE = true:**

```
⏰ Tiempo Total: ~15 minutos

[Minuto 0] Plantar frijol y regar
[Minuto 2] Semilla → Germinación (notificación de cambio de etapa)
[Minuto 4] Germinación → Plántula
[Minuto 6] Plántula → Joven
[Minuto 8] Joven → Maduro
[Minuto 10] Maduro → Cosechable
[Minuto 11] Cosechar y ganar 50 puntos
[Minuto 12] Ir a tienda, comprar nueva semilla
[Minuto 13] Plantar rábano
[Minuto 15] Ver primeras etapas del rábano
```

**Con DEBUG_MODE = false:**

El mismo ciclo tomaría **~13 horas** en tiempo real.

## 💡 Consejos

1. **No subas a producción con DEBUG_MODE = true** - Los jugadores verían plantas crecer demasiado rápido y el juego perdería su propósito educativo
2. **Usa logs para debugging** - Los mensajes de `DebugConfig.log()` solo aparecen en modo DEBUG
3. **Prueba ambos modos** - Antes de publicar, prueba unos minutos en modo normal para asegurar que los tiempos se sienten correctos
4. **Considera hacer una build de debug y otra de release** - Android Studio maneja esto automáticamente con flavors

## 🔍 Verificar el Modo Actual

Si no estás seguro de qué modo está activo, busca en Logcat al iniciar la app:

```
🌱 DEBUG: Notificaciones programadas cada 1 minutos  ← Modo DEBUG
```

o

```
(No hay mensaje de debug)  ← Modo Normal
```

---

**¡Felices pruebas! 🌱**

