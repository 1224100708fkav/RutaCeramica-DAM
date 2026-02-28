package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa una Reserva realizada por un Turista.
 * Corresponde a la entidad "Reserva" del Modelo Entidad-Relación.
 * Un turista realiza (N:1) reservas y cada reserva pertenece a un Recorrido.
 *
 * @property idReserva Identificador único del documento en Firestore.
 * @property fechaHora Fecha y hora de la reserva en formato ISO 8601 (String).
 * @property estatus Estado actual de la reserva: "pendiente", "confirmada" o "cancelada".
 * @property idUsuario UID del turista que realizó la reserva.
 * @property idRecorrido ID del recorrido que fue reservado.
 * @property nombreUsuario Nombre del turista (para visualización rápida).
 * @property nombreRecorrido Nombre del recorrido (para visualización rápida).
 */
data class Reserva(
    val idReserva: String = "",
    val fechaHora: String = "",
    val estatus: String = "pendiente",
    val idUsuario: String = "",
    val idRecorrido: String = "",
    val nombreUsuario: String = "",
    val nombreRecorrido: String = ""
)