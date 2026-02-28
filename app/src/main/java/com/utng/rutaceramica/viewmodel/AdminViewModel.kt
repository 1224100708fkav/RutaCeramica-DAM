package com.utng.rutaceramica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utng.rutaceramica.data.model.Usuario
import com.utng.rutaceramica.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel exclusivo del módulo Administrador.
 * Gestiona la lista de usuarios y las operaciones de cambio de rol.
 */
class AdminViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    /**
     * Carga la lista completa de usuarios registrados en la app.
     */
    fun cargarUsuarios() {
        viewModelScope.launch {
            _cargando.value = true
            repository.obtenerTodosUsuarios()
                .onSuccess { lista -> _usuarios.value = lista }
            _cargando.value = false
        }
    }

    /**
     * Cambia el rol de un usuario y recarga la lista de usuarios.
     *
     * @param idUsuario UID del usuario a modificar.
     * @param nuevoRol Nuevo rol: "turista", "dueno" o "admin".
     */
    fun cambiarRol(idUsuario: String, nuevoRol: String) {
        viewModelScope.launch {
            repository.cambiarRolUsuario(idUsuario, nuevoRol)
                .onSuccess {
                    _mensaje.value = "Rol actualizado a $nuevoRol"
                    cargarUsuarios()
                }
        }
    }

    /** Limpia el mensaje actual. */
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}