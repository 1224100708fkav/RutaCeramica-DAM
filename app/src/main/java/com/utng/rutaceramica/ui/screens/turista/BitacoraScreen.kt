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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.utng.rutaceramica.data.model.Bitacora
import com.utng.rutaceramica.viewmodel.BitacoraViewModel

/**
 * Pantalla principal de la Bitácora personal del Turista.
 * Muestra la lista de recuerdos guardados con foto y comentario.
 * Permite eliminar entradas y navegar para crear nuevas.
 *
 * @param idUsuario UID del turista propietario de la bitácora.
 * @param onNuevaEntrada Navegar a la pantalla de nueva entrada.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitacoraScreen(
    idUsuario: String,
    onNuevaEntrada: () -> Unit,
    onBack: () -> Unit,
    bitacoraViewModel: BitacoraViewModel = viewModel()
) {
    val entradas by bitacoraViewModel.entradas.collectAsState()
    val cargando by bitacoraViewModel.cargando.collectAsState()
    val mensaje by bitacoraViewModel.mensaje.collectAsState()

    LaunchedEffect(idUsuario) {
        bitacoraViewModel.cargarBitacora(idUsuario)
    }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(2000)
            bitacoraViewModel.limpiarMensajes()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📖 Mi Bitácora", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaEntrada,
                containerColor = Color(0xFF1A237E)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva entrada", tint = Color.White)
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
            } else if (entradas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tu bitácora está vacía", fontSize = 18.sp, color = Color.Gray)
                        Text("Toca + para agregar tu primer recuerdo",
                            fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entradas) { entrada ->
                        TarjetaBitacora(
                            entrada = entrada,
                            onEliminar = {
                                bitacoraViewModel.eliminarEntrada(entrada.idBitacora, idUsuario)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente de tarjeta para mostrar una entrada de la Bitácora.
 *
 * @param entrada Datos de la entrada de bitácora.
 * @param onEliminar Callback para eliminar esta entrada.
 */
@Composable
fun TarjetaBitacora(entrada: Bitacora, onEliminar: () -> Unit) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            if (entrada.fotoUrl.isNotBlank()) {
                AsyncImage(
                    model = entrada.fotoUrl,
                    contentDescription = "Foto del recuerdo",
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(entrada.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = { confirmarEliminar = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                            tint = Color.Red)
                    }
                }
                Text(entrada.comentario, fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(entrada.fecha, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar recuerdo?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmarEliminar = false; onEliminar() }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}