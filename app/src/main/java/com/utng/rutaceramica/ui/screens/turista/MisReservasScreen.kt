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
import com.utng.rutaceramica.data.model.Reserva
import com.utng.rutaceramica.data.repository.ReservaRepository
import kotlinx.coroutines.launch

/**
 * Pantalla de Mis Reservas con CRUD completo para el Turista.
 * Muestra todas las reservas con su estatus y permite cancelarlas.
 *
 * @param idUsuario UID del turista.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    idUsuario: String,
    onBack: () -> Unit
) {
    val repository = remember { ReservaRepository() }
    val scope = rememberCoroutineScope()
    var reservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var reservaAEditar by remember { mutableStateOf<Reserva?>(null) }
    var nuevaFecha by remember { mutableStateOf("") }

    fun cargar() {
        scope.launch {
            cargando = true
            repository.obtenerReservasDeUsuario(idUsuario)
                .onSuccess { reservas = it }
            cargando = false
        }
    }

    LaunchedEffect(idUsuario) { cargar() }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(2500)
            mensaje = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📅 Mis Reservas", fontWeight = FontWeight.Bold) },
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
            } else if (reservas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No tienes reservas aún", color = Color.Gray, fontSize = 16.sp)
                        Text("Ve a Recorridos para hacer una reserva",
                            color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reservas) { reserva ->
                        val colorEstatus = when (reserva.estatus) {
                            "confirmada" -> Color(0xFF1B5E20)
                            "cancelada" -> Color.Red
                            else -> Color(0xFFF57F17)
                        }
                        val iconoEstatus = when (reserva.estatus) {
                            "confirmada" -> "✅"
                            "cancelada" -> "❌"
                            else -> "⏳"
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reserva.nombreRecorrido,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "📅 ${reserva.fechaHora}",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = colorEstatus.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = "$iconoEstatus ${reserva.estatus.uppercase()}",
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp, vertical = 4.dp),
                                            color = colorEstatus,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Botones CRUD solo si está pendiente
                                if (reserva.estatus == "pendiente") {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Editar fecha
                                        OutlinedButton(
                                            onClick = {
                                                reservaAEditar = reserva
                                                nuevaFecha = reserva.fechaHora
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Cambiar fecha", fontSize = 12.sp)
                                        }

                                        // Cancelar reserva
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    repository.actualizarEstatus(
                                                        reserva.idReserva, "cancelada")
                                                    mensaje = "Reserva cancelada"
                                                    cargar()
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red
                                            )
                                        ) {
                                            Icon(Icons.Default.Cancel,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Cancelar", fontSize = 12.sp)
                                        }
                                    }
                                }

                                // Eliminar reserva cancelada
                                if (reserva.estatus == "cancelada") {
                                    TextButton(
                                        onClick = {
                                            scope.launch {
                                                repository.cancelarReserva(reserva.idReserva)
                                                mensaje = "Reserva eliminada"
                                                cargar()
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = Color.Red,
                                            modifier = Modifier.size(16.dp))
                                        Text(" Eliminar", color = Color.Red, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo editar fecha de reserva
    reservaAEditar?.let { reserva ->
        AlertDialog(
            onDismissRequest = { reservaAEditar = null },
            title = { Text("Cambiar fecha", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Reserva: ${reserva.nombreRecorrido}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nuevaFecha,
                        onValueChange = { nuevaFecha = it },
                        label = { Text("Nueva fecha (dd/MM/yyyy HH:mm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        // Actualizar fecha en Firestore
                        repository.actualizarFechaReserva(reserva.idReserva, nuevaFecha)
                        mensaje = "✅ Fecha actualizada"
                        reservaAEditar = null
                        cargar()
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { reservaAEditar = null }) { Text("Cancelar") }
            }
        )
    }
}