package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa una entrada de la Bitácora personal del Turista.
 * La Bitácora es un diario de viaje donde el turista guarda sus recuerdos
 * con fotos tomadas con la cámara del dispositivo y comentarios personales.
 *
 * @property idBitacora Identificador único del documento en Firestore.
 * @property titulo Título breve que el turista le da a la entrada.
 * @property comentario Texto descriptivo del recuerdo o experiencia vivida.
 * @property fotoUrl URL de la foto subida a Firebase Storage desde la cámara.
 * @property idUsuario UID del turista propietario de esta entrada.
 * @property idTaller ID del taller relacionado con esta entrada (opcional).
 * @property fecha Fecha y hora en que se creó la entrada.
 */
data class Bitacora(
    val idBitacora: String = "",
    val titulo: String = "",
    val comentario: String = "",
    val fotoUrl: String = "",
    val idUsuario: String = "",
    val idTaller: String = "",
    val fecha: String = ""
)