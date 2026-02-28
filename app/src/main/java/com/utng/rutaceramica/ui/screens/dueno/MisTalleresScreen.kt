package com.utng.rutaceramica.ui.screens.dueno

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
import com.utng.rutaceramica.data.model.Taller
import com.utng.rutaceramica.viewmodel.TallerViewModel

/**
 * Pantalla de gestión de Talleres para el usuario con rol Dueño.
 * Muestra la lista de sus talleres con opciones de: editar, gestionar
 * productos, recorridos y reservas. Permite crear nuevos talleres.
 *
 * @param idArtesano UID del dueño de los talleres.
 * @param onCrearTaller Navegar al formulario de nuevo taller.
 * @param onEditarTaller Navegar al formulario de edición con el ID del taller.
 * @param onProductos Navegar a los productos de un taller.
 * @param onRecorridos Navegar a los recorridos de un taller.
 * @param onReservas Navegar a las reservas de un taller.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisTalleresScreen(
    idArtesano: String,
    onCrearTaller: () -> Unit,
    onEditarTaller: (String) -> Unit,
    onProductos: (String) -> Unit,
    onRecorridos: (String) -> Unit,
    onReservas: (String) -> Unit,
    onBack: () -> Unit,
    tallerViewModel: TallerViewModel = viewModel()
) {
    val talleres by tallerViewModel.talleres.collectAsState()
    val cargando by tallerViewModel.cargando.collectAsState()
    val mensaje by tallerViewModel.mensaje.collectAsState()

    LaunchedEffect(idArtesano) {
        tallerViewModel.cargarTalleresDeArtesano(idArtesano)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Talleres", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearTaller,
                containerColor = Color(0xFF4A148C)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo taller", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            mensaje?.let {
                Snackbar(modifier = Modifier.padding(16.dp)) { Text(it) }
            }

            if (cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(talleres) { taller ->
                        TarjetaTallerDueno(
                            taller = taller,
                            onEditar = { onEditarTaller(taller.idTaller) },
                            onProductos = { onProductos(taller.idTaller) },
                            onRecorridos = { onRecorridos(taller.idTaller) },
                            onReservas = { onReservas(taller.idTaller) },
                            onEliminar = {
                                tallerViewModel.eliminarTaller(taller.idTaller, idArtesano)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de taller con controles de gestión para el Dueño.
 * Muestra estado de aprobación y botones de acción.
 */
@Composable
fun TarjetaTallerDueno(
    taller: Taller,
    onEditar: () -> Unit,
    onProductos: () -> Unit,
    onRecorridos: () -> Unit,
    onReservas: () -> Unit,
    onEliminar: () -> Unit
) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(taller.nombre, fontWeight = FontWeight.Bold, fontSize = 17.sp,
                    modifier = Modifier.weight(1f))
                // Badge de estado
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (taller.aprobado) Color(0xFF1B5E20).copy(0.1f)
                    else Color(0xFFF57F17).copy(0.1f)
                ) {
                    Text(
                        text = if (taller.aprobado) "✓ Aprobado" else "⏳ Pendiente",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (taller.aprobado) Color(0xFF1B5E20) else Color(0xFFF57F17),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(taller.descripcion, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonesAccion("Editar", Icons.Default.Edit, Color(0xFF1A237E), onEditar)
                BotonesAccion("Productos", Icons.Default.Inventory, Color(0xFF1B5E20), onProductos)
                BotonesAccion("Recorridos", Icons.Default.Tour, Color(0xFF4A148C), onRecorridos)
                BotonesAccion("Reservas", Icons.Default.CalendarToday, Color(0xFF006064), onReservas)
                BotonesAccion("Borrar", Icons.Default.Delete, Color.Red) {
                    confirmarEliminar = true
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar taller?") },
            text = { Text("Se eliminará permanentemente '${taller.nombre}'.") },
            confirmButton = {
                TextButton(onClick = { confirmarEliminar = false; onEliminar() }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

/**
 * Botón de acción pequeño con ícono y etiqueta para las tarjetas del Dueño.
 */
@Composable
fun BotonesAccion(etiqueta: String, icono: androidx.compose.ui.graphics.vector.ImageVector,
                  color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
            Icon(icono, contentDescription = etiqueta, tint = color,
                modifier = Modifier.size(20.dp))
        }
        Text(etiqueta, fontSize = 9.sp, color = color)
    }
}