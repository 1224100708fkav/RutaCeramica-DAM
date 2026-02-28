package com.utng.rutaceramica.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.utng.rutaceramica.data.model.Bitacora
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona el CRUD de la Bitácora personal del Turista.
 * Incluye la subida de fotos capturadas con CameraX a Firebase Storage.
 */
class BitacoraRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val bitacoraCol = db.collection("bitacora")

    /**
     * Obtiene todas las entradas de la bitácora de un usuario específico.
     *
     * @param idUsuario UID del turista propietario de la bitácora.
     */
    suspend fun obtenerBitacoraDeUsuario(idUsuario: String): Result<List<Bitacora>> {
        return try {
            val snapshot = bitacoraCol
                .whereEqualTo("idUsuario", idUsuario)
                .get().await()
            Result.success(snapshot.toObjects(Bitacora::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva entrada en la Bitácora en Firestore.
     *
     * @param bitacora Datos de la nueva entrada.
     */
    suspend fun crearEntrada(bitacora: Bitacora): Result<Bitacora> {
        return try {
            val docRef = bitacoraCol.document()
            val entradaConId = bitacora.copy(idBitacora = docRef.id)
            docRef.set(entradaConId).await()
            Result.success(entradaConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza una entrada existente de la Bitácora.
     *
     * @param bitacora Objeto [Bitacora] con datos actualizados.
     */
    suspend fun actualizarEntrada(bitacora: Bitacora): Result<Unit> {
        return try {
            bitacoraCol.document(bitacora.idBitacora).set(bitacora).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una entrada de la Bitácora de Firestore.
     *
     * @param idBitacora ID de la entrada a eliminar.
     */
    suspend fun eliminarEntrada(idBitacora: String): Result<Unit> {
        return try {
            bitacoraCol.document(idBitacora).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sube una foto de la bitácora a Firebase Storage.
     * La foto fue capturada con CameraX y se guarda en la ruta:
     * "bitacora/{idUsuario}/{idBitacora}.jpg"
     *
     * @param idUsuario UID del turista, para organizar las fotos por usuario.
     * @param idBitacora ID de la entrada de bitácora asociada.
     * @param fotoUri URI local de la imagen capturada por CameraX.
     * @return [Result] con la URL pública de la foto en Storage.
     */
    suspend fun subirFoto(idUsuario: String, idBitacora: String, fotoUri: Uri): Result<String> {
        return try {
            val ref = storage.reference
                .child("bitacora/$idUsuario/$idBitacora.jpg")
            ref.putFile(fotoUri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}