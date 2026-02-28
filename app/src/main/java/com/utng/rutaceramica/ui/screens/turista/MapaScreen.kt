package com.utng.rutaceramica.ui.screens.turista

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.utng.rutaceramica.data.model.Taller
import com.utng.rutaceramica.utils.Constants
import com.utng.rutaceramica.viewmodel.TallerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * Pantalla de mapa interactivo con navegación GPS integrada.
 * Muestra los talleres como marcadores y traza una ruta desde
 * la ubicación actual del usuario hasta el taller seleccionado,
 * todo dentro de la misma app sin abrir Google Maps externo.
 *
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapaScreen(
    onBack: () -> Unit,
    idTallerInicial: String = "",
    tallerViewModel: TallerViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val talleres by tallerViewModel.talleres.collectAsState()

    // Taller seleccionado para mostrar info y ruta
    var tallerSeleccionado by remember { mutableStateOf<Taller?>(null) }
    // Ubicación actual del usuario
    var ubicacionUsuario by remember { mutableStateOf<LatLng?>(null) }
    // Puntos de la ruta dibujada en el mapa
    var puntosRuta by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    // Instrucciones de navegación paso a paso
    var instrucciones by remember { mutableStateOf<List<String>>(emptyList()) }
    // Mostrar panel de instrucciones
    var mostrarInstrucciones by remember { mutableStateOf(false) }
    // Estado de carga de la ruta
    var cargandoRuta by remember { mutableStateOf(false) }
    // Mensaje de error si la ruta falla
    var errorRuta by remember { mutableStateOf<String?>(null) }

    val permisosUbicacion = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Centro inicial: Dolores Hidalgo
    val doloresHidalgo = LatLng(
        Constants.LAT_DOLORES_HIDALGO,
        Constants.LNG_DOLORES_HIDALGO
    )
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(doloresHidalgo, 14f)
    }

    LaunchedEffect(Unit) {
        tallerViewModel.cargarTalleresAprobados()
        // Solicitar permiso de ubicación
        if (!permisosUbicacion.status.isGranted) {
            permisosUbicacion.launchPermissionRequest()
        } else {
            // Obtener ubicación actual
            obtenerUbicacionActual(context) { ubicacion ->
                ubicacionUsuario = ubicacion
            }
        }
    }

    LaunchedEffect(talleres) {
        if (idTallerInicial.isNotBlank() && talleres.isNotEmpty()) {
            val taller = talleres.find { it.idTaller == idTallerInicial }
            taller?.let {
                tallerSeleccionado = it
                cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.latitud, it.longitud), 16f
                    )
                )
            }
        }
    }

    LaunchedEffect(permisosUbicacion.status.isGranted) {
        if (permisosUbicacion.status.isGranted) {
            obtenerUbicacionActual(context) { ubicacion ->
                ubicacionUsuario = ubicacion
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🗺 Mapa de Talleres", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    // Botón para centrar en ubicación actual
                    if (ubicacionUsuario != null) {
                        IconButton(onClick = {
                            scope.launch {
                                ubicacionUsuario?.let {
                                    cameraState.animate(
                                        CameraUpdateFactory.newLatLngZoom(it, 15f)
                                    )
                                }
                            }
                        }) {
                            Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación",
                                tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── MAPA PRINCIPAL ─────────────────────────────────────
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = MapProperties(
                    isMyLocationEnabled = permisosUbicacion.status.isGranted
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = true
                )
            ) {
                // Marcadores de talleres
                talleres.forEach { taller ->
                    val posicion = LatLng(taller.latitud, taller.longitud)
                    Marker(
                        state = MarkerState(position = posicion),
                        title = taller.nombre,
                        snippet = taller.direccion,
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (tallerSeleccionado?.idTaller == taller.idTaller)
                                BitmapDescriptorFactory.HUE_BLUE
                            else
                                BitmapDescriptorFactory.HUE_RED
                        ),
                        onClick = {
                            tallerSeleccionado = taller
                            mostrarInstrucciones = false
                            puntosRuta = emptyList()
                            instrucciones = emptyList()
                            errorRuta = null
                            true
                        }
                    )
                }

                // Marcador de ubicación del usuario
                ubicacionUsuario?.let { pos ->
                    Marker(
                        state = MarkerState(position = pos),
                        title = "Tu ubicación",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    )
                }

                // Dibujar la ruta en el mapa
                if (puntosRuta.size >= 2) {
                    Polyline(
                        points = puntosRuta,
                        color = Color(0xFF1A237E),
                        width = 12f,
                        pattern = listOf(Dot(), Gap(10f))
                    )
                }
            }

            // ── PANEL DE TALLER SELECCIONADO ───────────────────────
            tallerSeleccionado?.let { taller ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = taller.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = taller.direccion,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = { 
                                tallerSeleccionado = null
                                mostrarInstrucciones = false
                                puntosRuta = emptyList()
                                instrucciones = emptyList()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Botón Cómo llegar (ruta dentro de la app)
                            Button(
                                onClick = {
                                    if (ubicacionUsuario != null) {
                                        scope.launch {
                                            cargandoRuta = true
                                            errorRuta = null
                                            val resultado = withContext(Dispatchers.IO) {
                                                calcularRuta(
                                                    origen = ubicacionUsuario!!,
                                                    destino = LatLng(taller.latitud, taller.longitud),
                                                    context = context
                                                )
                                            }
                                            
                                            if (resultado.first.isNotEmpty()) {
                                                puntosRuta = resultado.first
                                                instrucciones = resultado.second
                                                
                                                // Mover cámara para ver toda la ruta
                                                val bounds = LatLngBounds.builder()
                                                puntosRuta.forEach { bounds.include(it) }
                                                cameraState.animate(
                                                    CameraUpdateFactory.newLatLngBounds(
                                                        bounds.build(), 120
                                                    )
                                                )
                                                mostrarInstrucciones = true
                                            } else {
                                                errorRuta = "No se pudo encontrar una ruta conduciendo."
                                            }
                                            cargandoRuta = false
                                        }
                                    } else {
                                        // Si no hay GPS, solo centrar el mapa en el taller
                                        scope.launch {
                                            cameraState.animate(
                                                CameraUpdateFactory.newLatLngZoom(
                                                    LatLng(taller.latitud, taller.longitud), 16f
                                                )
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1A237E)
                                )
                            ) {
                                if (cargandoRuta) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Navigation, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cómo llegar", fontSize = 13.sp)
                                }
                            }

                            // Botón para abrir Google Maps (como alternativa si falla)
                            OutlinedButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("google.navigation:q=${taller.latitud},${taller.longitud}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Map, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Abrir Maps", fontSize = 13.sp)
                            }
                        }

                        // Mensaje de error
                        errorRuta?.let {
                            Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }

                        // Panel de instrucciones paso a paso
                        if (mostrarInstrucciones && instrucciones.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("📍 Pasos para llegar:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(modifier = Modifier.heightIn(max = 200.dp)) {
                                LazyColumn {
                                    itemsIndexed(instrucciones) { index, paso ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "${index + 1}.",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1A237E),
                                                modifier = Modifier.width(28.dp)
                                            )
                                            Text(
                                                text = paso,
                                                fontSize = 12.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Indicador de carga
            if (cargandoRuta) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trazando ruta...", color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Obtiene la última ubicación conocida del dispositivo usando GPS.
 *
 * @param context Contexto de la aplicación.
 * @param onUbicacion Callback con la posición [LatLng] obtenida.
 */
private fun obtenerUbicacionActual(
    context: Context,
    onUbicacion: (LatLng) -> Unit
) {
    try {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
            location?.let {
                onUbicacion(LatLng(it.latitude, it.longitude))
            }
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

/**
 * Calcula la ruta entre dos puntos usando la API de Directions de Google Maps.
 * Intenta primero "driving" y si falla intenta "walking".
 *
 * @param origen Coordenadas de origen (ubicación del usuario).
 * @param destino Coordenadas de destino (ubicación del taller).
 * @param context Contexto para acceder a los recursos.
 * @return Par con lista de puntos de la ruta e instrucciones de texto.
 */
private suspend fun calcularRuta(
    origen: LatLng,
    destino: LatLng,
    context: Context
): Pair<List<LatLng>, List<String>> {
    val apiKey = try {
        context.packageManager
            .getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
            .metaData?.getString("com.google.android.geo.API_KEY") ?: ""
    } catch (e: Exception) { "" }

    if (apiKey.isBlank()) return Pair(emptyList(), emptyList())

    // Intentar primero conduciendo, si falla intentar caminando
    val modos = listOf("driving", "walking")
    val client = OkHttpClient()

    for (modo in modos) {
        try {
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origen.latitude},${origen.longitude}" +
                    "&destination=${destino.latitude},${destino.longitude}" +
                    "&mode=$modo" +
                    "&language=es" +
                    "&key=$apiKey"

            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: ""
            
            val resultado = parsearRespuestaDirections(json)
            if (resultado.first.isNotEmpty()) {
                // Si encontramos ruta, la devolvemos
                return resultado
            }
        } catch (e: Exception) {
            android.util.Log.e("MAPS_DEBUG", "Error con modo $modo: ${e.message}")
        }
    }

    return Pair(emptyList(), emptyList())
}

/**
 * Parsea la respuesta JSON de la API de Directions de Google.
 * Extrae los puntos de la polilínea y las instrucciones de navegación.
 *
 * @param json Respuesta JSON de la API de Directions.
 * @return Par con puntos de la ruta e instrucciones limpias.
 */
private fun parsearRespuestaDirections(json: String): Pair<List<LatLng>, List<String>> {
    val puntos = mutableListOf<LatLng>()
    val instrucciones = mutableListOf<String>()

    try {
        val jsonObj = JSONObject(json)
        val status = jsonObj.getString("status")
        
        if (status != "OK") {
            android.util.Log.e("MAPS_DEBUG", "Error de API: $status")
            return Pair(emptyList(), emptyList())
        }

        val routes = jsonObj.getJSONArray("routes")
        if (routes.length() == 0) return Pair(emptyList(), emptyList())

        val route = routes.getJSONObject(0)
        val legs = route.getJSONArray("legs")
        val leg = legs.getJSONObject(0)

        // Obtener puntos de la polilínea general
        val polyline = route.getJSONObject("overview_polyline").getString("points")
        puntos.addAll(decodificarPolyline(polyline))

        // Obtener instrucciones de cada paso
        val steps = leg.getJSONArray("steps")
        for (i in 0 until steps.length()) {
            val step = steps.getJSONObject(i)
            val instruccion = step.getString("html_instructions")
                .replace(Regex("<[^>]*>"), "") // Quitar tags HTML
                .trim()
            val distancia = step.getJSONObject("distance").getString("text")
            instrucciones.add("$instruccion ($distancia)")
        }
    } catch (e: Exception) {
        android.util.Log.e("MAPS_DEBUG", "Error parseando ruta: ${e.message}")
    }

    return Pair(puntos, instrucciones)
}

/**
 * Decodifica una cadena de polilínea codificada en formato Google Encoded Polyline.
 * Convierte la cadena comprimida en una lista de coordenadas [LatLng].
 *
 * @param encoded Cadena codificada de la polilínea.
 * @return Lista de puntos [LatLng] que forman la ruta.
 */
private fun decodificarPolyline(encoded: String): List<LatLng> {
    val puntos = mutableListOf<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < encoded.length) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dLat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dLng

        puntos.add(LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
    }
    return puntos
}