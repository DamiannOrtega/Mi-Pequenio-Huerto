# Mi Primer Huerto 🌱

Un juego educativo para Android diseñado para niños de primaria, donde aprenderán sobre el crecimiento de las plantas y ciencias naturales de manera interactiva y divertida.

## 📱 Descripción

"Mi Primer Huerto" es un juego similar a Pou, pero enfocado en el cuidado de plantas. Los niños aprenderán sobre:

- Las diferentes etapas de crecimiento de las plantas
- La importancia del riego y los cuidados
- Qué sucede cuando una planta no recibe los cuidados necesarios
- Diferentes tipos de plantas y sus características
- Sistema de múltiples macetas para cultivar varias plantas simultáneamente

## ✨ Características Principales

### 🎮 Gameplay

- **Pantalla de Carga**: Logo animado con barra de progreso personalizada
- **Registro de Usuario**: Selección de personaje (jardinero/jardinera) y nombre
- **Pantalla Principal**: Sistema de múltiples macetas con deslizamiento horizontal
- **Sistema de Tienda**: Compra de semillas, fertilizantes, pesticidas y macetas
- **Información Detallada**: Consulta el estado y progreso de cada planta
- **Carrusel de Macetas**: Navegación horizontal para gestionar múltiples plantas

### 🌿 Sistema de Plantas

- **3 Plantas Iniciales**: Frijol, Rábano y Lechuga
- **3 Plantas Desbloqueables**: Girasol, Rosa y Tomate
- **Etapas de Crecimiento**:
  - Semilla
  - Germinación
  - Plántula
  - Joven
  - Maduro
  - Cosechable/Florecimiento
- **Sistema de Tiempo Real**: 1 hora real = 1 día del juego
- **Sistema Diferenciado**: Plantas ornamentales vs cosechables con mecánicas distintas

### 💧 Mecánicas de Juego

- **Riego**: Mantén el nivel de agua de tu planta
- **Fertilizante**: Mejora rápidamente la salud de la planta
- **Control de Plagas**: Elimina plagas que aparecen aleatoriamente
- **Sistema de Salud**: La planta puede morir si no se cuida
- **Puntos y Monedas**: Gana recompensas al cuidar y cosechar
- **Sistema de Múltiples Macetas**: Hasta 5 macetas independientes
- **Compra de Macetas**: 500 estrellas por maceta nueva
- **Botón "Cortar"**: Para plantas ornamentales en etapa máxima

### 🌟 Funcionalidades Avanzadas

#### 🏺 Sistema de Múltiples Macetas

- **Deslizamiento Horizontal**: Navega entre macetas con gestos intuitivos
- **Hasta 5 Macetas**: Expande tu jardín comprando nuevas macetas
- **Macetas Independientes**: Cada maceta tiene su propia planta y estado
- **Costo**: 500 estrellas por maceta nueva
- **Indicadores Visuales**: Flechas de navegación para mostrar más macetas

#### 🌹 Sistema de Plantas Ornamentales

- **Plantas Especiales**: Girasol y Rosa (no se cosechan)
- **Puntos Mejorados**: 3x más puntos por cuidar plantas ornamentales
  - Regar: +15 puntos (vs +5 normales)
  - Fertilizar: +30 puntos (vs +10 normales)
  - Anti-plaga: +45 puntos (vs +15 normales)
- **Botón "Cortar"**: Aparece cuando la planta ornamental está floreciendo
- **Recompensa Especial**: +25 puntos por cortar plantas ornamentales
- **Indicadores Visuales**: Badge "+3x puntos" en plantas ornamentales

#### 🛒 Tienda Mejorada

- **Sección de Macetas**: Nueva categoría para comprar macetas
- **Validación de Recursos**: Verifica si tienes suficientes estrellas
- **Contador de Macetas**: Muestra cuántas macetas tienes vs máximo
- **Mensajes Informativos**: Explica costos y beneficios

#### 🎮 Controles Mejorados

