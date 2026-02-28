package com.utng.rutaceramica.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utng.rutaceramica.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla de inicio (Splash) con animaciones avanzadas.
 * Presenta un diseño visualmente atractivo con elementos en movimiento.
 * 
 * @param authViewModel ViewModel para verificar la sesión del usuario.
 * @param onNavigation Finalizar animación e ir a la siguiente pantalla.
 */
@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onNavigation: (String) -> Unit
) {
    // ── ESTADOS DE ANIMACIÓN ──────────────────────────────────────
    
    // Escala del logo central (Pulsante)
    val scale = remember { Animatable(0f) }
    
    // Opacidad de los textos (Fade In)
    val alpha = remember { Animatable(0f) }
    
    // Rotación suave del logo
    val rotation = rememberInfiniteTransition(label = "rotation")
    val angle by rotation.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "angle"
    )

    // Animación de partículas de fondo (Burbujas flotantes)
    val bubbleTransition = rememberInfiniteTransition(label = "bubbles")
    val bubbleOffset by bubbleTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    // ── LÓGICA DE NAVEGACIÓN ──────────────────────────────────────
    
    LaunchedEffect(Unit) {
        // Iniciar animaciones secuenciales
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
        alpha.animateTo(1f, animationSpec = tween(1000))
        
        // Esperar un momento para lucir el diseño
        delay(2500)
        
        // Verificar sesión y navegar
        authViewModel.verificarSesion()
        val usuario = authViewModel.usuario.value
        if (usuario != null) {
            val destino = when (usuario.rol) {
                "turista" -> "home_turista"
                "dueno" -> "home_dueno"
                "admin" -> "home_admin"
                else -> "home_turista"
            }
            onNavigation(destino)
        } else {
            onNavigation("login")
        }
    }

    // ── DISEÑO DE LA PANTALLA ─────────────────────────────────────
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Azul profundo
                        Color(0xFF3F51B5), // Azul vibrante
                        Color(0xFF1A237E)  // Azul profundo
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Partículas flotantes de fondo
        repeat(8) { index ->
            FloatingBubble(
                modifier = Modifier.offset(
                    x = (index * 50 - 150).dp,
                    y = (bubbleOffset + (index * 40)).dp
                ),
                size = (15 + (index * 8)).dp,
                opacity = 0.05f + (index * 0.03f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Icono principal animado
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .graphicsLayer { rotationZ = angle },
                contentAlignment = Alignment.Center
            ) {
                // Brillo circular de fondo para el logo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                )
                
                Text(
                    text = "🏺",
                    fontSize = 110.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre de la App con Fade In
            Text(
                text = "Ruta Cerámica",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.alpha(alpha.value),
                letterSpacing = 2.sp
            )

            Text(
                text = "DOLORES HIDALGO",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alpha.value),
                letterSpacing = 6.sp
            )
        }
        
        // Indicador de carga sutil al fondo
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(alpha.value)
        ) {
            Text(
                text = "Cargando tradiciones...",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

/**
 * Elemento decorativo flotante para el fondo.
 */
@Composable
fun FloatingBubble(
    modifier: Modifier,
    size: Dp,
    opacity: Float
) {
    Box(
        modifier = modifier
            .size(size)
            .background(Color.White.copy(alpha = opacity), CircleShape)
    )
}
