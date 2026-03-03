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
import com.utng.rutaceramica.data.model.Usuario
import com.utng.rutaceramica.utils.Constants
import com.utng.rutaceramica.viewmodel.AdminViewModel

/**
 * Pantalla del Admin para gestionar los roles de todos los usuarios.
 * Muestra la lista de usuarios y permite cambiar su rol entre
 * "turista", "dueno" y "admin" mediante un menú desplegable.
 * Permite CRUD completo: crear, leer, actualizar y eliminar.
 *
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    onBack: () -> Unit,
    adminViewModel: AdminViewModel = viewModel()
) {
    val usuarios by adminViewModel.usuarios.collectAsState()
    val cargando by adminViewModel.cargando.collectAsState()
    val mensaje by adminViewModel.mensaje.collectAsState()

    var mostrarDialogoUsuario by remember { mutableStateOf(false) }
    var usuarioAEditar by remember { mutableStateOf<Usuario?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        adminViewModel.cargarUsuarios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        usuarioAEditar = null
                        mostrarDialogoUsuario = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo Usuario", tint = Color.White)
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
                            TextButton(onClick = { adminViewModel.limpiarMensaje() }) {
                                Text("OK", color = Color.Yellow)
                            }
                        }
                    ) { Text(it) }
                }

                if (cargando && usuarios.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(usuarios) { usuario ->
                            var expandido by remember { mutableStateOf(false) }
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
                                            Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(usuario.email, fontSize = 13.sp, color = Color.Gray)
                                        }
                                        Row {
                                            IconButton(onClick = {
                                                usuarioAEditar = usuario
                                                mostrarDialogoUsuario = true
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF1A237E))
                                            }
                                            IconButton(onClick = {
                                                mostrarDialogoEliminar = usuario.idUsuario
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Rol: ${usuario.rol}", fontWeight = FontWeight.Bold)

                                        Box {
                                            OutlinedButton(
                                                onClick = { expandido = true },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Cambiar Rol", fontSize = 12.sp)
                                            }
                                            DropdownMenu(
                                                expanded = expandido,
                                                onDismissRequest = { expandido = false }
                                            ) {
                                                listOf(
                                                    Constants.ROL_TURISTA,
                                                    Constants.ROL_DUENO,
                                                    Constants.ROL_ADMIN
                                                ).forEach { rol ->
                                                    DropdownMenuItem(
                                                        text = { Text(rol.replaceFirstChar { it.uppercase() }) },
                                                        onClick = {
                                                            adminViewModel.cambiarRol(
                                                                usuario.idUsuario, rol)
                                                            expandido = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (cargando && usuarios.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }

        // Diálogo para Crear/Editar Usuario
        if (mostrarDialogoUsuario) {
            UsuarioDialog(
                usuario = usuarioAEditar,
                onDismiss = { mostrarDialogoUsuario = false },
                onConfirm = { nombre, email, rol ->
                    if (usuarioAEditar == null) {
                        adminViewModel.crearUsuario(Usuario(nombre = nombre, email = email, rol = rol))
                    } else {
                        adminViewModel.actualizarUsuario(usuarioAEditar!!.copy(nombre = nombre, email = email, rol = rol))
                    }
                    mostrarDialogoUsuario = false
                }
            )
        }

        // Diálogo de Confirmación para Eliminar
        if (mostrarDialogoEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = null },
                title = { Text("Eliminar Usuario") },
                text = { Text("¿Estás seguro de que deseas eliminar este usuario? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            adminViewModel.eliminarUsuario(mostrarDialogoEliminar!!)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioDialog(
    usuario: Usuario?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var email by remember { mutableStateOf(usuario?.email ?: "") }
    var rol by remember { mutableStateOf(usuario?.rol ?: Constants.ROL_TURISTA) }
    var expandidoRol by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (usuario == null) "Nuevo Usuario" else "Editar Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Rol", style = MaterialTheme.typography.labelMedium)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandidoRol = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(rol.replaceFirstChar { it.uppercase() })
                    }
                    DropdownMenu(
                        expanded = expandidoRol,
                        onDismissRequest = { expandidoRol = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        listOf(Constants.ROL_TURISTA, Constants.ROL_DUENO, Constants.ROL_ADMIN).forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    rol = r
                                    expandidoRol = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre, email, rol) },
                enabled = nombre.isNotBlank() && email.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}