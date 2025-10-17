# Mi Primer Huerto 🌱

Un juego educativo para Android diseñado para niños de primaria, donde aprenderán sobre el crecimiento de las plantas y ciencias naturales de manera interactiva y divertida.

## 📱 Descripción

"Mi Primer Huerto" es un juego similar a Pou, pero enfocado en el cuidado de plantas. Los niños aprenderán sobre:
- Las diferentes etapas de crecimiento de las plantas
- La importancia del riego y los cuidados
- Qué sucede cuando una planta no recibe los cuidados necesarios
- Diferentes tipos de plantas y sus características

## ✨ Características Principales

### 🎮 Gameplay
- **Pantalla de Carga**: Logo animado con barra de progreso personalizada
- **Registro de Usuario**: Selección de personaje (jardinero/jardinera) y nombre
- **Pantalla Principal**: Maceta interactiva con planta en crecimiento
- **Sistema de Tienda**: Compra de semillas, fertilizantes y pesticidas
- **Información Detallada**: Consulta el estado y progreso de tu planta

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

### 💧 Mecánicas de Juego
- **Riego**: Mantén el nivel de agua de tu planta
- **Fertilizante**: Mejora rápidamente la salud de la planta
- **Control de Plagas**: Elimina plagas que aparecen aleatoriamente
- **Sistema de Salud**: La planta puede morir si no se cuida
- **Puntos y Monedas**: Gana recompensas al cuidar y cosechar

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
│   ├── model/          # Modelos de datos (Plant, User, GameState)
│   └── repository/     # Repositorio para DataStore
├── ui/
│   ├── components/     # Componentes reutilizables
│   │   ├── CustomProgressBar.kt
│   │   ├── PlantVisuals.kt
│   │   └── GameButtons.kt
│   ├── navigation/     # Sistema de navegación
│   ├── screens/        # Pantallas del juego
│   │   ├── LoadingScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── ShopScreen.kt
│   │   └── PlantInfoScreen.kt
│   ├── theme/          # Colores y temas
│   └── viewmodel/      # Lógica de negocio
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
4. **Cosechar**: Cuando la planta esté madura, cosecha para obtener puntos
5. **Comprar**: Usa monedas para comprar más semillas y herramientas

### 💡 Consejos

- **Sacude tu dispositivo** para regar la planta rápidamente
- Revisa las **notificaciones** para saber cuándo tu planta necesita cuidados
- Cada planta tiene **diferentes tiempos de crecimiento**
- Las plantas **decorativas no se cosechan** pero dan más puntos
- **Previene las plagas** comprando pesticidas en la tienda

## 🌟 Características Futuras Potenciales

- Más tipos de plantas
- Sistema de logros
- Múltiples macetas
- Clima dinámico que afecta el crecimiento
- Minijuegos educativos
- Compartir progreso con amigos
- Modo jardín comunitario

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

