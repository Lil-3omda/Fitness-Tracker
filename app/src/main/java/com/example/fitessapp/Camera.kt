package com.example.fitessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Button
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
//import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import com.bumptech.glide.Glide
import androidx.lifecycle.LifecycleOwner
import org.json.JSONArray
import org.json.JSONException
import android.widget.TextView
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageCapture
import java.util.UUID
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent



class Camera : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var imageCapture: ImageCapture? = null
    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        previewView = findViewById(R.id.previewView)

        // Initialize cameraExecutor
        cameraExecutor = Executors.newSingleThreadExecutor()

        val btnCheckFood = findViewById<Button>(R.id.takePic)
        btnCheckFood.setOnClickListener {
            takePhoto()
        }
        if (allPermissionsGranted()) {
            Log.d(TAG, "Permissions granted")
            startCamera()
        } else {
            Log.d(TAG, "Permissions not granted, requesting permissions")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCamera() {
        Log.d(TAG, "startCamera() called")
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imageCapture = ImageCapture.Builder().build() // Initialize imageCapture

        preview.setSurfaceProvider(previewView.surfaceProvider)

        try {
            cameraProvider.unbindAll() // Clears any previous use cases
            cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }


    private fun takePhoto() {
        Log.d(TAG, "takePhoto() called")
        val imageCapture = imageCapture ?: run {
            Log.e(TAG, "ImageCapture not initialized")
            return
        }

        val photoDir = externalMediaDirs.firstOrNull()
        if (photoDir == null) {
            Log.e(TAG, "External media directory not found.")
            return
        }

        val photoFile = File(photoDir, "${System.currentTimeMillis()}.jpg")

        try {
            if (!photoFile.exists()) {
                val created = photoFile.createNewFile()
                if (!created) {
                    Log.e(TAG, "Failed to create photo file.")
                    return
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error creating photo file: ${e.message}", e)
            return
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "Image saved successfully: ${outputFileResults.savedUri}")
                    val capturedImage = photoFile.readBytes()
                    sendToApi(capturedImage)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun sendToApi(imageData: ByteArray) {
        Log.d(TAG, "Image sent to API")
        val apiURL = "https://api-inference.huggingface.co/models/nateraw/food"
        val headers = mapOf("Authorization" to "Bearer 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")

        CoroutineScope(Dispatchers.IO).launch {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(apiURL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/octet-stream")
                headers.forEach { (key, value) ->
                    connection.setRequestProperty(key, value)
                }
                connection.doOutput = true
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.write(imageData)
                outputStream.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseStream = connection.inputStream
                    val responseString = responseStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "API Response: $responseString")
                    // Display JSON response in the JsonResponse TextView
                    launchDisplayFoodInfoActivity(responseString)
                } else {
                    Log.e(TAG, "API request failed with response code $responseCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error writing image data: ${e.message}")
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun launchDisplayFoodInfoActivity(jsonResponse: String) {
        val intent = Intent(this@Camera, DisplayFoodInfo::class.java)
        intent.putExtra("jsonResponse", jsonResponse)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shutdown cameraExecutor
        cameraExecutor.shutdown()
    }

    private fun allPermissionsGranted() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}