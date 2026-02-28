package com.utng.rutaceramica.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utng.rutaceramica.data.model.Bitacora
import com.utng.rutaceramica.data.repository.BitacoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel que gestiona la lógica del módulo Bitácora del Turista.
 * Coordina la captura de fotos con CameraX, la subida a Storage
 * y el CRUD de entradas en Firestore.
 */
class BitacoraViewModel : ViewModel() {

    private val repository = BitacoraRepository()

    private val _entradas = MutableStateFlow<List<Bitacora>>(emptyList())
    val entradas: StateFlow<List<Bitacora>> = _entradas

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    /**
     * Carga todas las entradas de la bitácora del usuario activo.
     *
     * @param idUsuario UID del turista propietario de la bitácora.
     */
    fun cargarBitacora(idUsuario: String) {
        viewModelScope.launch {
            _cargando.value = true
            repository.obtenerBitacoraDeUsuario(idUsuario)
                .onSuccess { lista -> _entradas.value = lista }
                .onFailure { e -> _error.value = e.message }
            _cargando.value = false
        }
    }

    /**
     * Crea una nueva entrada en la Bitácora.
     * Si se proporciona una foto (capturada con CameraX), primero la sube
     * a Firebase Storage y luego guarda la entrada con la URL resultante.
     *
     * @param titulo Título de la entrada.
     * @param comentario Comentario o descripción del recuerdo.
     * @param idUsuario UID del turista.
     * @param idTaller ID del taller relacionado (puede ser vacío).
     * @param fotoUri URI de la foto capturada con la cámara (opcional).
     */
    fun crearEntrada(
        titulo: String,
        comentario: String,
        idUsuario: String,
        idTaller: String = "",
        fotoUri: Uri? = null
    ) {
        viewModelScope.launch {
            _cargando.value = true
            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date())

            // Crear entrada temporal para obtener un ID
            val entradaTemp = Bitacora(
                titulo = titulo,
                comentario = comentario,
                idUsuario = idUsuario,
                idTaller = idTaller,
                fecha = fecha
            )

            // Crear la entrada primero para obtener el ID de Firestore
            repository.crearEntrada(entradaTemp)
                .onSuccess { entradaCreada ->
                    // Si hay foto, subirla y actualizar la entrada con la URL
                    if (fotoUri != null) {
                        repository.subirFoto(idUsuario, entradaCreada.idBitacora, fotoUri)
                            .onSuccess { url ->
                                val entradaConFoto = entradaCreada.copy(fotoUrl = url)
                                repository.actualizarEntrada(entradaConFoto)
                            }
                    }
                    _mensaje.value = "Recuerdo guardado en tu bitácora"
                    cargarBitacora(idUsuario)
                }
                .onFailure { e -> _error.value = e.message }
            _cargando.value = false
        }
    }

    /**
     * Elimina una entrada de la Bitácora.
     *
     * @param idBitacora ID de la entrada a eliminar.
     * @param idUsuario UID del usuario (para recargar la lista).
     */
    fun eliminarEntrada(idBitacora: String, idUsuario: String) {
        viewModelScope.launch {
            repository.eliminarEntrada(idBitacora)
                .onSuccess {
                    _mensaje.value = "Entrada eliminada"
                    cargarBitacora(idUsuario)
                }
                .onFailure { e -> _error.value = e.message }
        }
    }

    /** Limpia mensajes de error y éxito. */
    fun limpiarMensajes() {
        _error.value = null
        _mensaje.value = null
    }
}