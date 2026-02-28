package com.utng.rutaceramica.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utng.rutaceramica.data.model.Taller
import com.utng.rutaceramica.data.repository.TallerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado y lógica de negocio de los Talleres.
 * Expone listas de talleres y estados de carga/error para la UI de Compose.
 * Es usado por las pantallas de Turista, Dueño y Administrador.
 */
class TallerViewModel : ViewModel() {

    private val repository = TallerRepository()

    private val _talleres = MutableStateFlow<List<Taller>>(emptyList())
    val talleres: StateFlow<List<Taller>> = _talleres

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    /**
     * Carga los talleres aprobados para mostrar al Turista en el mapa y en la lista.
     */
    fun cargarTalleresAprobados() {
        viewModelScope.launch {
            _cargando.value = true
            repository.obtenerTalleresAprobados()
                .onSuccess { lista -> _talleres.value = lista }
                .onFailure { e -> _error.value = e.message }
            _cargando.value = false
        }
    }

    /**
     * Carga los talleres de un artesano específico (para el módulo Dueño).
     *
     * @param idArtesano UID del artesano dueño.
     */
    fun cargarTalleresDeArtesano(idArtesano: String) {
        viewModelScope.launch {
            _cargando.value = true
            repository.obtenerTalleresDeArtesano(idArtesano)
                .onSuccess { lista -> _talleres.value = lista }
                .onFailure { e -> _error.value = e.message }
            _cargando.value = false
        }
    }

    /**
     * Carga todos los talleres sin filtro (para el panel Admin).
     */
    fun cargarTodosTalleres() {
        viewModelScope.launch {
            _cargando.value = true
            repository.obtenerTodosTalleres()
                .onSuccess { lista -> _talleres.value = lista }
                .onFailure { e -> _error.value = e.message }
            _cargando.value = false
        }
    }

    /**
     * Crea un nuevo taller. Si se proporciona una imagen, primero la sube
     * a Firebase Storage y luego guarda el taller con la URL de la foto.
     *
     * @param taller Datos del taller a crear.
     * @param imagenUri URI de la foto del taller (opcional).
     */
    fun crearTaller(taller: Taller, imagenUri: Uri? = null) {
        viewModelScope.launch {
            _cargando.value = true
            var tallerFinal = taller
            // Si hay imagen, subirla primero con un ID temporal
            if (imagenUri != null) {
                val idTemp = System.currentTimeMillis().toString()
                repository.subirFotoTaller(idTemp, imagenUri)
                    .onSuccess { url -> tallerFinal = taller.copy(fotoUrl = url) }
            }
            repository.crearTaller(tallerFinal)
                .onSuccess {
                    _mensaje.value = "Taller creado exitosamente"
                    cargarTalleresDeArtesano(taller.idArtesano)
                }
                .onFailure { e -> _error.value = e.message }
            _cargando.value = false
        }
    }

    /**
     * Actualiza los datos de un taller existente, subiendo la nueva foto si se proporciona.
     *
     * @param taller Datos actualizados del taller.
     * @param imagenUri Nueva foto del taller (null si no cambia).
     */
    fun actualizarTaller(taller: Taller, imagenUri: Uri? = null) {
        viewModelScope.launch {
            _cargando.value = true
            var tallerFinal = taller
            if (imagenUri != null) {
                repository.subirFotoTaller(taller.idTaller, imagenUri)
                    .onSuccess { url -> tallerFinal = taller.copy(fotoUrl = url) }
            }
            repository.actualizarTaller(tallerFinal)
                .onSuccess { _mensaje.value = "Taller actualizado" }
                .onFailure { e -> _error.value = e.message }
            _cargando.value = false
        }
    }

    /**
     * Elimina un taller de Firestore.
     *
     * @param idTaller ID del taller a eliminar.
     * @param idArtesano UID del artesano (para recargar su lista después).
     */
    fun eliminarTaller(idTaller: String, idArtesano: String) {
        viewModelScope.launch {
            repository.eliminarTaller(idTaller)
                .onSuccess {
                    _mensaje.value = "Taller eliminado"
                    cargarTalleresDeArtesano(idArtesano)
                }
                .onFailure { e -> _error.value = e.message }
        }
    }

    /**
     * Aprueba un taller (operación exclusiva del Admin).
     *
     * @param idTaller ID del taller a aprobar.
     */
    fun aprobarTaller(idTaller: String) {
        viewModelScope.launch {
            repository.aprobarTaller(idTaller)
                .onSuccess {
                    _mensaje.value = "Taller aprobado"
                    cargarTodosTalleres()
                }
                .onFailure { e -> _error.value = e.message }
        }
    }

    /** Limpia los mensajes de error y éxito. */
    fun limpiarMensajes() {
        _error.value = null
        _mensaje.value = null
    }
}