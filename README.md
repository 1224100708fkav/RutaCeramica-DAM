# 🏺 Ruta Cerámica - Dolores Hidalgo

![Versión](https://img.shields.io/badge/Versi%C3%B3n-1.0.0-blue)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange)
![Firebase](https://img.shields.io/badge/Backend-Firebase-yellow)

**Ruta Cerámica** es una aplicación móvil innovadora diseñada para potenciar el turismo artesanal en la región norte de Guanajuato, específicamente en Dolores Hidalgo C.I.N. La aplicación conecta a turistas con talleres de cerámica locales, ofreciendo una experiencia integral de exploración, navegación y gestión de compras.

---

## 🚀 Funcionalidades Principales

### 🗺️ Exploración y Navegación Inteligente
- **Mapa Interactivo**: Visualización de todos los talleres artesanales aprobados.
- **Navegación Interna**: Sistema de rutas integrado (Coche/Caminando) que guía al usuario paso a paso sin salir de la aplicación.
- **Integración con Google Maps**: Opción de abrir la navegación en la app nativa de Google.

### 🛍️ Sistema de Apartados (Carrito)
- Permite a los turistas seleccionar productos y cantidades.
- Cálculo automático de totales.
- Generación de lista para compra presencial en tienda física, fomentando el comercio local.

### 📸 Bitácora de Viaje regional
- Registro personal de experiencias utilizando la **Cámara** del dispositivo.
- Almacenamiento de recuerdos y fotografías en la nube.

### 👤 Gestión de Perfil y Reseñas
- Personalización de perfil (Nombre, Origen, Biografía y Foto).
- Sistema de retroalimentación para calificar y comentar sobre los talleres visitados.

---

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin
- **Interfaz de Usuario**: Jetpack Compose (UI Moderna y Declarativa)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Backend**: 
  - **Firebase Auth**: Autenticación segura y manejo de roles.
  - **Firestore**: Base de Datos NoSQL en tiempo real.
  - **Firebase Storage**: Almacenamiento de imágenes (Perfiles, Talleres, Productos).
- **APIs Externas**: 
  - Google Maps SDK for Android.
  - Google Directions API (Cálculo de rutas).

---

## 📦 Instalación y Configuración

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/TU_USUARIO/RutaCeramica.git
   ```
2. **Abrir en Android Studio**: Importar el proyecto como un proyecto de Gradle existente.
3. **Configurar Firebase**:
   - Colocar el archivo `google-services.json` en la carpeta `app/`.
4. **Configurar API Key**:
   - Añadir tu `MAPS_API_KEY` en el archivo `local.properties`.
5. **Compilar y Ejecutar**: Presionar el botón `Run` en Android Studio.

---

## 👥 Autores
- **Desarrollador**: [Tu Nombre Completo]
- **Asignatura**: Desarrollo de Aplicaciones Móviles
- **Institución**: Universidad Tecnológica del Norte de Guanajuato (UTNG)

---
*Este proyecto fue desarrollado como parte de la evaluación global para la Licenciatura en Ingeniería en Desarrollo y Gestión de Software.*
