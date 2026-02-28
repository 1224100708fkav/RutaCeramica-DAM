package com.utng.rutaceramica.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utng.rutaceramica.viewmodel.AuthViewModel

/**
 * Pantalla de inicio de sesión de la aplicación.
 * Permite al usuario ingresar su correo y contraseña para autenticarse.
 * Tras el login exitoso, consulta el rol en Firestore y notifica al NavGraph
 * para redirigir a la pantalla correcta.
 *
 * @param authViewModel ViewModel que maneja la lógica de autenticación.
 * @param onLoginExitoso Callback que recibe el rol del usuario para redirigir.
 * @param onIrARegistro Callback para navegar a la pantalla de registro.
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginExitoso: (String) -> Unit,
    onIrARegistro: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val cargando by authViewModel.cargando.collectAsState()
    val error by authViewModel.error.collectAsState()
    val usuario by authViewModel.usuario.collectAsState()

    // Observar cuando el usuario se loguea exitosamente
    LaunchedEffect(usuario) {
        usuario?.let {
            onLoginExitoso(it.rol)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Azul oscuro
                        Color(0xFF283593)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "🏺 Ruta Cerámica",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = "Dolores Hidalgo, Gto.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Mensaje de error
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                // Botón de Login
                Button(
                    onClick = {
                        authViewModel.login(email.trim(), password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !cargando && email.isNotBlank() && password.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A237E)
                    )
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Ir a Registro
                TextButton(onClick = onIrARegistro) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
                        color = Color(0xFF1A237E)
                    )
                }
            }
        }
    }
}