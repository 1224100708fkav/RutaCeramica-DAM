package com.utng.rutaceramica.ui.screens.turista

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.utng.rutaceramica.data.model.Producto
import com.utng.rutaceramica.data.model.Recorrido
import com.utng.rutaceramica.data.model.Resena
import com.utng.rutaceramica.data.repository.ProductoRepository
import com.utng.rutaceramica.data.repository.RecorridoRepository
import com.utng.rutaceramica.data.repository.ResenaRepository
import com.utng.rutaceramica.data.repository.TallerRepository
import com.utng.rutaceramica.viewmodel.CarritoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de detalle completo de un Taller.
 * Muestra: info, historia, productos, recorridos disponibles y reseñas.
 * El turista puede escribir reseñas con puntuación.
 *
 * @param idTaller ID del taller a mostrar.
 * @param idUsuario UID del turista (para reseñas).
 * @param nombreUsuario Nombre del turista (para reseñas).
 * @param carritoViewModel ViewModel del carrito compartido.
 * @param onBack Navegar hacia atrás.
 * @param onComoLlegar Navegar al mapa con este taller seleccionado.
 * @param onIrAlCarrito Navegar a la pantalla del carrito.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTallerScreen(
    idTaller: String,
    idUsuario: String = "",
    nombreUsuario: String = "",
    carritoViewModel: CarritoViewModel,
    onBack: () -> Unit,
    onComoLlegar: (String) -> Unit,
    onIrAlCarrito: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tallerRepository = remember { TallerRepository() }
    val productoRepository = remember { ProductoRepository() }
    val recorridoRepository = remember { RecorridoRepository() }
    val resenaRepository = remember { ResenaRepository() }

    val carritoItems by carritoViewModel.items.collectAsState()

    var taller by remember {
        mutableStateOf<com.utng.rutaceramica.data.model.Taller?>(null) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var recorridos by remember { mutableStateOf<List<Recorrido>>(emptyList()) }
    var resenas by remember { mutableStateOf<List<Resena>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Estado para cantidades de productos
    val cantidades = remember { mutableStateMapOf<String, Int>() }

    // Estado para nueva reseña
    var mostrarFormResena by remember { mutableStateOf(false) }
    var comentarioResena by remember { mutableStateOf("") }
    var puntuacion by remember { mutableStateOf(5f) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idTaller) {
        scope.launch {
            tallerRepository.obtenerTalleresAprobados().onSuccess { lista ->
                taller = lista.find { it.idTaller == idTaller }
            }
            productoRepository.obtenerProductosDeTaller(idTaller)
                .onSuccess { productos = it }
            recorridoRepository.obtenerRecorridosDeTaller(idTaller)
                .onSuccess { recorridos = it }
            resenaRepository.obtenerResenasDeTaller(idTaller)
                .onSuccess { resenas = it }
            cargando = false
        }
    }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(2000)
            mensaje = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(taller?.nombre ?: "Detalle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (carritoItems.isNotEmpty()) {
                                Badge { Text(carritoItems.sumOf { it.cantidad }.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = onIrAlCarrito) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Ver Carrito"
                            )
                        }
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
        if (cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            taller?.let { t ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Foto principal
                    if (t.fotoUrl.isNotBlank()) {
                        AsyncImage(
                            model = t.fotoUrl,
                            contentDescription = "Foto del taller",
                            modifier = Modifier.fillMaxWidth().height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏺", fontSize = 64.sp)
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Info principal
                        Text(t.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(t.descripcion, fontSize = 15.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Historia
                        if (t.historia.isNotBlank()) {
                            Text("Historia", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(t.historia, fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Contacto
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null,
                                tint = Color(0xFF1A237E))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(t.direccion, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null,
                                tint = Color(0xFF1A237E))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(t.telefono, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Cómo llegar
                        Button(
                            onClick = { onComoLlegar(t.idTaller) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A237E)
                            )
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cómo llegar")
                        }

                        // ── PRODUCTOS ──────────────────────────────────
                        if (productos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("🛍 Productos",
                                fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            productos.forEach { producto ->
                                // Inicializar cantidad si no existe
                                if (!cantidades.containsKey(producto.idProducto)) {
                                    cantidades[producto.idProducto] = 1
                                }
                                val cantidad = cantidades[producto.idProducto] ?: 1

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF5F5F5)
                                    )
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (producto.fotoUrl.isNotBlank()) {
                                                AsyncImage(
                                                    model = producto.fotoUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(70.dp)
                                                        .padding(end = 12.dp),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(70.dp)
                                                        .padding(end = 12.dp),
                                                    contentAlignment = Alignment.Center
                                                ) { Text("🏺", fontSize = 32.sp) }
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(producto.nombre,
                                                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                Text("${producto.material} · ${producto.tecnica}",
                                                    fontSize = 12.sp, color = Color.Gray)
                                                Text(producto.descripcion,
                                                    fontSize = 12.sp, color = Color.DarkGray,
                                                    maxLines = 2)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("$${producto.precio}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF1A237E))
                                                Text("Stock: ${producto.stock}",
                                                    fontSize = 11.sp, color = Color.Gray)
                                            }
                                        }
                                        
                                        // Selector de cantidad y botón apartar
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { 
                                                        if (cantidad > 1) cantidades[producto.idProducto] = cantidad - 1 
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Remove, contentDescription = "Menos")
                                                }
                                                Text(
                                                    text = cantidad.toString(),
                                                    modifier = Modifier.padding(horizontal = 8.dp),
                                                    fontWeight = FontWeight.Bold
                                                )
                                                IconButton(
                                                    onClick = { 
                                                        if (cantidad < producto.stock) cantidades[producto.idProducto] = cantidad + 1 
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = "Más")
                                                }
                                            }
                                            
                                            Button(
                                                onClick = {
                                                    carritoViewModel.agregarAlCarrito(producto, cantidad)
                                                    mensaje = "✅ ${producto.nombre} apartado"
                                                },
                                                modifier = Modifier.height(36.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF1A237E)
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                enabled = producto.stock > 0
                                            ) {
                                                Icon(Icons.Default.AddShoppingCart, 
                                                    contentDescription = null, 
                                                    modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Apartar", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── RECORRIDOS ─────────────────────────────────
                        if (recorridos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("🗺 Recorridos",
                                fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            recorridos.forEach { recorrido ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE8EAF6)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(recorrido.nombre,
                                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(recorrido.descripcion,
                                            fontSize = 13.sp, color = Color.DarkGray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("⏱ ${recorrido.duracion}", fontSize = 12.sp)
                                            Text("💰 $${recorrido.precio}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1A237E))
                                            Text("👥 ${recorrido.capacidadMaxima}",
                                                fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // ── RESEÑAS ────────────────────────────────────
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐ Reseñas (${resenas.size})",
                                fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            if (idUsuario.isNotBlank()) {
                                TextButton(onClick = {
                                    mostrarFormResena = !mostrarFormResena
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Text("Escribir reseña")
                                }
                            }
                        }

                        // Formulario nueva reseña
                        if (mostrarFormResena) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF5F5F5)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Tu puntuación:", fontWeight = FontWeight.Bold)
                                    // Estrellas interactivas
                                    Row {
                                        (1..5).forEach { estrella ->
                                            IconButton(
                                                onClick = { puntuacion = estrella.toFloat() },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (estrella <= puntuacion)
                                                        Icons.Default.Star
                                                    else Icons.Default.StarBorder,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFC107),
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                        Text(" ${puntuacion.toInt()}/5",
                                            modifier = Modifier.align(Alignment.CenterVertically))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = comentarioResena,
                                        onValueChange = { comentarioResena = it },
                                        label = { Text("Escribe tu comentario") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        maxLines = 4,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                val fecha = SimpleDateFormat(
                                                    "dd/MM/yyyy",
                                                    Locale.getDefault()
                                                ).format(Date())
                                                val resena = Resena(
                                                    puntuacion = puntuacion,
                                                    comentario = comentarioResena,
                                                    idUsuario = idUsuario,
                                                    idTaller = idTaller,
                                                    nombreUsuario = nombreUsuario,
                                                    fecha = fecha
                                                )
                                                resenaRepository.crearResena(resena)
                                                    .onSuccess {
                                                        mensaje = "✅ Reseña publicada"
                                                        comentarioResena = ""
                                                        puntuacion = 5f
                                                        mostrarFormResena = false
                                                        // Recargar reseñas
                                                        resenaRepository
                                                            .obtenerResenasDeTaller(idTaller)
                                                            .onSuccess { resenas = it }
                                                    }
                                            }
                                        },
                                        enabled = comentarioResena.isNotBlank(),
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Publicar reseña") }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        mensaje?.let {
                            Text(it, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                        }

                        // Lista de reseñas
                        if (resenas.isEmpty()) {
                            Text("Sin reseñas aún. ¡Sé el primero!",
                                color = Color.Gray, fontSize = 13.sp)
                        } else {
                            resenas.forEach { resena ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(resena.nombreUsuario,
                                                fontWeight = FontWeight.Bold)
                                            Text(resena.fecha,
                                                fontSize = 11.sp, color = Color.Gray)
                                        }
                                        // Estrellas
                                        Row {
                                            (1..5).forEach { i ->
                                                Icon(
                                                    imageVector = if (i <= resena.puntuacion)
                                                        Icons.Default.Star
                                                    else Icons.Default.StarBorder,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFC107),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(resena.comentario, fontSize = 13.sp)
                                        // Botón eliminar propia reseña
                                        if (resena.idUsuario == idUsuario) {
                                            TextButton(
                                                onClick = {
                                                    scope.launch {
                                                        resenaRepository
                                                            .eliminarResena(resena.idResena)
                                                        resenaRepository
                                                            .obtenerResenasDeTaller(idTaller)
                                                            .onSuccess { resenas = it }
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.Delete,
                                                    contentDescription = null,
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(14.dp))
                                                Text(" Eliminar mi reseña",
                                                    color = Color.Red, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}