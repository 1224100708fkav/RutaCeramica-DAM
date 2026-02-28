package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa una Reseña escrita por un Usuario sobre un Taller.
 * Corresponde a la entidad "Reseña" del Modelo Entidad-Relación.
 *
 * @property idResena Identificador único del documento en Firestore.
 * @property puntuacion Calificación del taller del 1 al 5 estrellas.
 * @property comentario Texto del comentario o reseña escrita por el usuario.
 * @property idUsuario UID del usuario que escribió la reseña.
 * @property idTaller ID del taller que está siendo reseñado.
 * @property nombreUsuario Nombre del autor de la reseña (para mostrar en UI).
 * @property fecha Fecha en que se escribió la reseña.
 */
data class Resena(
    val idResena: String = "",
    val puntuacion: Float = 5f,
    val comentario: String = "",
    val idUsuario: String = "",
    val idTaller: String = "",
    val nombreUsuario: String = "",
    val fecha: String = ""
)