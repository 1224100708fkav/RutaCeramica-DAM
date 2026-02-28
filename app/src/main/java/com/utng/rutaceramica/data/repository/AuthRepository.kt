package com.utng.rutaceramica.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.utng.rutaceramica.data.model.Usuario
import kotlinx.coroutines.tasks.await
import android.net.Uri

/**
 * Repositorio que gestiona toda la autenticación con Firebase Auth
 * y el manejo de datos de usuario en Firestore.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val usuariosCol = db.collection("usuarios")

    fun getUsuarioActual(): FirebaseUser? = auth.currentUser

    /**
     * Registra un nuevo usuario y lo guarda en Firestore.
     */
    suspend fun registrar(nombre: String, email: String, password: String): Result<Usuario> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = resultado.user!!.uid
            val nuevoUsuario = Usuario(
                idUsuario = uid,
                nombre = nombre,
                email = email,
                rol = "turista"
            )
            usuariosCol.document(uid).set(nuevoUsuario).await()
            Result.success(nuevoUsuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión y obtiene el rol del usuario desde Firestore.
     */
    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            // Paso 1: Autenticar con Firebase Auth
            val resultado = auth.signInWithEmailAndPassword(email, password).await()
            val uid = resultado.user!!.uid

            android.util.Log.d("AUTH_DEBUG", "UID obtenido: $uid")

            // Paso 2: Buscar documento en Firestore
            val doc = usuariosCol.document(uid).get().await()

            android.util.Log.d("AUTH_DEBUG", "Documento existe: ${doc.exists()}")
            android.util.Log.d("AUTH_DEBUG", "Datos: ${doc.data}")

            if (doc.exists()) {
                val usuario = doc.toObject(Usuario::class.java)!!
                Result.success(usuario)
            } else {
                // Si no existe el documento, crear uno como turista
                android.util.Log.d("AUTH_DEBUG", "Documento NO existe, creando turista")
                val usuarioNuevo = Usuario(
                    idUsuario = uid,
                    nombre = email.substringBefore("@"),
                    email = email,
                    rol = "turista"
                )
                usuariosCol.document(uid).set(usuarioNuevo).await()
                Result.success(usuarioNuevo)
            }
        } catch (e: Exception) {
            android.util.Log.e("AUTH_DEBUG", "Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión actual.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Obtiene el perfil del usuario desde Firestore.
     */
    suspend fun obtenerPerfil(uid: String): Result<Usuario> {
        return try {
            val doc = usuariosCol.document(uid).get().await()
            if (doc.exists()) {
                val usuario = doc.toObject(Usuario::class.java)!!
                Result.success(usuario)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos del perfil de un usuario en Firestore.
     * Si se proporciona una URI de imagen, la sube a Firebase Storage primero.
     */
    suspend fun actualizarPerfil(usuario: Usuario, nuevaFotoUri: Uri? = null): Result<Usuario> {
        return try {
            var usuarioActualizado = usuario

            // 1. Subir foto si hay una nueva
            nuevaFotoUri?.let { uri ->
                val storageRef = storage.reference.child("perfiles/${usuario.idUsuario}.jpg")
                
                // Subir el archivo y esperar a que la tarea se complete
                val uploadTask = storageRef.putFile(uri).await()
                
                // Obtener la URL directamente desde la referencia que acaba de terminar la subida
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
                
                usuarioActualizado = usuarioActualizado.copy(fotoUrl = downloadUrl)
            }

            // 2. Guardar en Firestore (usamos set con merge para no borrar campos accidentales)
            usuariosCol.document(usuario.idUsuario).set(usuarioActualizado).await()
            Result.success(usuarioActualizado)
        } catch (e: Exception) {
            android.util.Log.e("AUTH_DEBUG", "Error en actualizarPerfil: ${e.message}")
            Result.failure(e)
        }
    }
}