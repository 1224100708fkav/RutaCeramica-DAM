package com.utng.rutaceramica.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.utng.rutaceramica.data.model.Producto
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona las operaciones CRUD de la entidad Producto en Firestore.
 * También maneja la subida de fotos de productos a Firebase Storage.
 */
class ProductoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val productosCol = db.collection("productos")

    /**
     * Obtiene todos los productos de un taller específico.
     *
     * @param idTaller ID del taller del cual se quieren obtener los productos.
     * @return [Result] con lista de [Producto] del taller.
     */
    suspend fun obtenerProductosDeTaller(idTaller: String): Result<List<Producto>> {
        return try {
            val snapshot = productosCol
                .whereEqualTo("idTaller", idTaller)
                .get().await()
            Result.success(snapshot.toObjects(Producto::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea un nuevo Producto en Firestore con ID generado automáticamente.
     *
     * @param producto Objeto [Producto] con los datos del nuevo producto.
     * @return [Result] con el [Producto] creado (con ID asignado).
     */
    suspend fun crearProducto(producto: Producto): Result<Producto> {
        return try {
            val docRef = productosCol.document()
            val productoConId = producto.copy(idProducto = docRef.id)
            docRef.set(productoConId).await()
            Result.success(productoConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos de un Producto existente en Firestore.
     *
     * @param producto Objeto [Producto] con datos actualizados.
     */
    suspend fun actualizarProducto(producto: Producto): Result<Unit> {
        return try {
            productosCol.document(producto.idProducto).set(producto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un Producto de Firestore por su ID.
     *
     * @param idProducto ID del producto a eliminar.
     */
    suspend fun eliminarProducto(idProducto: String): Result<Unit> {
        return try {
            productosCol.document(idProducto).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sube una foto de producto a Firebase Storage.
     * La ruta en Storage será: "productos/{idProducto}/foto.jpg"
     *
     * @param idProducto ID del producto asociado a la foto.
     * @param imagenUri URI local de la imagen.
     * @return [Result] con la URL pública de la foto subida.
     */
    suspend fun subirFotoProducto(idProducto: String, imagenUri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child("productos/$idProducto/foto.jpg")
            ref.putFile(imagenUri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}