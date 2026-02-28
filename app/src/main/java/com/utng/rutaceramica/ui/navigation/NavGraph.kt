package com.utng.rutaceramica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.utng.rutaceramica.ui.screens.admin.AprobarTalleresScreen
import com.utng.rutaceramica.ui.screens.admin.GestionUsuariosScreen
import com.utng.rutaceramica.ui.screens.admin.HomeAdminScreen
import com.utng.rutaceramica.ui.screens.auth.LoginScreen
import com.utng.rutaceramica.ui.screens.auth.RegistroScreen
import com.utng.rutaceramica.ui.screens.auth.SplashScreen
import com.utng.rutaceramica.ui.screens.dueno.FormTallerScreen
import com.utng.rutaceramica.ui.screens.dueno.GestionReservasScreen
import com.utng.rutaceramica.ui.screens.dueno.HomeDuenoScreen
import com.utng.rutaceramica.ui.screens.dueno.MisProductosScreen
import com.utng.rutaceramica.ui.screens.dueno.MisRecorridosScreen
import com.utng.rutaceramica.ui.screens.dueno.MisTalleresScreen
import com.utng.rutaceramica.ui.screens.turista.BitacoraScreen
import com.utng.rutaceramica.ui.screens.turista.CarritoScreen
import com.utng.rutaceramica.ui.screens.turista.DetalleTallerScreen
import com.utng.rutaceramica.ui.screens.turista.HomeTuristaScreen
import com.utng.rutaceramica.ui.screens.turista.MapaScreen
import com.utng.rutaceramica.ui.screens.turista.MisReservasScreen
import com.utng.rutaceramica.ui.screens.turista.NuevaBitacoraScreen
import com.utng.rutaceramica.ui.screens.turista.PerfilScreen
import com.utng.rutaceramica.ui.screens.turista.RecorridosScreen
import com.utng.rutaceramica.utils.Constants
import com.utng.rutaceramica.viewmodel.AuthViewModel
import com.utng.rutaceramica.viewmodel.CarritoViewModel
import com.utng.rutaceramica.viewmodel.PerfilViewModel

