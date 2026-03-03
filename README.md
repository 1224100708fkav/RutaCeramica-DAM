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

**RutaCerámica** es una app móvil Android desarrollada en **Kotlin + Jetpack Compose** que permite explorar talleres artesanales, gestionar usuarios con roles y recibir notificaciones en tiempo real.

### ✨ Funcionalidades

| Funcionalidad | Descripción |
|---|---|
| 🔐 **Autenticación** | Registro e inicio de sesión con Firebase Authentication |
| 👑 **Roles** | Administrador y Usuario con permisos distintos |
| 👤 **CRUD Usuarios** | Crear, leer, actualizar y eliminar cuentas |
| 🏺 **CRUD Talleres** | Gestión completa de talleres artesanales |
| 🗺️ **Google Maps** | Mapa interactivo con ubicación de talleres |
| 📡 **Sensores** | GPS, acelerómetro, temperatura, brújula, luz ambiental |
| 📷 **Cámara** | Escaneo y reconocimiento de piezas cerámicas con IA |
| 🔔 **Notificaciones** | Push notifications con Firebase Cloud Messaging |
| 🔒 **Seguridad** | Reglas Firestore, biométrico y HTTPS/TLS |
| 🤖 **IA + AR** | Guía inteligente con realidad aumentada |

---

## 🏗️ Arquitectura — MVVM

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

## 📁 Estructura del Proyecto

```
app/
├── data/
│   ├── model/
│   │   ├── Usuario.kt
│   │   └── Ubicacion.kt
│   └── repository/
│       ├── AuthRepository.kt
│       └── UbicacionRepository.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   └── UbicacionViewModel.kt
├── ui/
│   ├── screen/
│   │   ├── LoginActivity.kt
│   │   ├── RegistroActivity.kt
│   │   ├── HomeScreen.kt
│   │   ├── MapaScreen.kt
│   │   ├── PerfilScreen.kt
│   │   ├── CamaraScreen.kt
│   │   ├── SensorScreen.kt
│   │   ├── NotificacionesScreen.kt
│   │   └── AdminActivity.kt
│   ├── components/
│   │   └── Components.kt
│   └── theme/
│       └── Theme.kt
├── navigation/
│   ├── Screen.kt
│   └── NavGraph.kt
├── MainActivity.kt
├── MensajeriaService.kt
└── AndroidManifest.xml
```

---

## 🧩 Modelos de Datos

### `Usuario.kt`

```kotlin
package com.utng.rutaceramica.data.model

/**
 * Modelo de usuario registrado en Firebase Auth + Firestore.
 *
 * @property uid           ID único de Firebase Auth
 * @property nombre        Nombre para mostrar
 * @property email         Correo electrónico
 * @property telefono      Número de contacto
 * @property rol           "admin" | "usuario"
 * @property fechaRegistro Timestamp de creación
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

### `Ubicacion.kt`

```kotlin
package com.utng.rutaceramica.data.model

