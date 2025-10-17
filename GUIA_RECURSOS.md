# 🎨 Guía para Cambiar Recursos e Imágenes

## 📁 Ubicación de los Recursos

Todos los recursos gráficos de la aplicación se encuentran en:
```
app/src/main/res/
```

## 🖼️ Tipos de Recursos

### 1. **Imágenes (drawable/)**

#### Ubicación:
```
app/src/main/res/drawable/
```

#### Tipos de archivos soportados:
- **PNG** (.png) - Recomendado para imágenes con transparencia
- **JPEG** (.jpg) - Para fotos sin transparencia
- **XML** (.xml) - Para gráficos vectoriales (muy recomendado)
- **WebP** (.webp) - Formato moderno y comprimido

#### Cómo agregar imágenes:

1. **Copiar la imagen** a la carpeta `drawable/`
2. **Nombrar el archivo** en minúsculas, solo letras, números y guiones bajos
   - ✅ Correcto: `semilla_frijol.png`, `planta_etapa_1.png`
   - ❌ Incorrecto: `Semilla-Frijol.png`, `planta etapa 1.png`

3. **Usar en el código Kotlin:**
   ```kotlin
   Image(
       painter = painterResource(id = R.drawable.semilla_frijol),
       contentDescription = "Semilla de frijol"
   )
   ```

### 2. **Iconos de la App (mipmap/)**

#### Ubicación de cada tamaño:
```
app/src/main/res/mipmap-hdpi/     (72x72 px)
app/src/main/res/mipmap-mdpi/     (48x48 px)
app/src/main/res/mipmap-xhdpi/    (96x96 px)
app/src/main/res/mipmap-xxhdpi/   (144x144 px)
app/src/main/res/mipmap-xxxhdpi/  (192x192 px)
```

#### Cómo cambiar el logo de la app:

1. **Generar iconos** de diferentes tamaños (usa [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/))
2. **Reemplazar los archivos:**
   - `ic_launcher.webp` (ícono normal)
   - `ic_launcher_round.webp` (ícono redondo)
3. **Opcional:** Editar `ic_launcher_foreground.xml` y `ic_launcher_background.xml` para iconos adaptativos

### 3. **Gráficos Vectoriales (drawable/ con XML)**

Los gráficos vectoriales son **recomendados** porque:
- 📏 Escalan sin perder calidad
- 💾 Ocupan menos espacio
- 🎨 Son fáciles de personalizar

#### Ejemplo de vector XML:
```xml
<!-- res/drawable/ic_plant.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#4CAF50"
        android:pathData="M12,2L4,9v12h16V9L12,2z"/>
</vector>
```

## 🌱 Implementar Imágenes para las Etapas de las Plantas

### Opción 1: Usar Imágenes PNG/WebP

1. **Crear imágenes para cada etapa:**
   ```
   frijol_semilla.png
   frijol_germinacion.png
   frijol_plantula.png
   frijol_joven.png
   frijol_maduro.png
   frijol_cosechable.png
   ```

2. **Colocarlas en** `app/src/main/res/drawable/`

3. **Modificar** `PlantVisuals.kt` para usar imágenes:

```kotlin
@Composable
fun PlantVisualization(
    plantType: PlantType,
    stage: PlantStage,
    health: Float,
    hasPest: Boolean,
    modifier: Modifier = Modifier
) {
    val imageRes = when (plantType) {
        PlantType.FRIJOL -> when (stage) {
            PlantStage.SEMILLA -> R.drawable.frijol_semilla
            PlantStage.GERMINACION -> R.drawable.frijol_germinacion
            PlantStage.PLANTULA -> R.drawable.frijol_plantula
            PlantStage.JOVEN -> R.drawable.frijol_joven
            PlantStage.MADURO -> R.drawable.frijol_maduro
            PlantStage.COSECHABLE -> R.drawable.frijol_cosechable
            PlantStage.MUERTA -> R.drawable.planta_muerta
            else -> R.drawable.frijol_semilla
        }
        // Otros tipos...
    }
    
    // Animación de transición suave
    var currentImage by remember { mutableStateOf(imageRes) }
    
    LaunchedEffect(imageRes) {
        currentImage = imageRes
    }
    
    Image(
        painter = painterResource(id = currentImage),
        contentDescription = "$plantType en etapa $stage",
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(
                // Efecto de fade cuando cambia
                alpha = animateFloatAsState(
                    targetValue = if (currentImage == imageRes) 1f else 0f,
                    animationSpec = tween(500)
                ).value
            ),
        contentScale = ContentScale.Fit
    )
}
```

