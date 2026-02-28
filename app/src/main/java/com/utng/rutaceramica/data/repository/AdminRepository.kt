package com.utng.rutaceramica.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.utng.rutaceramica.data.model.Usuario
import kotlinx.coroutines.tasks.await

/**
 * Repositorio exclusivo del Administrador.
 * Gestiona operaciones administrativas: aprobación de talleres
 * y cambio de roles de usuarios.
 */
class AdminRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosCol = db.collection("usuarios")

    /**
     * Obtiene la lista completa de todos los usuarios registrados en la app.
     * Solo accesible para el rol "admin".
     *
     * @return [Result] con la lista de todos los [Usuario].
     */
    suspend fun obtenerTodosUsuarios(): Result<List<Usuario>> {
        return try {
            val snapshot = usuariosCol.get().await()
            Result.success(snapshot.toObjects(Usuario::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cambia el rol de un usuario en Firestore.
     * Permite al administrador promover o degradar usuarios.
     *
     * @param idUsuario UID del usuario a modificar.
     * @param nuevoRol Nuevo rol a asignar: "turista", "dueno" o "admin".
     * @return [Result] indicando éxito o excepción.
     */
    suspend fun cambiarRolUsuario(idUsuario: String, nuevoRol: String): Result<Unit> {
        return try {
            usuariosCol.document(idUsuario)
                .update("rol", nuevoRol).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}