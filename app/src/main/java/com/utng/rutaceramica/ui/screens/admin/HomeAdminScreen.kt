package com.utng.rutaceramica.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utng.rutaceramica.ui.screens.dueno.BotonMenu

/**
 * Panel principal del Administrador del sistema.
 * Centraliza el acceso a: aprobación de talleres y gestión de roles de usuarios.
 *
 * @param onIrAAprobar Navegar a la pantalla de aprobación de talleres.
 * @param onIrAUsuarios Navegar a la gestión de usuarios y roles.
 * @param onLogout Cerrar sesión del administrador.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen(
    onIrAAprobar: () -> Unit,
    onIrAUsuarios: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔧 Panel Admin", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF37474F),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir",
                            tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Panel de Administración", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Gestiona talleres y usuarios de la plataforma.",
                fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            BotonMenu(
                icono = Icons.Default.CheckCircle,
                titulo = "Aprobar Talleres",
                subtitulo = "Revisar y aprobar talleres pendientes",
                color = Color(0xFF37474F),
                onClick = onIrAAprobar
            )

            BotonMenu(
                icono = Icons.Default.ManageAccounts,
                titulo = "Gestión de Usuarios",
                subtitulo = "Cambiar roles de los usuarios",
                color = Color(0xFF37474F),
                onClick = onIrAUsuarios
            )
        }
    }
}