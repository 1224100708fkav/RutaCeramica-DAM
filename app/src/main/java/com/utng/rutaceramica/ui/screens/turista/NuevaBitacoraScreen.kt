package com.utng.rutaceramica.ui.screens.turista

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.utng.rutaceramica.utils.CamaraUtils
import com.utng.rutaceramica.viewmodel.BitacoraViewModel

/**
 * Pantalla para crear una nueva entrada en la Bitácora del Turista.
 * Integra CameraX para capturar una foto directamente desde la app,
 * permite agregar título y comentario, y sube todo a Firebase.
 *
 * @param idUsuario UID del turista propietario.
 * @param onGuardar Callback al guardar exitosamente.
 * @param onBack Navegar hacia atrás sin guardar.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NuevaBitacoraScreen(
    idUsuario: String,
    onGuardar: () -> Unit,
    onBack: () -> Unit,
    bitacoraViewModel: BitacoraViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var titulo by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var mostrarCamara by remember { mutableStateOf(false) }

    val camaraUtils = remember { CamaraUtils(context) }
    val cargando by bitacoraViewModel.cargando.collectAsState()
    val mensaje by bitacoraViewModel.mensaje.collectAsState()

    // Permiso de cámara
    val permisoCamara = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(1500)
            onGuardar()
        }
    }

    DisposableEffect(Unit) {
        onDispose { camaraUtils.liberar() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Recuerdo", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vista previa de la foto o cámara
            if (mostrarCamara) {
                // Mostrar vista de CameraX
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).also { previewView ->
                                camaraUtils.iniciarCamara(
                                    lifecycleOwner = lifecycleOwner,
                                    surfaceProvider = previewView.surfaceProvider
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    // Botón disparador
                    FloatingActionButton(
                        onClick = {
                            camaraUtils.tomarFoto(
                                onFotoCapturada = { uri ->
                                    fotoUri = uri
                                    mostrarCamara = false
                                },
                                onError = { /* Error ignorado silenciosamente */ }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        containerColor = Color.White
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = "Capturar",
                            tint = Color(0xFF1A237E))
                    }
                }
            } else if (fotoUri != null) {
                // Mostrar foto capturada
                Box {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Foto capturada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { fotoUri = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Quitar foto",
                            tint = Color.White)
                    }
                }
            } else {
                // Botón para abrir cámara
                OutlinedButton(
                    onClick = {
                        if (permisoCamara.status.isGranted) {
                            mostrarCamara = true
                        } else {
                            permisoCamara.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null,
                            modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tomar Foto del Recuerdo")
                    }
                }
            }

            // Campo Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título del recuerdo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Comentario
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Descripción o comentario") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            // Botón Guardar
            Button(
                onClick = {
                    bitacoraViewModel.crearEntrada(
                        titulo = titulo,
                        comentario = comentario,
                        idUsuario = idUsuario,
                        fotoUri = fotoUri
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !cargando && titulo.isNotBlank() && comentario.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
            ) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar en Bitácora", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}