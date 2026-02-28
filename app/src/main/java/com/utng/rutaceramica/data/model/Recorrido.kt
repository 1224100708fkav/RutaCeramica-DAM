package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa un Recorrido turístico organizado por un Taller.
 * Corresponde a la entidad "Recorrido" del Modelo Entidad-Relación.
 *
 * @property idRecorrido Identificador único del documento en Firestore.
 * @property nombre Nombre descriptivo del recorrido turístico.
 * @property descripcion Descripción detallada de la experiencia del recorrido.
 * @property duracion Duración estimada del recorrido (ej: "2 horas").
 * @property precio Costo por persona del recorrido en pesos mexicanos.
 * @property capacidadMaxima Número máximo de participantes permitidos.
 * @property idTaller ID del taller que organiza y ofrece este recorrido.
 */
data class Recorrido(
    val idRecorrido: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val duracion: String = "",
    val precio: Double = 0.0,
    val capacidadMaxima: Int = 0,
    val idTaller: String = ""
)