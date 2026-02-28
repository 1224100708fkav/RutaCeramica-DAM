package com.utng.rutaceramica.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.utng.rutaceramica.data.model.Recorrido
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona las operaciones CRUD de la entidad Recorrido en Firestore.
 */
class RecorridoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val recorridosCol = db.collection("recorridos")

    /**
     * Obtiene todos los recorridos disponibles en la app.
     */
    suspend fun obtenerTodosRecorridos(): Result<List<Recorrido>> {
        return try {
            val snapshot = recorridosCol.get().await()
            Result.success(snapshot.toObjects(Recorrido::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los recorridos organizados por un taller específico.
     *
     * @param idTaller ID del taller organizador.
     */
    suspend fun obtenerRecorridosDeTaller(idTaller: String): Result<List<Recorrido>> {
        return try {
            val snapshot = recorridosCol
                .whereEqualTo("idTaller", idTaller)
                .get().await()
            Result.success(snapshot.toObjects(Recorrido::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea un nuevo Recorrido en Firestore.
     *
     * @param recorrido Datos del recorrido a crear.
     */
    suspend fun crearRecorrido(recorrido: Recorrido): Result<Recorrido> {
        return try {
            val docRef = recorridosCol.document()
            val recorridoConId = recorrido.copy(idRecorrido = docRef.id)
            docRef.set(recorridoConId).await()
            Result.success(recorridoConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un Recorrido existente.
     */
    suspend fun actualizarRecorrido(recorrido: Recorrido): Result<Unit> {
        return try {
            recorridosCol.document(recorrido.idRecorrido).set(recorrido).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un Recorrido de Firestore.
     *
     * @param idRecorrido ID del recorrido a eliminar.
     */
    suspend fun eliminarRecorrido(idRecorrido: String): Result<Unit> {
        return try {
            recorridosCol.document(idRecorrido).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}