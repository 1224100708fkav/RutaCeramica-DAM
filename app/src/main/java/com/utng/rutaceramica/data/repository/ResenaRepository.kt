package com.utng.rutaceramica.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.utng.rutaceramica.data.model.Resena
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona las operaciones CRUD de la entidad Reseña en Firestore.
 */
class ResenaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val resenasCol = db.collection("resenas")

    /**
     * Obtiene todas las reseñas de un taller específico.
     *
     * @param idTaller ID del taller cuyas reseñas se quieren obtener.
     */
    suspend fun obtenerResenasDeTaller(idTaller: String): Result<List<Resena>> {
        return try {
            val snapshot = resenasCol
                .whereEqualTo("idTaller", idTaller)
                .get().await()
            Result.success(snapshot.toObjects(Resena::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva Reseña en Firestore.
     *
     * @param resena Objeto [Resena] con los datos de la nueva reseña.
     */
    suspend fun crearResena(resena: Resena): Result<Resena> {
        return try {
            val docRef = resenasCol.document()
            val resenaConId = resena.copy(idResena = docRef.id)
            docRef.set(resenaConId).await()
            Result.success(resenaConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una Reseña de Firestore.
     *
     * @param idResena ID de la reseña a eliminar.
     */
    suspend fun eliminarResena(idResena: String): Result<Unit> {
        return try {
            resenasCol.document(idResena).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}