### Opción 2: Animación de Crossfade entre etapas

```kotlin
@Composable
fun PlantVisualizationWithCrossfade(
    plantType: PlantType,
    stage: PlantStage,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = stage,
        animationSpec = tween(800), // 800ms de transición
        label = "plant_stage"
    ) { currentStage ->
        val imageRes = getPlantImage(plantType, currentStage)
        
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
```

## 🎨 Cambiar Colores del Tema

### Ubicación:
```
app/src/main/java/com/example/miprimerhuerto/ui/theme/Color.kt
```

### Editar colores:
```kotlin
val GreenPrimary = Color(0xFF4CAF50) // Cambiar el valor hexadecimal
val BrownPrimary = Color(0xFF8D6E63)
```

## 🔊 Agregar Sonidos (Opcional)

### Ubicación:
```
app/src/main/res/raw/
```

### Cómo usar:
```kotlin
val mediaPlayer = MediaPlayer.create(context, R.raw.water_sound)
mediaPlayer.start()
```

## 📐 Mejores Prácticas

### Tamaños Recomendados:

| Tipo | Tamaño | Formato |
|------|--------|---------|
| Iconos pequeños | 24x24 dp | Vector XML |
| Ilustraciones plantas | 512x512 px | PNG/WebP |
| Fondos | 1920x1080 px | JPEG/WebP |
| Logo app | 512x512 px | PNG con transparencia |

### Optimización:

1. **Comprimir imágenes** antes de agregarlas:
   - [TinyPNG](https://tinypng.com/)
   - [Squoosh](https://squoosh.app/)

2. **Usar WebP** en lugar de PNG cuando sea posible (50% menos tamaño)

3. **Vectores para iconos** siempre que sea posible

## 🛠️ Herramientas Recomendadas

- **[Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/)** - Genera iconos de app
- **[Shape Shifter](https://shapeshifter.design/)** - Crea animaciones de vectores
- **[Figma](https://www.figma.com/)** - Diseño de interfaces
- **[Inkscape](https://inkscape.org/)** - Editor de vectores gratuito
- **[GIMP](https://www.gimp.org/)** - Editor de imágenes gratuito

## 💡 Ejemplo Completo: Cambiar la Maceta

### 1. Crear/Obtener imagen de maceta:
- Tamaño: 500x300 px
- Formato: PNG con transparencia
- Nombre: `maceta_principal.png`

### 2. Colocar en drawable:
```
app/src/main/res/drawable/maceta_principal.png
```

### 3. Modificar `GameButtons.kt`:

Buscar la función `PlantPot` y reemplazar el Canvas por:

```kotlin
@Composable
fun PlantPot(
    modifier: Modifier = Modifier,
    hasPlant: Boolean = false
) {
    Image(
        painter = painterResource(id = R.drawable.maceta_principal),
        contentDescription = "Maceta",
        modifier = modifier.size(250.dp),
        contentScale = ContentScale.Fit
    )
}
```

## 🚀 Aplicar Cambios

1. **Sync Gradle** después de agregar recursos
2. **Clean & Rebuild** si Android Studio no encuentra los recursos
3. **Ejecutar la app** para ver los cambios

## ❓ Solución de Problemas

**Error: "Unresolved reference R.drawable"**
- Solución: Sync Gradle Files

**Las imágenes se ven pixeladas**
- Solución: Usar imágenes de mayor resolución o vectores

**La app pesa demasiado**
- Solución: Comprimir imágenes o usar WebP

---

¡Ahora puedes personalizar completamente el aspecto visual de "Mi Primer Huerto"! 🌱✨

