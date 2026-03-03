package com.utng.rutaceramica.ui.screens.dueno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.utng.rutaceramica.data.model.Taller
import com.utng.rutaceramica.viewmodel.TallerViewModel

/**
 * Formulario para Crear o Editar un Taller artesanal.
 * Si [idTaller] es "nuevo", se crea un taller. Si tiene un ID válido,
 * se cargan los datos existentes para edición.
 *
 * @param idTaller ID del taller a editar, o "nuevo" para crear uno.
 * @param idArtesano UID del dueño que crea/edita el taller.
 * @param onGuardar Callback al guardar exitosamente.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTallerScreen(
    idTaller: String,
    idArtesano: String,
    onGuardar: () -> Unit,
    onBack: () -> Unit,
    tallerViewModel: TallerViewModel = viewModel()
) {
    val esEdicion = idTaller != "nuevo"
    val tallerEditado by tallerViewModel.tallerSeleccionado.collectAsState()
    val cargando by tallerViewModel.cargando.collectAsState()
    val mensaje by tallerViewModel.mensaje.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var historia by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("21.1522") }
    var longitud by remember { mutableStateOf("-100.9338") }
    var telefono by remember { mutableStateOf("") }
    var artesanoIdActual by remember { mutableStateOf(idArtesano) }

    // Si es edición, cargar datos del taller
    LaunchedEffect(idTaller) {
        if (esEdicion) {
            tallerViewModel.cargarTallerPorId(idTaller)
        }
    }

    LaunchedEffect(tallerEditado) {
        if (esEdicion && tallerEditado != null) {
            tallerEditado?.let { t ->
                nombre = t.nombre
                descripcion = t.descripcion
                historia = t.historia
                direccion = t.direccion
                latitud = t.latitud.toString()
                longitud = t.longitud.toString()
                telefono = t.telefono
                artesanoIdActual = t.idArtesano
            }
        }
    }


    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(1500)
            onGuardar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (esEdicion) "Editar Taller" else "Nuevo Taller",
                        fontWeight = FontWeight.Bold)
                },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Todos los campos del MER
            CampoTexto("Nombre del Taller *", nombre) { nombre = it }
            CampoTexto("Descripción *", descripcion, lineas = 3) { descripcion = it }
            CampoTexto("Historia del Taller", historia, lineas = 4) { historia = it }
            CampoTexto("Dirección *", direccion) { direccion = it }
            CampoTexto("Teléfono", telefono) { telefono = it }

            Text("Coordenadas GPS", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = latitud,
                    onValueChange = { latitud = it },
                    label = { Text("Latitud") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = longitud,
                    onValueChange = { longitud = it },
                    label = { Text("Longitud") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = {
                    val taller = Taller(
                        idTaller = if (esEdicion) idTaller else "",
                        nombre = nombre,
                        descripcion = descripcion,
                        historia = historia,
                        direccion = direccion,
                        latitud = latitud.toDoubleOrNull() ?: 21.1522,
                        longitud = longitud.toDoubleOrNull() ?: -100.9338,
                        telefono = telefono,
                        idArtesano = artesanoIdActual,
                        aprobado = tallerEditado?.aprobado ?: false // Mantener estado de aprobación
                    )
                    if (esEdicion) {
                        tallerViewModel.actualizarTaller(taller)
                    } else {
                        tallerViewModel.crearTaller(taller)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !cargando && nombre.isNotBlank() && descripcion.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C))
            ) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(if (esEdicion) "Actualizar Taller" else "Crear Taller",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Campo de texto reutilizable para formularios del Dueño.
 *
 * @param etiqueta Texto de la etiqueta del campo.
 * @param valor Valor actual del campo.
 * @param lineas Número máximo de líneas (1 para campo simple, >1 para área de texto).
 * @param onChange Callback al cambiar el texto.
 */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    lineas: Int = 1,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth().let {
            if (lineas > 1) it.height((lineas * 40 + 20).dp) else it
        },
        maxLines = lineas,
        singleLine = lineas == 1,
        shape = RoundedCornerShape(12.dp)
    )
}