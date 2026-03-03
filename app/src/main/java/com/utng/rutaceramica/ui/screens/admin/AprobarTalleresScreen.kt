package com.utng.rutaceramica.ui.screens.admin

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
import com.utng.rutaceramica.viewmodel.TallerViewModel

/**
 * Pantalla del Admin para revisar, aprobar y gestionar talleres.
 * Muestra todos los talleres con su estado de aprobación y permite
 * aprobarlos, editarlos, eliminarlos o crear nuevos.
 *
 * @param onBack Navegar hacia atrás.
 * @param onEditarTaller Callback para navegar al formulario de edición.
 * @param onNuevoTaller Callback para navegar al formulario de creación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AprobarTalleresScreen(
    onBack: () -> Unit,
    onEditarTaller: (String) -> Unit,
    onNuevoTaller: () -> Unit,
    tallerViewModel: TallerViewModel = viewModel()
) {
    val talleres by tallerViewModel.talleres.collectAsState()
    val cargando by tallerViewModel.cargando.collectAsState()
    val mensaje by tallerViewModel.mensaje.collectAsState()
    
    var mostrarDialogoEliminar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        tallerViewModel.cargarTodosTalleres()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Talleres", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = onNuevoTaller) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo Taller", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF37474F),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                mensaje?.let {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { tallerViewModel.limpiarMensajes() }) {
                                Text("OK", color = Color.Yellow)
                            }
                        }
                    ) { Text(it) }
                }

                if (cargando && talleres.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(talleres) { taller ->
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(taller.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(taller.descripcion, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
                                        }
                                        Row {
                                            IconButton(onClick = { onEditarTaller(taller.idTaller) }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF1A237E))
                                            }
                                            IconButton(onClick = { mostrarDialogoEliminar = taller.idTaller }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                            }
                                        }
                                    }
                                    
                                    Text("Artesano ID: ${taller.idArtesano}", fontSize = 11.sp,
                                        color = Color.LightGray)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (taller.aprobado) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Ya aprobado", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    tallerViewModel.aprobarTaller(taller.idTaller)
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF1B5E20)
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                            ) {
                                                Text("✓ Aprobar Taller", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (cargando && talleres.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }

        // Diálogo de Confirmación para Eliminar
        if (mostrarDialogoEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = null },
                title = { Text("Eliminar Taller") },
                text = { Text("¿Estás seguro de que deseas eliminar este taller? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            tallerViewModel.eliminarTaller(mostrarDialogoEliminar!!, "") // idArtesano no se usa si cargamos todos
                            // Pequeño truco: eliminarTaller en el VM actualiza la lista del artesano. 
                            // Necesitamos que actualice todos si es admin.
                            // Modificaré el VM para manejar esto.
                            mostrarDialogoEliminar = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}