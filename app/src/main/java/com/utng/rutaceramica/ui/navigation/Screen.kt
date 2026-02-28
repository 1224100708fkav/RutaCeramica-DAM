package com.utng.rutaceramica.ui.navigation

/**
 * Clase sellada que define todas las rutas de navegación de la aplicación.
 * Cada objeto representa una pantalla con su ruta única en el NavGraph.
 * El uso de sealed class garantiza que solo existan las rutas definidas aquí.
 *
 * @property ruta Cadena de texto única que identifica la pantalla en la navegación.
 */
sealed class Screen(val ruta: String) {
    // ── Pantallas de Inicio y Autenticación ───────────────────────
    /** Pantalla de carga animada. */
    object Splash : Screen("splash")
    /** Pantalla de inicio de sesión. */
    object Login : Screen("login")
    /** Pantalla de registro de nuevo usuario. */
    object Registro : Screen("registro")

    // ── Pantallas del Turista ─────────────────────────────────────
    /** Pantalla principal del turista con lista de talleres. */
    object HomeTurista : Screen("home_turista")
    /** Mapa interactivo con talleres geolocalizados. */
    /** Mapa interactivo con talleres geolocalizados. */
    object Mapa : Screen("mapa/{idTaller}") {
        fun conId(idTaller: String = "") = "mapa/$idTaller"
    }
    /** Detalle completo de un taller específico. */
    object DetalleTaller : Screen("detalle_taller/{idTaller}") {
        /** Genera la ruta con el ID del taller incluido. */
        fun conId(idTaller: String) = "detalle_taller/$idTaller"
    }
    /** Lista de recorridos disponibles. */
    object Recorridos : Screen("recorridos")
    /** Lista de reservas del turista. */
    object MisReservas : Screen("mis_reservas")
    /** Bitácora personal de recuerdos del turista. */
    object Bitacora : Screen("bitacora")
    /** Pantalla para agregar entrada a la bitácora con cámara. */
    object NuevaBitacora : Screen("nueva_bitacora")
    /** Pantalla del carrito de apartados del turista. */
    object Carrito : Screen("carrito")
    /** Pantalla del perfil del usuario Turista. */
    object Perfil : Screen("perfil")

    // ── Pantallas del Dueño ───────────────────────────────────────
    /** Panel principal del dueño de taller. */
    object HomeDueno : Screen("home_dueno")
    /** CRUD de talleres del dueño. */
    object MisTalleres : Screen("mis_talleres")
    /** Formulario para crear/editar taller. */
    object FormTaller : Screen("form_taller/{idTaller}") {
        fun conId(idTaller: String = "nuevo") = "form_taller/$idTaller"
    }
    /** CRUD de productos del taller. */
    object MisProductos : Screen("mis_productos/{idTaller}") {
        fun conId(idTaller: String) = "mis_productos/$idTaller"
    }
    /** CRUD de recorridos del taller. */
    object MisRecorridos : Screen("mis_recorridos/{idTaller}") {
        fun conId(idTaller: String) = "mis_recorridos/$idTaller"
    }
    /** Gestión de reservas del taller. */
    object GestionReservas : Screen("gestion_reservas/{idTaller}") {
        fun conId(idTaller: String) = "gestion_reservas/$idTaller"
    }

    // ── Pantallas del Admin ───────────────────────────────────────
    /** Panel principal del administrador. */
    object HomeAdmin : Screen("home_admin")
    /** Lista de talleres pendientes de aprobación. */
    object AprobarTalleres : Screen("aprobar_talleres")
    /** Gestión de roles de usuarios. */
    object GestionUsuarios : Screen("gestion_usuarios")
}