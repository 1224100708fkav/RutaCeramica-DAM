package com.utng.rutaceramica.ui.screens.turista

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.utng.rutaceramica.viewmodel.CarritoViewModel

/**
 * Pantalla que muestra los productos que el turista ha "apartado".
 * Permite gestionar las cantidades, eliminar productos y ver el total estimado.
 * Informa al usuario que la compra debe realizarse físicamente en la tienda.
 * 
 * @param viewModel ViewModel del carrito para acceder al estado global.
 * @param onBack Navegar hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val total = viewModel.calcularTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Apartados", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.vaciarCarrito() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Vaciar carrito", tint = Color.White)
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
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No tienes productos apartados.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onBack) {
                        Text("Ir a ver productos")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(items, key = { it.producto.idProducto }) { item ->
                        val producto = item.producto
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Imagen del producto
                                if (producto.fotoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = producto.fotoUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(end = 12.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(end = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) { Text("🏺", fontSize = 40.sp) }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        producto.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        "$${producto.precio} c/u",
                                        fontSize = 14.sp,
                                        color = Color(0xFF1A237E)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Control de cantidad
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.actualizarCantidad(producto.idProducto, item.cantidad - 1) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Menos")
                                        }
                                        Text(
                                            text = item.cantidad.toString(),
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(
                                            onClick = { viewModel.actualizarCantidad(producto.idProducto, item.cantidad + 1) },
                                            modifier = Modifier.size(32.dp),
                                            enabled = item.cantidad < producto.stock
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Más")
                                        }
                                        
                                        Spacer(modifier = Modifier.weight(1f))
                                        
                                        IconButton(
                                            onClick = { viewModel.eliminarDelCarrito(producto.idProducto) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Resumen y botón de finalizar (informativo)
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total estimado:", fontSize = 18.sp)
                            Text(
                                "$${String.format("%.2f", total)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFF9C4)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFBC02D))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Muestra esta lista en la tienda física para realizar tu compra.",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A237E)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Seguir viendo talleres", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}
