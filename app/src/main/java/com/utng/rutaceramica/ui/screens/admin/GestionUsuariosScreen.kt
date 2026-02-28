package com.utng.rutaceramica.ui.screens.admin

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utng.rutaceramica.utils.Constants
import com.utng.rutaceramica.viewmodel.AdminViewModel

/**
 * Pantalla del Admin para gestionar los roles de todos los usuarios.
 * Muestra la lista de usuarios y permite cambiar su rol entre
 * "turista", "dueno" y "admin" mediante un menú desplegable.
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF37474F),
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
                                Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(usuario.email, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Rol: ${usuario.rol}", fontWeight = FontWeight.Bold)

                                    Box {
                                        OutlinedButton(onClick = { expandido = true }) {
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
                                                    text = { Text(rol.capitalize()) },
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
    }
}