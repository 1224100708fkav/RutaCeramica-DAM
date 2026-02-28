package com.utng.rutaceramica.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utng.rutaceramica.viewmodel.TallerViewModel

/**
 * Pantalla del Admin para revisar y aprobar los talleres pendientes.
 * Muestra todos los talleres con su estado de aprobación y permite
 * aprobarlos con un botón.
 *
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AprobarTalleresScreen(
    onBack: () -> Unit,
    tallerViewModel: TallerViewModel = viewModel()
) {
    val talleres by tallerViewModel.talleres.collectAsState()
    val mensaje by tallerViewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        tallerViewModel.cargarTodosTalleres()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aprobar Talleres", fontWeight = FontWeight.Bold) },
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
                            Text(taller.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(taller.descripcion, fontSize = 13.sp, color = Color.Gray)
                            Text("Artesano ID: ${taller.idArtesano}", fontSize = 11.sp,
                                color = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            if (taller.aprobado) {
                                Text("✅ Ya aprobado", color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.Bold)
                            } else {
                                Button(
                                    onClick = {
                                        tallerViewModel.aprobarTaller(taller.idTaller)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1B5E20)
                                    )
                                ) {
                                    Text("✓ Aprobar Taller")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}