package com.utng.rutaceramica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.utng.rutaceramica.ui.navigation.NavGraph
import com.utng.rutaceramica.ui.theme.RutaCeramicaTheme
import com.utng.rutaceramica.viewmodel.AuthViewModel

/**
 * Actividad principal y única de la aplicación.
 * Configura el tema, el controlador de navegación y el ViewModel de autenticación.
 * Verifica si existe una sesión activa al abrir la app para
 * redirigir automáticamente sin pasar por el login.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RutaCeramicaTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                NavGraph(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}