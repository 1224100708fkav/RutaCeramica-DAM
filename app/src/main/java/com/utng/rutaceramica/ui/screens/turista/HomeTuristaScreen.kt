package com.utng.rutaceramica.ui.screens.turista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.utng.rutaceramica.data.model.Taller
import com.utng.rutaceramica.viewmodel.TallerViewModel

/**
 * Pantalla principal del Turista. Muestra la lista de talleres aprobados
 * con tarjetas informativas y acceso a las funcionalidades principales.
 *
 * @param onIrAMapa Navegar al mapa de talleres.
 * @param onIrARecorridos Navegar a la lista de recorridos.
 * @param onIrABitacora Navegar a la bitácora personal.
 * @param onIrAReservas Navegar a las reservas del turista.
 * @param onIrALCarrito Navegar al carrito de apartados.
 * @param onIrAPerfil Navegar al perfil del usuario.
 * @param onVerTaller Navegar al detalle de un taller específico.
 * @param onLogout Cerrar sesión y volver al login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTuristaScreen(
    onIrAMapa: () -> Unit,
    onIrARecorridos: () -> Unit,
    onIrABitacora: () -> Unit,
    onIrAReservas: () -> Unit,
    onIrALCarrito: () -> Unit,
    onIrAPerfil: () -> Unit,
    onVerTaller: (String) -> Unit,
    onLogout: () -> Unit,
    tallerViewModel: TallerViewModel = viewModel()
) {
    val talleres by tallerViewModel.talleres.collectAsState()
    val cargando by tallerViewModel.cargando.collectAsState()

    LaunchedEffect(Unit) {
        tallerViewModel.cargarTalleresAprobados()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏺 Talleres Artesanales", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onIrALCarrito) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Ver Carrito",
                            tint = Color.White)
                    }
                    IconButton(onClick = onIrAPerfil) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil",
                            tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión",
                            tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    label = { Text("Mapa") },
                    selected = false,
                    onClick = onIrAMapa
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Tour, contentDescription = null) },
                    label = { Text("Recorridos") },
                    selected = false,
                    onClick = onIrARecorridos
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Book, contentDescription = null) },
                    label = { Text("Bitácora") },
                    selected = false,
                    onClick = onIrABitacora
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    label = { Text("Reservas") },
                    selected = false,
                    onClick = onIrAReservas
                )
            }
        }
    ) { padding ->
        if (cargando) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(talleres) { taller ->
                    TarjetaTaller(taller = taller, onClick = { onVerTaller(taller.idTaller) })
                }
            }
        }
    }
}

/**
 * Componente de tarjeta reutilizable para mostrar información resumida de un Taller.
 *
 * @param taller Datos del taller a mostrar.
 * @param onClick Acción al hacer clic en la tarjeta.
 */
@Composable
fun TarjetaTaller(taller: Taller, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (taller.fotoUrl.isNotBlank()) {
                AsyncImage(
                    model = taller.fotoUrl,
                    contentDescription = "Foto de ${taller.nombre}",
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(taller.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(taller.descripcion, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = Color(0xFF1A237E), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(taller.direccion, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}