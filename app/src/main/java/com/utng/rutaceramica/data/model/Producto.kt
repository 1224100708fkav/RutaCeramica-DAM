package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa un Producto artesanal ofrecido por un Taller.
 * Corresponde a la entidad "Producto" del Modelo Entidad-Relación.
 *
 * @property idProducto Identificador único del documento en Firestore.
 * @property nombre Nombre del producto artesanal.
 * @property descripcion Descripción detallada del producto.
 * @property precio Precio de venta del producto en pesos mexicanos.
 * @property material Material principal con el que está elaborado (ej: barro, cerámica).
 * @property tecnica Técnica artesanal utilizada en su fabricación.
 * @property stock Cantidad de unidades disponibles en inventario.
 * @property idTaller ID del taller al que pertenece este producto.
 * @property fotoUrl URL de la foto del producto almacenada en Firebase Storage.
 */
data class Producto(
    val idProducto: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val material: String = "",
    val tecnica: String = "",
    val stock: Int = 0,
    val idTaller: String = "",
    val fotoUrl: String = ""
)