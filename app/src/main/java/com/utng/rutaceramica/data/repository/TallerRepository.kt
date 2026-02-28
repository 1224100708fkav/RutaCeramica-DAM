package com.utng.rutaceramica.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.utng.rutaceramica.data.model.Taller
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona todas las operaciones CRUD de la entidad Taller
 * en Firebase Firestore, así como la subida de fotos a Firebase Storage.
 */
class TallerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val talleresCol = db.collection("talleres")

    /**
     * Obtiene la lista completa de talleres aprobados desde Firestore.
     * Solo los talleres con [Taller.aprobado] = true son visibles para turistas.
     *
     * @return [Result] con lista de [Taller] aprobados o excepción si falla.
     */
    suspend fun obtenerTalleresAprobados(): Result<List<Taller>> {
        return try {
            val snapshot = talleresCol
                .whereEqualTo("aprobado", true)
                .get().await()
            val talleres = snapshot.toObjects(Taller::class.java)
            Result.success(talleres)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los talleres de un artesano específico (para el módulo Dueño).
     *
     * @param idArtesano UID del artesano dueño de los talleres.
     * @return [Result] con lista de [Taller] del artesano.
     */
    suspend fun obtenerTalleresDeArtesano(idArtesano: String): Result<List<Taller>> {
        return try {
            val snapshot = talleresCol
                .whereEqualTo("idArtesano", idArtesano)
                .get().await()
            Result.success(snapshot.toObjects(Taller::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene TODOS los talleres sin filtro (para el panel del Administrador).
     *
     * @return [Result] con lista completa de talleres.
     */
    suspend fun obtenerTodosTalleres(): Result<List<Taller>> {
        return try {
            val snapshot = talleresCol.get().await()
            Result.success(snapshot.toObjects(Taller::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea un nuevo Taller en Firestore. El ID se genera automáticamente.
     * El taller inicia con [Taller.aprobado] = false hasta que el Admin lo apruebe.
     *
     * @param taller Objeto [Taller] con los datos del nuevo taller.
     * @return [Result] con el [Taller] creado (con su ID asignado) o excepción.
     */
    suspend fun crearTaller(taller: Taller): Result<Taller> {
        return try {
            val docRef = talleresCol.document()
            val tallerConId = taller.copy(idTaller = docRef.id)
            docRef.set(tallerConId).await()
            Result.success(tallerConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos de un Taller existente en Firestore.
     *
     * @param taller Objeto [Taller] con los datos actualizados. Debe tener [Taller.idTaller] válido.
     * @return [Result] indicando éxito o excepción.
     */
    suspend fun actualizarTaller(taller: Taller): Result<Unit> {
        return try {
            talleresCol.document(taller.idTaller).set(taller).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un Taller de Firestore por su ID.
     *
     * @param idTaller ID del taller a eliminar.
     * @return [Result] indicando éxito o excepción.
     */
    suspend fun eliminarTaller(idTaller: String): Result<Unit> {
        return try {
            talleresCol.document(idTaller).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Aprueba un taller cambiando su campo [Taller.aprobado] a true en Firestore.
     * Esta operación solo puede ser ejecutada por el Administrador.
     *
     * @param idTaller ID del taller a aprobar.
     * @return [Result] indicando éxito o excepción.
     */
    suspend fun aprobarTaller(idTaller: String): Result<Unit> {
        return try {
            talleresCol.document(idTaller)
                .update("aprobado", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sube una imagen al Firebase Storage en la ruta "talleres/{idTaller}/foto.jpg"
     * y retorna la URL pública de descarga.
     *
     * @param idTaller ID del taller, usado para organizar la imagen en Storage.
     * @param imagenUri URI local de la imagen seleccionada o capturada.
     * @return [Result] con la URL pública de la imagen o excepción si falla.
     */
    suspend fun subirFotoTaller(idTaller: String, imagenUri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child("talleres/$idTaller/foto.jpg")
            ref.putFile(imagenUri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}