package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa un Taller de artesanía.
 * Corresponde a la entidad "Taller" del Modelo Entidad-Relación.
 * Un taller es gestionado por un Usuario con rol "dueño".
 *
 * @property idTaller Identificador único del documento en Firestore.
 * @property nombre Nombre del taller artesanal.
 * @property descripcion Descripción general del taller y sus actividades.
 * @property historia Historia o reseña del taller y sus tradiciones.
 * @property direccion Dirección física del taller en Dolores Hidalgo.
 * @property latitud Coordenada geográfica de latitud para Google Maps.
 * @property longitud Coordenada geográfica de longitud para Google Maps.
 * @property telefono Número de teléfono de contacto del taller.
 * @property idArtesano UID del usuario dueño que gestiona este taller.
 * @property fotoUrl URL de la foto principal del taller en Firebase Storage.
 * @property aprobado Indica si el taller ha sido aprobado por el Administrador.
 * @property categorias Lista de IDs de categorías a las que pertenece el taller.
 */
data class Taller(
    val idTaller: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val historia: String = "",
    val direccion: String = "",
    val latitud: Double = 21.1522,
    val longitud: Double = -100.9338,
    val telefono: String = "",
    val idArtesano: String = "",
    val fotoUrl: String = "",
    val aprobado: Boolean = false,
    val categorias: List<String> = emptyList()
)