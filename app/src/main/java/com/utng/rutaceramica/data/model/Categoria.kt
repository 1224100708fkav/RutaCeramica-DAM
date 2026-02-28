package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa una Categoría de taller artesanal.
 * Corresponde a la entidad "Categoría" del Modelo Entidad-Relación.
 * Un Taller puede pertenecer a múltiples categorías (relación N:M).
 *
 * @property idCategoria Identificador único del documento en Firestore.
 * @property nombre Nombre de la categoría artesanal (ej: "Talavera", "Barro Negro").
 */
data class Categoria(
    val idCategoria: String = "",
    val nombre: String = ""
)