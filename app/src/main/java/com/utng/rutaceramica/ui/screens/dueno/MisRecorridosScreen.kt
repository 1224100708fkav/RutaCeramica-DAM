package com.utng.rutaceramica.ui.screens.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utng.rutaceramica.data.model.Recorrido
import com.utng.rutaceramica.data.repository.RecorridoRepository
import kotlinx.coroutines.launch

/**
 * Pantalla de CRUD de Recorridos para el Dueño del Taller.
 * Permite crear, ver, editar y eliminar los recorridos turísticos del taller.
 *
 * @param idTaller ID del taller cuyos recorridos se gestionan.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisRecorridosScreen(idTaller: String, onBack: () -> Unit) {
    val repository = remember { RecorridoRepository() }
    val scope = rememberCoroutineScope()

    var recorridos by remember { mutableStateOf<List<Recorrido>>(emptyList()) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var recorridoEditando by remember { mutableStateOf<Recorrido?>(null) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }

    fun cargar() {
        scope.launch {
            repository.obtenerRecorridosDeTaller(idTaller).onSuccess { recorridos = it }
        }
    }

    fun limpiar() {
        nombre = ""; descripcion = ""; duracion = ""; precio = ""; capacidad = ""
        recorridoEditando = null
    }

    LaunchedEffect(idTaller) { cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recorridos", fontWeight = FontWeight.Bold) },
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
                onClick = { limpiar(); mostrarDialogo = true },
                containerColor = Color(0xFF4A148C)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo recorrido", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(recorrido.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                modifier = Modifier.weight(1f))
                            Row {
                                IconButton(onClick = {
                                    recorridoEditando = recorrido
                                    nombre = recorrido.nombre
                                    descripcion = recorrido.descripcion
                                    duracion = recorrido.duracion
                                    precio = recorrido.precio.toString()
                                    capacidad = recorrido.capacidadMaxima.toString()
                                    mostrarDialogo = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar",
                                        tint = Color(0xFF4A148C))
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        repository.eliminarRecorrido(recorrido.idRecorrido)
                                        cargar()
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                                        tint = Color.Red)
                                }
                            }
                        }
                        Text(recorrido.descripcion, fontSize = 13.sp, color = Color.Gray)
                        Text("⏱ ${recorrido.duracion} | 💰 $${recorrido.precio} | 👥 ${recorrido.capacidadMaxima}",
                            fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false; limpiar() },
            title = {
                Text(if (recorridoEditando != null) "Editar Recorrido" else "Nuevo Recorrido")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CampoTexto("Nombre *", nombre) { nombre = it }
                    CampoTexto("Descripción", descripcion, 3) { descripcion = it }
                    CampoTexto("Duración (ej: 2 horas)", duracion) { duracion = it }
                    CampoTexto("Precio ($)", precio) { precio = it }
                    CampoTexto("Capacidad máxima", capacidad) { capacidad = it }
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val recorrido = Recorrido(
                            idRecorrido = recorridoEditando?.idRecorrido ?: "",
                            nombre = nombre,
                            descripcion = descripcion,
                            duracion = duracion,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            capacidadMaxima = capacidad.toIntOrNull() ?: 0,
                            idTaller = idTaller
                        )
                        if (recorridoEditando != null) {
                            repository.actualizarRecorrido(recorrido)
                        } else {
                            repository.crearRecorrido(recorrido)
                        }
                        mostrarDialogo = false
                        limpiar()
                        cargar()
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false; limpiar() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}