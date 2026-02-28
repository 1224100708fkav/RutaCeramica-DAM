package com.utng.rutaceramica.ui.screens.dueno

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
import com.utng.rutaceramica.data.model.Producto
import com.utng.rutaceramica.data.repository.ProductoRepository
import kotlinx.coroutines.launch

/**
 * Pantalla de CRUD completo de Productos para el Dueño del Taller.
 * Permite crear, ver, editar y eliminar productos artesanales.
 *
 * @param idTaller ID del taller cuyos productos se gestionan.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisProductosScreen(idTaller: String, onBack: () -> Unit) {
    val repository = remember { ProductoRepository() }
    val scope = rememberCoroutineScope()

    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoEditando by remember { mutableStateOf<Producto?>(null) }

    // Campos del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var tecnica by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    /** Carga o recarga la lista de productos del taller. */
    fun cargar() {
        scope.launch {
            cargando = true
            repository.obtenerProductosDeTaller(idTaller).onSuccess { productos = it }
            cargando = false
        }
    }

    /** Limpia los campos del formulario después de guardar o cancelar. */
    fun limpiarCampos() {
        nombre = ""; descripcion = ""; precio = ""
        material = ""; tecnica = ""; stock = ""
        productoEditando = null
    }

    LaunchedEffect(idTaller) { cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Productos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { limpiarCampos(); mostrarDialogo = true },
                containerColor = Color(0xFF1B5E20)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo producto", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
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
                            Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row {
                                IconButton(onClick = {
                                    productoEditando = producto
                                    nombre = producto.nombre
                                    descripcion = producto.descripcion
                                    precio = producto.precio.toString()
                                    material = producto.material
                                    tecnica = producto.tecnica
                                    stock = producto.stock.toString()
                                    mostrarDialogo = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar",
                                        tint = Color(0xFF1B5E20))
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        repository.eliminarProducto(producto.idProducto)
                                        cargar()
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                                        tint = Color.Red)
                                }
                            }
                        }
                        Text("${producto.material} · ${producto.tecnica}",
                            fontSize = 13.sp, color = Color.Gray)
                        Text("💰 $${producto.precio} | 📦 Stock: ${producto.stock}",
                            fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Diálogo para crear/editar producto
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false; limpiarCampos() },
            title = {
                Text(if (productoEditando != null) "Editar Producto" else "Nuevo Producto",
                    fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CampoTexto("Nombre *", nombre) { nombre = it }
                    CampoTexto("Descripción", descripcion, 2) { descripcion = it }
                    CampoTexto("Precio ($)", precio) { precio = it }
                    CampoTexto("Material", material) { material = it }
                    CampoTexto("Técnica", tecnica) { tecnica = it }
                    CampoTexto("Stock (unidades)", stock) { stock = it }
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val producto = Producto(
                            idProducto = productoEditando?.idProducto ?: "",
                            nombre = nombre,
                            descripcion = descripcion,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            material = material,
                            tecnica = tecnica,
                            stock = stock.toIntOrNull() ?: 0,
                            idTaller = idTaller
                        )
                        if (productoEditando != null) {
                            repository.actualizarProducto(producto)
                        } else {
                            repository.crearProducto(producto)
                        }
                        mostrarDialogo = false
                        limpiarCampos()
                        cargar()
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false; limpiarCampos() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}