package com.camstamp.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraScreen()
        }
    }
}

// Man hinh chinh: chua co quyen thi hien nut xin quyen, co roi thi hien khung camera
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        hasPermission = granted
    }

    Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        if (hasPermission) {
            CameraPreview(modifier = Modifier.fillMaxSize())
        } else {
            Column {
                Text(text = "Camera permission is required to use this app.")
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text(text = "Request Camera Permission")
                }
            }
        }
    }
}

// Khung hinh camera: dat PreviewView (View kieu cu) vao Compose qua AndroidView
@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    // Vong doi cua man hinh, de CameraX tu bat/tat camera
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).also { previewView ->
                // Xin "nguoi quan ly camera" cua app; viec nay mat mot luc nen dung future
                val future = ProcessCameraProvider.getInstance(ctx)
                future.addListener({
                    val cameraProvider = future.get()

                    // Use case Preview: hien khung hinh len previewView
                    val preview = Preview.Builder().build()
                    preview.surfaceProvider = previewView.surfaceProvider

                    // Go moi rang buoc cu (neu co) roi gan lai: camera sau + Preview + vong doi
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview
                    )
                }, ContextCompat.getMainExecutor(ctx))
            }
        }
    )
}
