package com.utng.rutaceramica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utng.rutaceramica.data.model.Usuario
import com.utng.rutaceramica.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado de autenticación de la aplicación.
 * Actúa como intermediario entre las pantallas de Auth y el [AuthRepository].
 * Expone el estado de la UI mediante [StateFlow] para que Compose
 * reaccione automáticamente a los cambios.
 */
class AuthViewModel : ViewModel() {

    /** Repositorio de autenticación que ejecuta las operaciones con Firebase. */
    private val repository = AuthRepository()

    /** Estado de carga: true mientras se espera respuesta de Firebase. */
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    /** Mensaje de error para mostrar en la UI, null si no hay error. */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Usuario autenticado actualmente, null si no hay sesión activa. */
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    /**
     * Verifica si ya existe una sesión activa al abrir la app.
     * Si hay sesión, carga el perfil del usuario desde Firestore
     * para obtener su rol y datos actualizados.
     */
    fun verificarSesion() {
        val usuarioFirebase = repository.getUsuarioActual()
        if (usuarioFirebase != null) {
            viewModelScope.launch {
                val resultado = repository.obtenerPerfil(usuarioFirebase.uid)
                resultado.onSuccess { usuario ->
                    _usuario.value = usuario
                }
            }
        }
    }

    /**
     * Ejecuta el proceso de registro de un nuevo usuario.
     * Al completarse con éxito, actualiza [_usuario] con los datos del nuevo usuario.
     *
     * @param nombre Nombre completo del usuario.
     * @param email Correo electrónico.
     * @param password Contraseña (mínimo 6 caracteres).
     */
    fun registrar(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = repository.registrar(nombre, email, password)
            resultado.onSuccess { usuario ->
                _usuario.value = usuario
            }.onFailure { excepcion ->
                _error.value = excepcion.message ?: "Error al registrar"
            }
            _cargando.value = false
        }
    }

    /**
     * Ejecuta el proceso de inicio de sesión con correo y contraseña.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            val resultado = repository.login(email, password)
            resultado.onSuccess { usuario ->
                _usuario.value = usuario
            }.onFailure { excepcion ->
                _error.value = excepcion.message ?: "Error al iniciar sesión"
            }
            _cargando.value = false
        }
    }

    /**
     * Cierra la sesión del usuario actual y limpia el estado de la UI.
     */
    fun logout() {
        repository.logout()
        _usuario.value = null
        _error.value = null
    }

    /**
     * Limpia el mensaje de error actual (ej: después de mostrarlo en la UI).
     */
    fun limpiarError() {
        _error.value = null
    }
}