package com.utng.rutaceramica.utils

/**
 * Objeto singleton que centraliza todas las constantes de la aplicación.
 * Evita el uso de "magic strings" dispersos por el código.
 */
object Constants {

    // ── Roles de usuario ───────────────────────────────────────────
    /** Rol de usuario sin privilegios especiales, solo puede explorar. */
    const val ROL_TURISTA = "turista"

    /** Rol de usuario propietario de talleres artesanales. */
    const val ROL_DUENO = "dueno"

    /** Rol de administrador del sistema con todos los privilegios. */
    const val ROL_ADMIN = "admin"

    // ── Estatus de Reservas ────────────────────────────────────────
    const val ESTATUS_PENDIENTE = "pendiente"
    const val ESTATUS_CONFIRMADA = "confirmada"
    const val ESTATUS_CANCELADA = "cancelada"

    // ── Rutas de Firebase Storage ──────────────────────────────────
    const val STORAGE_TALLERES = "talleres"
    const val STORAGE_PRODUCTOS = "productos"
    const val STORAGE_BITACORA = "bitacora"

    // ── Coordenadas de Dolores Hidalgo (centro del mapa inicial) ───
    const val LAT_DOLORES_HIDALGO = 21.1522
    const val LNG_DOLORES_HIDALGO = -100.9338

    // ── Colecciones de Firestore ───────────────────────────────────
    const val COL_USUARIOS = "usuarios"
    const val COL_TALLERES = "talleres"
    const val COL_PRODUCTOS = "productos"
    const val COL_RECORRIDOS = "recorridos"
    const val COL_RESERVAS = "reservas"
    const val COL_RESENAS = "resenas"
    const val COL_CATEGORIAS = "categorias"
    const val COL_BITACORA = "bitacora"
}