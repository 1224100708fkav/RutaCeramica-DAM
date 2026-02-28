package com.utng.rutaceramica.utils

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Clase helper que encapsula toda la lógica de CameraX.
 * Permite inicializar la cámara, mostrar el preview en pantalla
 * y capturar fotos guardándolas en el almacenamiento del dispositivo.
 *
 * @param context Contexto de la aplicación o actividad.
 */
class CamaraUtils(private val context: Context) {

    /** Caso de uso de captura de imagen de CameraX. */
    private var imageCapture: ImageCapture? = null

    /** Executor para operaciones de cámara en hilo secundario. */
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    /**
     * Inicia el proveedor de cámara y configura los casos de uso:
     * Preview (para mostrar en pantalla) e ImageCapture (para tomar fotos).
     *
     * @param lifecycleOwner Owner del ciclo de vida (Activity o Fragment).
     * @param surfaceProvider Proveedor de superficie del PreviewView de Compose.
     */
    fun iniciarCamara(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider
    ) {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = surfaceProvider
                }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Captura una foto y la guarda en el directorio de la aplicación.
     * El nombre del archivo incluye la fecha y hora actuales para evitar duplicados.
     *
     * @param onFotoCapturada Callback que recibe el [Uri] de la foto guardada exitosamente.
     * @param onError Callback que recibe un mensaje de error si la captura falla.
     */
    fun tomarFoto(
        onFotoCapturada: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val imageCaptureLocal = imageCapture ?: run {
            onError("La cámara no está inicializada")
            return
        }

        // Crear archivo con nombre basado en timestamp
        val nombre = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
            .format(System.currentTimeMillis())
        val fotoFile = File(
            context.filesDir,
            "bitacora_${nombre}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(fotoFile).build()

        imageCaptureLocal.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = Uri.fromFile(fotoFile)
                    onFotoCapturada(uri)
                }

                override fun onError(exception: ImageCaptureException) {
                    onError("Error al capturar: ${exception.message}")
                }
            }
        )
    }

    /**
     * Libera los recursos del executor de la cámara.
     * Debe llamarse en el onDestroy de la Activity o en el onCleared del ViewModel.
     */
    fun liberar() {
        cameraExecutor.shutdown()
    }
}