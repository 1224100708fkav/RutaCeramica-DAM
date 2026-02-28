package com.utng.rutaceramica.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
 * Pantalla de registro de nuevos usuarios.
 * Solicita nombre, correo y contraseña. El rol por defecto es "turista".
 *
 * @param authViewModel ViewModel de autenticación.
 * @param onRegistroExitoso Callback al completar el registro exitosamente.
 * @param onIrALogin Callback para regresar a la pantalla de login.
 */
@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,
    onRegistroExitoso: () -> Unit,
    onIrALogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    val cargando by authViewModel.cargando.collectAsState()
    val error by authViewModel.error.collectAsState()
    val usuario by authViewModel.usuario.collectAsState()

    LaunchedEffect(usuario) {
        usuario?.let { onRegistroExitoso() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A237E), Color(0xFF283593))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Crear Cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = confirmarPassword,
                    onValueChange = { confirmarPassword = it },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = confirmarPassword.isNotEmpty() && password != confirmarPassword
                )

                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        if (password == confirmarPassword) {
                            authViewModel.registrar(nombre.trim(), email.trim(), password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !cargando
                            && nombre.isNotBlank()
                            && email.isNotBlank()
                            && password.length >= 6
                            && password == confirmarPassword,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    if (cargando) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onIrALogin) {
                    Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFF1A237E))
                }
            }
        }
    }
}