- **Botones Específicos**: "Cortar" vs "Cosechar" según tipo de planta
- **Colores Diferenciados**: Rosa para plantas ornamentales, amarillo para cosechables
- **Iconos Específicos**: Tijeras para cortar, hoja para cosechar
- **Lógica Inteligente**: Solo muestra opciones relevantes para cada planta

#### 🎨 Interfaz Actualizada

- **Cards Optimizadas**: Tamaño mejorado (300dp) para mejor visualización
- **Indicadores de Navegación**: Flechas para mostrar que hay más contenido
- **Badges Informativos**: "+3x puntos" para plantas ornamentales
- **Notificaciones Especiales**: Mensajes específicos para cada acción

### 📲 Características Técnicas

#### 🎨 Interfaz de Usuario

- **Jetpack Compose**: UI moderna y declarativa
- **Material 3**: Sistema de diseño actualizado
- **Animaciones**: Transiciones suaves y elementos animados
- **Canvas Personalizado**: Gráficos dibujados a mano para plantas

#### 🌤️ Sistema Dinámico

- **Fondos que Cambian**: Según la hora del día (mañana, tarde, atardecer, noche)
- **Barras Personalizadas**: Salud, agua con diseños temáticos
- **Visualización de Plantas**: Diferentes sprites para cada etapa

#### 🔔 Notificaciones

- **Nivel de Agua Bajo**: 50% y 25%
- **Plaga Detectada**: Alerta cuando aparecen plagas
- **Planta Lista**: Aviso cuando está lista para cosechar
- **Planta Muerta**: Notificación si la planta muere

#### 📱 Sensores

- **Acelerómetro**: Sacude el dispositivo para regar la planta
- **Sensor de Luz**: Detecta el nivel de luz ambiente (preparado para expansión futura)

#### 💾 Almacenamiento

- **DataStore**: Guardado automático del progreso
- **Serialización**: Estado del juego persistente
- **Respaldo Local**: Todo el progreso se guarda localmente

## 🛠️ Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación
- **Jetpack Compose**: Framework de UI
- **Material 3**: Sistema de diseño
- **Navigation Compose**: Navegación entre pantallas
- **DataStore**: Almacenamiento de preferencias
- **WorkManager**: Notificaciones periódicas
- **ViewModel**: Arquitectura MVVM
- **Coroutines & Flow**: Programación asíncrona
- **Sensors API**: Acelerómetro y sensor de luz

## 📂 Estructura del Proyecto

```
app/src/main/java/com/example/miprimerhuerto/
├── data/
│   ├── model/          # Modelos de datos
│   │   ├── Plant.kt
│   │   ├── User.kt
│   │   ├── GameState.kt
│   │   ├── PlantPot.kt          # Modelo para macetas
│   │   ├── PlantType.kt
│   │   └── PlantStage.kt
│   └── repository/     # Repositorio para DataStore
├── ui/
│   ├── components/     # Componentes reutilizables
│   │   ├── CustomProgressBar.kt
│   │   ├── PlantVisuals.kt
│   │   ├── GameButtons.kt
│   │   └── PlantPotsCarousel.kt # Carrusel de macetas
│   ├── navigation/     # Sistema de navegación
│   ├── screens/        # Pantallas del juego
│   │   ├── LoadingScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── ShopScreen.kt        # Incluye sección de macetas
│   │   └── PlantInfoScreen.kt
│   ├── theme/          # Colores y temas
│   │   └── Type.kt              # Tipografía personalizada
│   └── viewmodel/      # Lógica de negocio
│       └── GameViewModel.kt     # Manejo de múltiples macetas
├── notifications/      # Sistema de notificaciones
├── sensors/           # Manejo de sensores
└── utils/             # Utilidades (tiempo, etc.)
```

## 🎯 Requisitos del Proyecto

### Requisitos Implementados ✅

a. ✅ **Diferentes Layouts**: Se utilizan Box, Column, Row, LazyColumn, etc.
b. ✅ **Uso Correcto de Controles**: Buttons, Cards, TextField, Icons, etc.
c. ✅ **Controles Personalizados**: Barras de progreso, botones animados, visualización de plantas
d. ✅ **Widgets**: Sistema de notificaciones implementado
e. ✅ **Gráficos en Android**: Canvas personalizado para dibujar plantas
f. ✅ **Sensores**: Acelerómetro para regar, sensor de luz
g. ✅ **Guardado Local**: DataStore para persistir el progreso

