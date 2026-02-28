package com.utng.rutaceramica.data.model

/**
 * Modelo de datos que representa a un Usuario de la aplicación.
 * Corresponde a la entidad "Usuario" del Modelo Entidad-Relación.
 *
 * @property idUsuario Identificador único generado por Firebase Auth (UID).
 * @property nombre Nombre completo del usuario.
 * @property email Correo electrónico usado para autenticación.
 * @property rol Rol del usuario en el sistema: "turista", "dueno" o "admin".
 * @property fotoUrl URL de la foto de perfil almacenada en Firebase Storage.
 * @property origen Lugar de origen del usuario.
 * @property descripcion Pequeña descripción o biografía del usuario.
 */
data class Usuario(
    val idUsuario: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "turista",
    val fotoUrl: String = "",
    val origen: String = "",
    val descripcion: String = ""
)