data class Ubicacion(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val tipo: String = "",        // "Talavera" | "Barro" | "Mayólica"
    val artesano: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val horario: String = "",
    val telefono: String = ""
)
```

---

## 🗄️ AuthRepository — CRUD Completo

```kotlin
package com.utng.rutaceramica.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.utng.rutaceramica.data.model.Usuario
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()
    private val coleccion = "usuarios"

    // ── CREATE ─────────────────────────────────────────────────────────────
    suspend fun registrarUsuario(
        email: String,
        password: String,
        nombre: String,
        telefono: String,
        rol: String = "usuario"
    ): Result<Usuario> {
        return try {
            val resultado = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = resultado.user
                ?: return Result.failure(Exception("No se pudo crear el usuario"))

            val usuario = Usuario(
                uid = firebaseUser.uid,
                nombre = nombre,
                email = email,
                telefono = telefono,
                rol = rol
            )

            db.collection(coleccion)
                .document(firebaseUser.uid)
                .set(usuario)
                .await()

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── READ — Login ───────────────────────────────────────────────────────
    suspend fun iniciarSesion(email: String, password: String): Result<Usuario> {
        return try {
            val resultado = auth
                .signInWithEmailAndPassword(email, password)
                .await()

            val uid = resultado.user?.uid
                ?: return Result.failure(Exception("Credenciales inválidas"))

            obtenerUsuario(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── READ — Obtener por UID ─────────────────────────────────────────────
    suspend fun obtenerUsuario(uid: String): Result<Usuario> {
        return try {
            val doc = db.collection(coleccion)
                .document(uid)
                .get()
                .await()

            val usuario = doc.toObject(Usuario::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── READ — Todos los usuarios (Admin) ──────────────────────────────────
    suspend fun obtenerTodosUsuarios(): Result<List<Usuario>> {
        return try {
            val snapshot = db.collection(coleccion).get().await()
            Result.success(snapshot.toObjects(Usuario::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── UPDATE — Datos personales ──────────────────────────────────────────
    suspend fun actualizarUsuario(
        uid: String,
        nombre: String,
        telefono: String
    ): Result<Unit> {
        return try {
            db.collection(coleccion)
                .document(uid)
                .update(mapOf("nombre" to nombre, "telefono" to telefono))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── UPDATE — Contraseña ────────────────────────────────────────────────
    suspend fun cambiarPassword(nuevaPassword: String): Result<Unit> {
        return try {
            auth.currentUser?.updatePassword(nuevaPassword)?.await()
                ?: return Result.failure(Exception("Sin usuario activo"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────────────
    suspend fun eliminarUsuario(uid: String): Result<Unit> {
        return try {
            db.collection(coleccion).document(uid).delete().await()
            if (auth.currentUser?.uid == uid) {
                auth.currentUser?.delete()?.await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun usuarioActual(): FirebaseUser? = auth.currentUser
    fun cerrarSesion()                 = auth.signOut()
}
```

---

## 🧠 AuthViewModel — Estado Reactivo

```kotlin
package com.utng.rutaceramica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utng.rutaceramica.data.model.Usuario
import com.utng.rutaceramica.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val mensaje: String) : AuthUiState()
    data class Error(val mensaje: String)   : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    val haySession get() = repo.usuarioActual() != null

    // CREATE
    fun registrarUsuario(
        email: String, password: String,
        nombre: String, telefono: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repo.registrarUsuario(email, password, nombre, telefono)
                .onSuccess { u ->
                    _usuario.value = u
                    _uiState.value = AuthUiState.Success("¡Bienvenido, ${u.nombre}!")
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Error al registrar")
                }
        }
    }

    // READ / LOGIN
    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repo.iniciarSesion(email, password)
                .onSuccess { u ->
                    _usuario.value = u
                    _uiState.value = AuthUiState.Success("Bienvenido, ${u.nombre}")
                }
                .onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Error al iniciar sesión")
                }
        }
    }

    // READ / PERFIL
    fun cargarPerfil() {
        val uid = repo.usuarioActual()?.uid ?: return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repo.obtenerUsuario(uid)
                .onSuccess { u -> _usuario.value = u; _uiState.value = AuthUiState.Idle }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error") }
        }
    }

    // UPDATE
    fun actualizarUsuario(nombre: String, telefono: String) {
        val uid = repo.usuarioActual()?.uid ?: return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repo.actualizarUsuario(uid, nombre, telefono)
                .onSuccess {
                    _usuario.value = _usuario.value?.copy(nombre = nombre, telefono = telefono)
                    _uiState.value = AuthUiState.Success("Datos actualizados")
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error") }
        }
    }

    // UPDATE contraseña
    fun cambiarPassword(nueva: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repo.cambiarPassword(nueva)
                .onSuccess { _uiState.value = AuthUiState.Success("Contraseña actualizada") }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error") }
        }
    }

    // DELETE
    fun eliminarCuenta() {
        val uid = repo.usuarioActual()?.uid ?: return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repo.eliminarUsuario(uid)
                .onSuccess {
                    _usuario.value = null
                    _uiState.value = AuthUiState.Success("Cuenta eliminada")
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Error") }
        }
    }

    fun cerrarSesion() { repo.cerrarSesion(); _usuario.value = null }
    fun resetState()   { _uiState.value = AuthUiState.Idle }
}
```

---

## 📱 Pantallas

### `RegistroActivity.kt`

```kotlin
package com.utng.rutaceramica.ui.screen

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utng.rutaceramica.R
import com.utng.rutaceramica.viewmodel.AuthUiState
import com.utng.rutaceramica.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.utng.rutaceramica.MainActivity

class RegistroActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNombre    = findViewById<EditText>(R.id.etNombre)
        val etEmail     = findViewById<EditText>(R.id.etEmail)
        val etTelefono  = findViewById<EditText>(R.id.etTelefono)
        val etPassword  = findViewById<EditText>(R.id.etPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnIrLogin   = findViewById<Button>(R.id.btnIrLogin)
        val progressBar  = findViewById<ProgressBar>(R.id.progressBar)

        btnRegistrar.setOnClickListener {
            val nombre   = etNombre.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Mínimo 6 caracteres en la contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.registrarUsuario(email, password, nombre, telefono)
        }

        btnIrLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is AuthUiState.Loading -> {
                        progressBar.visibility = ProgressBar.VISIBLE
                        btnRegistrar.isEnabled = false
                    }
                    is AuthUiState.Success -> {
                        progressBar.visibility = ProgressBar.GONE
                        Toast.makeText(this@RegistroActivity, state.mensaje, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegistroActivity, MainActivity::class.java))
                        finish()
                    }
                    is AuthUiState.Error -> {
                        progressBar.visibility = ProgressBar.GONE
                        btnRegistrar.isEnabled = true
                        Toast.makeText(this@RegistroActivity, state.mensaje, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        progressBar.visibility = ProgressBar.GONE
                        btnRegistrar.isEnabled = true
                    }
                }
            }
        }
    }
}
```

### `LoginActivity.kt`

```kotlin
package com.utng.rutaceramica.ui.screen

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utng.rutaceramica.R
import com.utng.rutaceramica.viewmodel.AuthUiState
import com.utng.rutaceramica.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.utng.rutaceramica.MainActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.haySession) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail    = findViewById<EditText>(R.id.etEmailLogin)
        val etPassword = findViewById<EditText>(R.id.etPasswordLogin)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)
        val btnRegistro = findViewById<Button>(R.id.btnIrRegistro)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.iniciarSesion(email, pass)
        }

        btnRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is AuthUiState.Loading -> progressBar.visibility = ProgressBar.VISIBLE
                    is AuthUiState.Success -> {
                        progressBar.visibility = ProgressBar.GONE
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    is AuthUiState.Error -> {
                        progressBar.visibility = ProgressBar.GONE
                        Toast.makeText(this@LoginActivity, state.mensaje, Toast.LENGTH_LONG).show()
                    }
                    else -> progressBar.visibility = ProgressBar.GONE
                }
            }
        }
    }
}
```

---

## 🗺️ Navegación

### `Screen.kt`

```kotlin
package com.utng.rutaceramica.navigation

sealed class Screen(val route: String) {
    object Login          : Screen("login")
    object Registro       : Screen("registro")
    object Home           : Screen("home")
    object Mapa           : Screen("mapa")
    object Perfil         : Screen("perfil")
    object Camara         : Screen("camara")
    object Sensores       : Screen("sensores")
    object Notificaciones : Screen("notificaciones")
    object GuiaIA         : Screen("guia_ia")
}
```

### `NavGraph.kt`

```kotlin
package com.utng.rutaceramica.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.utng.rutaceramica.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController, vm: AuthViewModel = viewModel()) {

    val start = if (vm.haySession) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = start) {

        composable(Screen.Login.route) {
            LoginScreen(
                vm           = vm,
                onLoginOk    = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onIrRegistro = { navController.navigate(Screen.Registro.route) }
            )
        }

        composable(Screen.Registro.route) {
            RegistroScreen(
                vm           = vm,
                onRegistroOk = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onIrLogin    = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                vm         = vm,
                onIrPerfil = { navController.navigate(Screen.Perfil.route) },
                onIrMapa   = { navController.navigate(Screen.Mapa.route) }
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                vm       = vm,
                onSalir  = { navController.navigate(Screen.Login.route) { popUpTo(0) } },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
```

---

## 🔔 MensajeriaService — FCM

```kotlin
package com.utng.rutaceramica

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio Firebase Cloud Messaging para notificaciones push.
 * Registrado en AndroidManifest bajo MESSAGING_EVENT.
 */
class MensajeriaService : FirebaseMessagingService() {

    // Se ejecuta cuando llega una notificación con la app activa
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let { notif ->
            mostrarNotificacion(notif.title ?: "", notif.body ?: "")
        }
    }

    // Se ejecuta cuando Firebase renueva el token del dispositivo
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Guarda el nuevo token en Firestore del usuario actual
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        // Implementar NotificationManager aquí
    }
}
```

---

## 🎨 Tema — Theme.kt

```kotlin
package com.utng.rutaceramica.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Paleta Ruta Cerámica ───────────────────────────────────────────────────
val TerraCotta  = Color(0xFFC9622A)  // Primario — botones y headers
val TerraDeep   = Color(0xFF8B3E12)  // Gradientes oscuros
val TerraLight  = Color(0xFFE8845A)  // Acentos y hover
val Sand        = Color(0xFFF5F0EB)  // Fondo general
val Cream       = Color(0xFFFBF7F3)  // Fondo de campos
val BrownDark   = Color(0xFF2D1A0A)  // Texto principal
val BrownMid    = Color(0xFF5A3A1A)  // Labels y subtítulos
val BrownLight  = Color(0xFF8A6A50)  // Texto secundario
val InputBorder = Color(0xFFDDD0C4)  // Bordes de inputs
val RedDanger   = Color(0xFFC0392B)  // Eliminar / errores

private val RutaColorScheme = lightColorScheme(
    primary        = TerraCotta,
    onPrimary      = Color.White,
    background     = Sand,
    onBackground   = BrownDark,
    surface        = Color.White,
    onSurface      = BrownDark,
    surfaceVariant = Cream,
    outline        = InputBorder,
    error          = RedDanger,
)

@Composable
fun RutaCeramicaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = RutaColorScheme, content = content)
}
```

---

## 📄 AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Ruta Cerámica"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.RutaCeramica">

        <activity
            android:name=".ui.screen.LoginActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        <activity android:name=".ui.screen.RegistroActivity" android:exported="false"/>
        <activity android:name=".ui.screen.AdminActivity"    android:exported="false"/>
        <activity android:name=".MainActivity"               android:exported="false"/>

        <service
            android:name=".MensajeriaService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT"/>
            </intent-filter>
        </service>

    </application>
</manifest>
```

