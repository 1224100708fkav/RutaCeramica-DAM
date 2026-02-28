package com.utng.rutaceramica.ui.screens.turista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.utng.rutaceramica.data.model.Recorrido
import com.utng.rutaceramica.data.model.Reserva
import com.utng.rutaceramica.data.repository.RecorridoRepository
import com.utng.rutaceramica.data.repository.ReservaRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla que muestra todos los Recorridos disponibles.
 * Permite al turista ver detalles y hacer una Reserva directamente.
 *
 * @param idUsuario UID del turista para crear reservas.
 * @param nombreUsuario Nombre del turista para mostrar en la reserva.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecorridosScreen(
    idUsuario: String = "",
    nombreUsuario: String = "",
    onBack: () -> Unit
) {
    val recorridoRepo = remember { RecorridoRepository() }
    val reservaRepo = remember { ReservaRepository() }
    val scope = rememberCoroutineScope()

    var recorridos by remember { mutableStateOf<List<Recorrido>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var recorridoSeleccionado by remember { mutableStateOf<Recorrido?>(null) }
    var mostrarDialogoReserva by remember { mutableStateOf(false) }
    var fechaHora by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        recorridoRepo.obtenerTodosRecorridos()
            .onSuccess { recorridos = it }
        cargando = false
    }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(2500)
            mensaje = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🗺 Recorridos", fontWeight = FontWeight.Bold) },
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
            } else if (recorridos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗺", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No hay recorridos disponibles", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recorridos) { recorrido ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(recorrido.nombre,
                                    fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(recorrido.descripcion,
                                    fontSize = 14.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("⏱ ${recorrido.duracion}", fontSize = 13.sp)
                                        Text("👥 Máx. ${recorrido.capacidadMaxima} personas",
                                            fontSize = 13.sp)
                                    }
                                    Text("💰 $${recorrido.precio}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A237E))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        recorridoSeleccionado = recorrido
                                        fechaHora = SimpleDateFormat(
                                            "dd/MM/yyyy HH:mm",
                                            Locale.getDefault()
                                        ).format(Date())
                                        mostrarDialogoReserva = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1A237E)
                                    ),
                                    enabled = idUsuario.isNotBlank()
                                ) {
                                    Icon(Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reservar lugar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para confirmar reserva
    if (mostrarDialogoReserva && recorridoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoReserva = false },
            title = { Text("Reservar Recorrido", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📍 ${recorridoSeleccionado!!.nombre}",
                        fontWeight = FontWeight.Bold)
                    Text("💰 Precio: $${recorridoSeleccionado!!.precio}")
                    Text("👥 Capacidad: ${recorridoSeleccionado!!.capacidadMaxima} personas")
                    Divider()
                    OutlinedTextField(
                        value = fechaHora,
                        onValueChange = { fechaHora = it },
                        label = { Text("Fecha y hora (dd/MM/yyyy HH:mm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Text("Estado: Pendiente de confirmación",
                        fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val reserva = Reserva(
                            fechaHora = fechaHora,
                            estatus = "pendiente",
                            idUsuario = idUsuario,
                            idRecorrido = recorridoSeleccionado!!.idRecorrido,
                            nombreUsuario = nombreUsuario,
                            nombreRecorrido = recorridoSeleccionado!!.nombre
                        )
                        reservaRepo.crearReserva(reserva)
                            .onSuccess {
                                mensaje = "✅ Reserva creada exitosamente"
                                mostrarDialogoReserva = false
                            }
                            .onFailure {
                                mensaje = "❌ Error al crear reserva"
                            }
                    }
                }) { Text("Confirmar Reserva") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoReserva = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}