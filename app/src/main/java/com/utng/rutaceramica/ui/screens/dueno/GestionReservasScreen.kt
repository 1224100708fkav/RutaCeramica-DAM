package com.utng.rutaceramica.ui.screens.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utng.rutaceramica.data.model.Reserva
import com.utng.rutaceramica.data.repository.RecorridoRepository
import com.utng.rutaceramica.data.repository.ReservaRepository
import kotlinx.coroutines.launch

/**
 * Pantalla para que el Dueño gestione el estatus de las Reservas
 * de todos sus recorridos. Puede confirmar o cancelar cada reserva.
 *
 * @param idTaller ID del taller cuyas reservas se gestionan.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionReservasScreen(idTaller: String, onBack: () -> Unit) {
    val reservaRepository = remember { ReservaRepository() }
    val recorridoRepository = remember { RecorridoRepository() }
    val scope = rememberCoroutineScope()
    var reservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    fun cargar() {
        scope.launch {
            cargando = true
            // Obtener recorridos del taller y sus reservas
            recorridoRepository.obtenerRecorridosDeTaller(idTaller).onSuccess { recorridos ->
                val todasReservas = mutableListOf<Reserva>()
                recorridos.forEach { recorrido ->
                    reservaRepository.obtenerReservasDeRecorrido(recorrido.idRecorrido)
                        .onSuccess { lista -> todasReservas.addAll(lista) }
                }
                reservas = todasReservas
            }
            cargando = false
        }
    }

    LaunchedEffect(idTaller) { cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reservas", fontWeight = FontWeight.Bold)
                        if (reservas.any { it.estatus == "pendiente" }) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge {
                                Text("${reservas.count { it.estatus == "pendiente" }}")
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF006064),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reservas) { reserva ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(reserva.nombreUsuario, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(reserva.nombreRecorrido, fontSize = 13.sp, color = Color.Gray)
                        Text("📅 ${reserva.fechaHora}", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Estado: ${reserva.estatus.uppercase()}",
                            fontWeight = FontWeight.Bold,
                            color = when (reserva.estatus) {
                                "confirmada" -> Color(0xFF1B5E20)
                                "cancelada" -> Color.Red
                                else -> Color(0xFFF57F17)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        reservaRepository.actualizarEstatus(
                                            reserva.idReserva, "confirmada")
                                        cargar()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1B5E20)
                                ),
                                enabled = reserva.estatus != "confirmada"
                            ) { Text("Confirmar", fontSize = 12.sp) }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        reservaRepository.actualizarEstatus(
                                            reserva.idReserva, "cancelada")
                                        cargar()
                                    }
                                },
                                enabled = reserva.estatus != "cancelada"
                            ) { Text("Cancelar", fontSize = 12.sp, color = Color.Red) }
                        }
                    }
                }
            }
        }
    }
}