---

## ⚙️ Dependencias — app/build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace   = "com.utng.rutaceramica"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.utng.rutaceramica"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"
    }

    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}

dependencies {
    // ── UI y Core ──────────────────────────────────────────────────────────
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation("androidx.compose.material:material-icons-extended")

    // ── Navegación y ViewModel ─────────────────────────────────────────────
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // ── Firebase ───────────────────────────────────────────────────────────
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // ── Coroutines — OBLIGATORIO para Firebase con .await() ────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // ── Google Maps y GPS ──────────────────────────────────────────────────
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // ── Cámara ─────────────────────────────────────────────────────────────
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // ── Permisos en Compose ────────────────────────────────────────────────
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // ── Imágenes ───────────────────────────────────────────────────────────
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.guava:guava:33.0.0-android")
}
```

---

## 🔥 Configuración Firebase

### Paso 1 — Crear Proyecto

1. Ve a [console.firebase.google.com](https://console.firebase.google.com)
2. Clic en **"Crear un proyecto"** → Nombre: `RutaCeramica`
3. Acepta los términos → **Crear proyecto**

### Paso 2 — Registrar App Android

1. Clic en el ícono Android `</>`
2. Nombre del paquete: `com.utng.rutaceramica`
3. Descarga `google-services.json` y colócalo en `app/google-services.json` ✅

### Paso 3 — Activar Authentication

```
Firebase Console → Authentication → Sign-in method
→ Correo electrónico/Contraseña → Habilitar → Guardar ✅
```

### Paso 4 — Crear Firestore

```
Firebase Console → Firestore Database → Crear base de datos
→ Modo de prueba → Listo ✅
```

---

## 🗄️ Estructura Firestore

### Colección `usuarios`

```
usuarios/
   └── {uid}
        ├── uid:           String
        ├── nombre:        String
        ├── email:         String
        ├── telefono:      String
        ├── rol:           String   ("admin" | "usuario")
        └── fechaRegistro: Number