## 🚀 Cómo Ejecutar

1. **Clonar el repositorio**
2. **Abrir en Android Studio**
3. **Sincronizar Gradle**
4. **Ejecutar en dispositivo o emulador** (API 29+)

## 📱 Requisitos del Sistema

- **minSdk**: 29 (Android 10)
- **targetSdk**: 36
- **Kotlin**: 2.2.20
- **Gradle**: 8.13.0

## 🎮 Cómo Jugar

1. **Primera Vez**: Ingresa tu nombre y elige tu personaje
2. **Plantar**: Selecciona una semilla y plántala en la maceta
3. **Cuidar**: Riega regularmente, aplica fertilizante y elimina plagas
4. **Cosechar/Cortar**:
   - **Plantas cosechables**: Cosecha cuando estén maduras
   - **Plantas ornamentales**: Corta cuando estén floreciendo
5. **Comprar**: Usa estrellas para comprar semillas, herramientas y macetas
6. **Expandir Jardín**: Compra nuevas macetas para cultivar más plantas
7. **Deslizar**: Navega entre tus macetas deslizando horizontalmente

### 💡 Consejos

- **Sacude tu dispositivo** para regar la planta rápidamente
- Revisa las **notificaciones** para saber cuándo tu planta necesita cuidados
- Cada planta tiene **diferentes tiempos de crecimiento**
- Las plantas **ornamentales dan 3x más puntos** al cuidarlas
- **Previene las plagas** comprando pesticidas en la tienda
- Las plantas ornamentales se "cortan" en lugar de cosecharse
- Cada maceta es independiente - puedes tener diferentes plantas

### 📊 Sistema de Puntuación

#### 🌱 Plantas Cosechables (Frijol, Rábano, Lechuga, Tomate)

| Acción    | Puntos    | Descripción          |
| ---------- | --------- | --------------------- |
| Regar      | +5 ⭐     | Mantener hidratada    |
| Fertilizar | +10 ⭐    | Mejorar salud         |
| Anti-plaga | +15 ⭐    | Eliminar plagas       |
| Cosechar   | 40-150 ⭐ | Según tipo de planta |

#### 🌹 Plantas Ornamentales (Girasol, Rosa)

| Acción    | Puntos | Descripción             |
| ---------- | ------ | ------------------------ |
| Regar      | +15 ⭐ | 3x más que normales     |
| Fertilizar | +30 ⭐ | 3x más que normales     |
| Anti-plaga | +45 ⭐ | 3x más que normales     |
| Cortar     | +25 ⭐ | Cuando está floreciendo |

#### 🏺 Sistema de Macetas

| Item         | Costo  | Descripción                 |
| ------------ | ------ | ---------------------------- |
| Nueva Maceta | 500 ⭐ | Desbloquea espacio adicional |
| Fertilizante | 10 ⭐  | Mejora salud de plantas      |
| Pesticida    | 15 ⭐  | Elimina plagas               |

## 🌟 Características Futuras Potenciales

- Más tipos de plantas
- Sistema de logros
- Clima dinámico que afecta el crecimiento
- Minijuegos educativos
- Compartir progreso con amigos
- Modo jardín comunitario
- Sistema de temporadas
- Plantas que requieren cuidados especiales
- Intercambio de plantas entre usuarios

## 👨‍💻 Desarrollo

Este proyecto fue creado como parte de un curso de desarrollo móvil, implementando las mejores prácticas de Android y Jetpack Compose.

### Arquitectura

- **MVVM**: Separación clara entre UI y lógica de negocio
- **Single Source of Truth**: Estado centralizado en ViewModel
- **Flujo Unidireccional**: Eventos hacia arriba, estado hacia abajo

## 📄 Licencia

Este proyecto es educativo y fue creado con fines de aprendizaje.

---

**¡Disfruta aprendiendo sobre plantas! 🌱🌻🌾**
