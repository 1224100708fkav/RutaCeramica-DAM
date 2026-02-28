package com.utng.rutaceramica.ui.screens.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pantalla principal del usuario con rol Dueño de Taller.
 * Muestra un panel de control con acceso a la gestión de sus talleres.
 *
 * @param idArtesano UID del dueño.
 * @param onIrATalleres Navegar a la gestión de talleres.
 * @param onLogout Cerrar sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDuenoScreen(
    idArtesano: String,
    onIrATalleres: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏺 Panel del Artesano", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C),
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
            Text(
                text = "Bienvenido, Artesano",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gestiona tus talleres, productos, recorridos y reservas.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            BotonMenu(
                icono = Icons.Default.Store,
                titulo = "Mis Talleres",
                subtitulo = "Administra tus talleres artesanales",
                color = Color(0xFF4A148C),
                onClick = onIrATalleres
            )
        }
    }
}

/**
 * Componente reutilizable para los botones de menú del panel de artesano.
 *
 * @param icono Ícono representativo de la opción.
 * @param titulo Texto principal del botón.
 * @param subtitulo Descripción breve de la opción.
 * @param color Color de acento del botón.
 * @param onClick Acción al presionar el botón.
 */
@Composable
fun BotonMenu(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitulo, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}