```

### Colección `talleres`

```
talleres/
   └── {documentId}
        ├── nombre:      String
        ├── descripcion: String
        ├── tipo:        String   ("Talavera" | "Barro" | "Mayólica")
        ├── artesano:    String
        ├── latitud:     Number
        ├── longitud:    Number
        ├── horario:     String
        └── telefono:    String
```

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

## 👑 Crear Usuario Administrador

1. Registra el usuario normalmente en la app
2. Ve a Firebase Console → **Firestore Database**
3. Abre la colección `usuarios` → busca el documento del usuario
4. Edita el campo `rol`:

```
rol: "admin"
```

---

## 📱 Pantallas

| # | Pantalla | Descripción |
|---|---------|-------------|
| 01 |  Splash | Carga inicial con animación |
| 02 |  Login | Inicio de sesión — READ Firebase Auth |
| 03 |  Registro | Crear cuenta — CREATE Firebase Auth |
| 04 |  Home | Lista de 5 talleres artesanales |
| 05 |  Mapa | Google Maps con marcadores interactivos |
| 06 |  Detalle Taller | Info completa + ruta al taller |
| 07 |  Perfil | Editar datos / eliminar cuenta — UPDATE / DELETE |
| 08 |  Cámara | Escaneo de piezas con reconocimiento IA |
| 09 |  Sensores | GPS, temperatura, acelerómetro, brújula |
| 10 |  Notificaciones | Centro de notificaciones FCM |
| 11 |  Seguridad | Auth + biométrico + HTTPS |
| 12 |  Guía IA | AR + ruta personalizada inteligente |


```

