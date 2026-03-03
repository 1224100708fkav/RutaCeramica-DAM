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

    /**
     * Crea un nuevo usuario en Firestore.
     * Nota: El administrador crea el documento en Firestore, la autenticación
     * se maneja por separado o el usuario deberá registrarse.
     */
    suspend fun crearUsuario(usuario: Usuario): Result<Usuario> {
        return try {
            val docRef = if (usuario.idUsuario.isEmpty()) usuariosCol.document() else usuariosCol.document(usuario.idUsuario)
            val usuarioConId = usuario.copy(idUsuario = docRef.id)
            docRef.set(usuarioConId).await()
            Result.success(usuarioConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    suspend fun actualizarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            usuariosCol.document(usuario.idUsuario).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un usuario de Firestore.
     */
    suspend fun eliminarUsuario(idUsuario: String): Result<Unit> {
        return try {
            usuariosCol.document(idUsuario).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}