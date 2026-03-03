# 🏺 RutaCerámica
### Aplicación Android de Turismo y Artesanía — Dolores Hidalgo, Gto.

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
  <img src="https://img.shields.io/badge/Google_Maps-4285F4?style=for-the-badge&logo=google-maps&logoColor=white"/>
  <img src="https://img.shields.io/badge/MVVM-Architecture-orange?style=for-the-badge"/>
</p>

<p align="center">
  <b>RutaCerámica</b> conecta turistas con talleres artesanales de cerámica en Dolores Hidalgo, Guanajuato.<br/>
  Descubre, navega y explora la tradición cerámica de México desde tu dispositivo Android.
</p>

---

## 📌 Descripción

**RutaCerámica** es una aplicación móvil Android desarrollada en **Kotlin + Jetpack Compose**, diseñada bajo principios de arquitectura limpia y documentada profesionalmente con estándar **KDoc**.

La aplicación permite explorar talleres artesanales, gestionar usuarios con roles diferenciados y recibir notificaciones en tiempo real mediante Firebase.

---

## ✨ Funcionalidades

| Funcionalidad | Descripción |
|---|---|
| 🔐 Autenticación | Registro e inicio de sesión con Firebase Authentication |
| 👑 Roles | Administrador y Usuario con permisos distintos |
| 👤 CRUD Usuarios | Crear, leer, actualizar y eliminar cuentas |
| 🏺 CRUD Talleres | Gestión completa de talleres artesanales |
| 🗺️ Google Maps | Mapa interactivo con ubicación de talleres |
| 📡 Sensores | GPS, acelerómetro, temperatura, brújula |
| 📷 Cámara | Escaneo y reconocimiento de piezas cerámicas |
| 🔔 Notificaciones | Push notifications con Firebase Cloud Messaging |
| 🔒 Seguridad | Reglas Firestore, autenticación y HTTPS |
| 🤖 IA + AR | Guía inteligente con realidad aumentada |

---

## 🏗️ Arquitectura — MVVM

La aplicación implementa la arquitectura **MVVM (Model-View-ViewModel)** para garantizar:

- Separación clara de responsabilidades
- Código mantenible y escalable
- Manejo reactivo de estado con `StateFlow`
- Integración limpia con Firebase y Coroutines

### 📐 Diagrama Arquitectónico

```
┌─────────────────────────────────────────┐
│                   VIEW                   │
│    Jetpack Compose Screens / Activities  │
└──────────────────┬──────────────────────┘
                   │ observa StateFlow
┌──────────────────▼──────────────────────┐
│               VIEWMODEL                  │
│     AuthViewModel / UbicacionViewModel   │
└──────────────────┬──────────────────────┘
                   │ suspend fun / coroutines
┌──────────────────▼──────────────────────┐
│              REPOSITORY                  │
│    AuthRepository / UbicacionRepository  │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│                 MODEL                    │
│         Usuario.kt / Ubicacion.kt        │
└─────────────────────────────────────────┘
```

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Kotlin
- **Interfaz:** Jetpack Compose
- **Arquitectura:** MVVM
- **Documentación:** Estándar profesional **KDoc**
- **Backend:**
  - Firebase Authentication
  - Cloud Firestore
  - Firebase Cloud Messaging
  - Firebase Storage
- **APIs Externas:**
  - Google Maps SDK
  - Google Play Services Location

---

## 📁 Estructura del Proyecto

```
app/
├── data/
│   ├── model/
│   └── repository/
├── viewmodel/
├── ui/
│   ├── screen/
│   ├── components/
│   └── theme/
├── navigation/
├── MainActivity.kt
├── MensajeriaService.kt
└── AndroidManifest.xml
```

---

## 🧩 Documentación con KDoc

Todo el código fuente está documentado utilizando el estándar **KDoc**, lo que permite:

- Generar documentación automática
- Explicar responsabilidades de clases y funciones
- Describir parámetros y valores de retorno
- Facilitar mantenimiento y escalabilidad

### Ejemplo de Modelo Documentado

```kotlin
/**
 * Modelo de usuario registrado en Firebase Auth + Firestore.
 *
 * @property uid ID único del usuario generado por Firebase.
 * @property nombre Nombre completo del usuario.
 * @property email Correo electrónico asociado a la cuenta.
 * @property telefono Número telefónico de contacto.
 * @property rol Rol del usuario dentro del sistema ("admin" | "usuario").
 * @property fechaRegistro Timestamp de creación del registro.
 */
data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val rol: String = "usuario",
    val fechaRegistro: Long = System.currentTimeMillis()
)
```

---

## 🔥 Configuración Firebase

1. Crear proyecto en Firebase Console
2. Registrar aplicación Android con paquete:

```
com.utng.rutaceramica
```

3. Descargar `google-services.json`
4. Colocarlo en:

```
app/google-services.json
```

5. Activar:
  - Authentication (Email/Password)
  - Cloud Firestore
  - Cloud Messaging

---

## 🔒 Reglas de Seguridad Firestore

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /usuarios/{uid} {
      allow read, write: if request.auth != null
                         && request.auth.uid == uid;
    }

    match /talleres/{id} {
      allow read:  if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

---

## 💻 Instalación

```bash
git clone https://github.com/TU_USUARIO/RutaCeramica.git
cd RutaCeramica
```

Abrir en Android Studio → Sync Gradle → Run.

---

## 📋 Requisitos

| Requisito | Versión |
|-----------|---------|
| Android Studio | Hedgehog+ |
| Kotlin | 2.0+ |
| compileSdk | 35 |
| minSdk | 26 |
| JDK | 11+ |

---

## 👩‍💻 Autores

**Fabián Karel Avalos Velázquez**  
**Ángel Daniel Hernández Mandujano**

Universidad Tecnológica del Norte de Guanajuato — UTNG  
Proyecto Final · Desarrollo de Aplicaciones Móviles

---

## 📜 Licencia

Proyecto académico — Uso educativo.

---

<p align="center">
  <b>🏺 RutaCerámica</b><br/>
  Conectando tradición, turismo y tecnología<br/>
  Hecho con ❤️ en Dolores Hidalgo, Guanajuato, México
</p>