---

## 💻 Instalación

```bash
# 1. Clonar el repositorio
git clone https://github.com/TU_USUARIO/RutaCeramica.git
cd RutaCeramica

# 2. Copiar google-services.json (descárgalo de Firebase Console)
cp ~/Descargas/google-services.json app/google-services.json

# 3. Abrir en Android Studio
# File → Open → Selecciona la carpeta RutaCeramica

# 4. Sincronizar Gradle
# Build → Sync Project with Gradle Files

# 5. Ejecutar la app
# Run → Run 'app'  (Shift + F10)
```

---

## 📋 Requisitos del Sistema

| Requisito | Versión |
|-----------|---------|
| Android Studio | Hedgehog 2023.1.1+ |
| Kotlin | 2.0.21 |
| compileSdk | 35 |
| minSdk | 26 (Android 8.0) |
| JDK | 11+ |
| Gradle | 8.9 |

---

## 🐛 Solución de Problemas

| Error | Causa | Solución |
|-------|-------|----------|
| `google-services.json not found` | Archivo en carpeta incorrecta | Colócalo en `app/google-services.json` |
| `FirebaseApp is not initialized` | JSON inválido o paquete incorrecto | Verifica `package_name = com.utng.rutaceramica` |
| `PERMISSION_DENIED` Firestore | Reglas expiradas | Actualiza reglas en Firebase Console |
| App se congela al registrar | Falta `coroutines-play-services` | Agrega la dependencia en `build.gradle.kts` |
| No funciona Authentication | No activado | Activa Email/Contraseña en Firebase → Auth |
| Gradle no sincroniza | Caché corrupta | `File → Invalidate Caches → Restart` |

---

## 📄 .gitignore

```gitignore
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar
local.properties
*.iml
.idea/
.navigation/
captures/
.externalNativeBuild
.cxx/
.DS_Store
Thumbs.db
google-services.json
*.log
*.apk
*.aab
```

---

## 👩‍💻 Autora

**Fabián Karel Avalos Velázquez y Angel Daniel Hernandez Mandujano**
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