/**
 * Grafo de navegación principal de la aplicación.
 * Define todas las rutas disponibles y maneja la redirección automática
 * según el rol del usuario obtenido desde Firestore.
 *
 * @param navController Controlador de navegación de Compose.
 * @param authViewModel ViewModel compartido de autenticación.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val usuario by authViewModel.usuario.collectAsState()
    val carritoViewModel: CarritoViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.ruta
    ) {
        composable(Screen.Splash.ruta) {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigation = { destino ->
                    navController.navigate(destino) {
                        popUpTo(Screen.Splash.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.ruta) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginExitoso = { rol ->
                    val destino = when (rol) {
                        "turista" -> Screen.HomeTurista.ruta
                        "dueno" -> Screen.HomeDueno.ruta
                        "admin" -> Screen.HomeAdmin.ruta
                        else -> Screen.HomeTurista.ruta
                    }
                    navController.navigate(destino) {
                        popUpTo(Screen.Login.ruta) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Screen.Registro.ruta) }
            )
        }

        composable(Screen.Registro.ruta) {
            RegistroScreen(
                authViewModel = authViewModel,
                onRegistroExitoso = {
                    navController.navigate(Screen.HomeTurista.ruta) {
                        popUpTo(Screen.Login.ruta) { inclusive = true }
                    }
                },
                onIrALogin = { navController.popBackStack() }
            )
        }

        // ── TURISTA ───────────────────────────────────────────────
        composable(Screen.HomeTurista.ruta) {
            HomeTuristaScreen(
                onIrAMapa = { navController.navigate(Screen.Mapa.conId()) },
                onIrARecorridos = { navController.navigate(Screen.Recorridos.ruta) },
                onIrABitacora = { navController.navigate(Screen.Bitacora.ruta) },
                onIrAReservas = { navController.navigate(Screen.MisReservas.ruta) },
                onIrALCarrito = { navController.navigate(Screen.Carrito.ruta) },
                onIrAPerfil = { navController.navigate(Screen.Perfil.ruta) },
                onVerTaller = { id -> navController.navigate(Screen.DetalleTaller.conId(id)) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Mapa.ruta) { backStack ->
            val idTaller = backStack.arguments?.getString("idTaller") ?: ""
            MapaScreen(
                idTallerInicial = idTaller,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DetalleTaller.ruta) { backStack ->
            val idTaller = backStack.arguments?.getString("idTaller") ?: ""
            DetalleTallerScreen(
                idTaller = idTaller,
                idUsuario = usuario?.idUsuario ?: "",
                nombreUsuario = usuario?.nombre ?: "",
                carritoViewModel = carritoViewModel,
                onBack = { navController.popBackStack() },
                onComoLlegar = { id -> navController.navigate(Screen.Mapa.conId(id)) },
                onIrAlCarrito = { navController.navigate(Screen.Carrito.ruta) }
            )
        }

        composable(Screen.Carrito.ruta) {
            CarritoScreen(
                viewModel = carritoViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Recorridos.ruta) {
            RecorridosScreen(
                idUsuario = usuario?.idUsuario ?: "",
                nombreUsuario = usuario?.nombre ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MisReservas.ruta) {
            MisReservasScreen(
                idUsuario = usuario?.idUsuario ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Bitacora.ruta) {
            BitacoraScreen(
                idUsuario = usuario?.idUsuario ?: "",
                onNuevaEntrada = { navController.navigate(Screen.NuevaBitacora.ruta) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.NuevaBitacora.ruta) {
            NuevaBitacoraScreen(
                idUsuario = usuario?.idUsuario ?: "",
                onGuardar = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Perfil.ruta) {
            PerfilScreen(
                uid = usuario?.idUsuario ?: "",
                onBack = { navController.popBackStack() },
                viewModel = perfilViewModel
            )
        }

        // ── DUEÑO ─────────────────────────────────────────────────
        composable(Screen.HomeDueno.ruta) {
            HomeDuenoScreen(
                idArtesano = usuario?.idUsuario ?: "",
                onIrATalleres = { navController.navigate(Screen.MisTalleres.ruta) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MisTalleres.ruta) {
            MisTalleresScreen(
                idArtesano = usuario?.idUsuario ?: "",
                onCrearTaller = { navController.navigate(Screen.FormTaller.conId()) },
                onEditarTaller = { id -> navController.navigate(Screen.FormTaller.conId(id)) },
                onProductos = { id -> navController.navigate(Screen.MisProductos.conId(id)) },
                onRecorridos = { id -> navController.navigate(Screen.MisRecorridos.conId(id)) },
                onReservas = { id -> navController.navigate(Screen.GestionReservas.conId(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FormTaller.ruta) { backStack ->
            val idTaller = backStack.arguments?.getString("idTaller") ?: "nuevo"
            FormTallerScreen(
                idTaller = idTaller,
                idArtesano = usuario?.idUsuario ?: "",
                onGuardar = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MisProductos.ruta) { backStack ->
            val idTaller = backStack.arguments?.getString("idTaller") ?: ""
            MisProductosScreen(
                idTaller = idTaller,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MisRecorridos.ruta) { backStack ->
            val idTaller = backStack.arguments?.getString("idTaller") ?: ""
            MisRecorridosScreen(
                idTaller = idTaller,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.GestionReservas.ruta) { backStack ->
            val idTaller = backStack.arguments?.getString("idTaller") ?: ""
            GestionReservasScreen(
                idTaller = idTaller,
                onBack = { navController.popBackStack() }
            )
        }

        // ── ADMIN ─────────────────────────────────────────────────
        composable(Screen.HomeAdmin.ruta) {
            HomeAdminScreen(
                onIrAAprobar = { navController.navigate(Screen.AprobarTalleres.ruta) },
                onIrAUsuarios = { navController.navigate(Screen.GestionUsuarios.ruta) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AprobarTalleres.ruta) {
            AprobarTalleresScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.GestionUsuarios.ruta) {
            GestionUsuariosScreen(onBack = { navController.popBackStack() })
        }
    }
}