package com.utng.rutaceramica.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.utng.rutaceramica.data.model.Reserva
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona las operaciones CRUD de la entidad Reserva en Firestore.
 * Las reservas conectan a los turistas con los recorridos de los talleres.
 */
class ReservaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val reservasCol = db.collection("reservas")

    /**
     * Obtiene todas las reservas de un turista específico.
     *
     * @param idUsuario UID del turista.
     */
    suspend fun obtenerReservasDeUsuario(idUsuario: String): Result<List<Reserva>> {
        return try {
            val snapshot = reservasCol
                .whereEqualTo("idUsuario", idUsuario)
                .get().await()
            Result.success(snapshot.toObjects(Reserva::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las reservas de los recorridos de un taller específico.
     * Usado por el Dueño para gestionar el estatus de las reservas.
     *
     * @param idRecorrido ID del recorrido del taller.
     */
    suspend fun obtenerReservasDeRecorrido(idRecorrido: String): Result<List<Reserva>> {
        return try {
            val snapshot = reservasCol
                .whereEqualTo("idRecorrido", idRecorrido)
                .get().await()
            Result.success(snapshot.toObjects(Reserva::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva Reserva en Firestore.
     *
     * @param reserva Datos de la reserva a crear.
     */
    suspend fun crearReserva(reserva: Reserva): Result<Reserva> {
        return try {
            val docRef = reservasCol.document()
            val reservaConId = reserva.copy(idReserva = docRef.id)
            docRef.set(reservaConId).await()
            Result.success(reservaConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el estatus de una reserva (ej: "pendiente" → "confirmada").
     * Usado por el Dueño para gestionar sus reservas.
     *
     * @param idReserva ID de la reserva.
     * @param nuevoEstatus Nuevo estado: "pendiente", "confirmada" o "cancelada".
     */
    suspend fun actualizarEstatus(idReserva: String, nuevoEstatus: String): Result<Unit> {
        return try {
            reservasCol.document(idReserva)
                .update("estatus", nuevoEstatus).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancela/elimina una Reserva de Firestore.
     *
     * @param idReserva ID de la reserva a cancelar.
     */
    suspend fun cancelarReserva(idReserva: String): Result<Unit> {
        return try {
            reservasCol.document(idReserva).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /**
     * Actualiza la fecha y hora de una reserva existente.
     *
     * @param idReserva ID de la reserva a actualizar.
     * @param nuevaFecha Nueva fecha en formato dd/MM/yyyy HH:mm.
     */
    suspend fun actualizarFechaReserva(idReserva: String, nuevaFecha: String): Result<Unit> {
        return try {
            reservasCol.document(idReserva)
                .update("fechaHora", nuevaFecha).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}