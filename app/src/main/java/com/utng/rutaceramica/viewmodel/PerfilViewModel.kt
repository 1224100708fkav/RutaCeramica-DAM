package com.utng.rutaceramica.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utng.rutaceramica.data.model.Usuario
import com.utng.rutaceramica.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar la lógica de la pantalla de perfil del usuario.
 * Permite cargar y actualizar los datos del perfil, incluyendo la foto.
 */
class PerfilViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    /**
     * Carga el perfil del usuario desde Firestore.
     */
    fun cargarPerfil(uid: String) {
        viewModelScope.launch {
            _cargando.value = true
            repository.obtenerPerfil(uid)
                .onSuccess { _usuario.value = it }
                .onFailure { _mensaje.value = "Error al cargar perfil: ${it.message}" }
            _cargando.value = false
        }
    }

    /**
     * Actualiza los datos del perfil en Firestore y Storage.
     */
    fun actualizarPerfil(nuevoNombre: String, nuevoOrigen: String, nuevaDescripcion: String, nuevaFotoUri: Uri? = null) {
        val currentUsuario = _usuario.value ?: return
        
        viewModelScope.launch {
            _cargando.value = true
            val usuarioActualizado = currentUsuario.copy(
                nombre = nuevoNombre,
                origen = nuevoOrigen,
                descripcion = nuevaDescripcion
            )
            
            repository.actualizarPerfil(usuarioActualizado, nuevaFotoUri)
                .onSuccess {
                    _usuario.value = it
                    _mensaje.value = "Perfil actualizado correctamente"
                }
                .onFailure { _mensaje.value = "Error al actualizar: ${it.message}" }
            _cargando.value = false
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
