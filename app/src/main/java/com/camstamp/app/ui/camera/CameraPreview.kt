package com.camstamp.app.ui.camera

import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.camstamp.app.camera.bindCamera
import com.camstamp.app.camera.logCameras

// Khung hình camera. cameraIndex đổi -> gắn lại camera khác.
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraIndex: Int,
    onCameraBound: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // "Người quản lý camera", null cho đến khi lấy xong (bất đồng bộ)
    var provider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    // Use case Preview tạo một lần, dùng lại khi đổi camera
    // [ ] Hoãn lại: thêm ResolutionSelector 4:3 (xem docs/TODO.md)
    val preview = remember { Preview.Builder().build() }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).also { previewView ->
                preview.surfaceProvider = previewView.surfaceProvider
                val future = ProcessCameraProvider.getInstance(ctx)
                future.addListener({
                    val p = future.get()
                    logCameras(p)
                    provider = p   // đổi state -> LaunchedEffect bên dưới chạy
                }, ContextCompat.getMainExecutor(ctx))
            }
        }
    )

    // Chạy lại mỗi khi provider sẵn sàng hoặc cameraIndex đổi
    LaunchedEffect(provider, cameraIndex) {
        val p = provider ?: return@LaunchedEffect
        val label = bindCamera(p, lifecycleOwner, preview, cameraIndex)
        onCameraBound(label)
    }
}
