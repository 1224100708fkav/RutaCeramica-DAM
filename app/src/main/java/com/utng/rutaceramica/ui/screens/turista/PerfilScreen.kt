package com.utng.rutaceramica.ui.screens.turista

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.utng.rutaceramica.viewmodel.PerfilViewModel

/**
 * Pantalla de perfil del usuario Turista.
 * Permite ver y editar su información personal: nombre, origen, descripción y foto.
 * 
 * @param uid UID del usuario actual.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    uid: String,
    onBack: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    
    // Campos editables
    var nombre by remember { mutableStateOf("") }
    var origen by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    
    // Launcher para seleccionar imagen de la galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fotoUri = uri
    }

    // Cargar perfil al iniciar
    LaunchedEffect(uid) {
        viewModel.cargarPerfil(uid)
    }
    
    // Sincronizar campos cuando el usuario se carga
    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            origen = it.origen
            descripcion = it.descripcion
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
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
        if (cargando && usuario == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto de perfil
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    if (fotoUri != null) {
                        AsyncImage(
                            model = fotoUri,
                            contentDescription = "Foto de perfil seleccionada",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (usuario?.fotoUrl?.isNotBlank() == true) {
                        AsyncImage(
                            model = usuario?.fotoUrl,
                            contentDescription = "Foto de perfil actual",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, 
                                modifier = Modifier.size(80.dp), tint = Color.Gray)
                        }
                    }
                    
                    // Icono de edición sobre la foto
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = Color(0xFF1A237E),
                        tonalElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Cambiar foto",
                            modifier = Modifier.padding(8.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Formulario
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = origen,
                    onValueChange = { origen = it },
                    label = { Text("Lugar de Origen") },
                    placeholder = { Text("Ej: Ciudad de México, México") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Place, null) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Pequeña Descripción") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (cargando) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            viewModel.actualizarPerfil(nombre, origen, descripcion, fotoUri)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                        enabled = nombre.isNotBlank()
                    ) {
                        Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Mostrar mensaje de éxito o error
                mensaje?.let { msg ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        msg, 
                        color = if (msg.contains("Error")) Color.Red else Color(0xFF1B5E20),
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    // Limpiar mensaje después de unos segundos
                    LaunchedEffect(msg) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.limpiarMensaje()
                    }
                }
            }
        }
    }
}
