package com.camstamp.app

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

const val TAG = "CamStamp"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraScreen()
        }
    }
}

// Man hinh chinh: chua co quyen thi hien nut xin quyen, co roi thi hien khung camera + nut doi camera
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

    // State: dang chon camera thu may trong danh sach, va nhan mo ta camera dang dung
    var cameraIndex by remember { mutableStateOf(0) }
    var cameraLabel by remember { mutableStateOf("...") }

    Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        if (hasPermission) {
            Column(modifier = Modifier.fillMaxSize()) {
                CameraPreview(
                    modifier = Modifier.weight(1f).fillMaxWidth().aspectRatio(3f / 4f),
                    cameraIndex = cameraIndex,
                    onCameraBound = { label -> cameraLabel = label }
                )
                Text(
                    text = "Dang dung: $cameraLabel",
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = { cameraIndex++ },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Doi camera")
                }
            }
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

// Khung hinh camera. cameraIndex doi -> gan lai camera khac.
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraIndex: Int,
    onCameraBound: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // "Nguoi quan ly camera", null cho den khi lay xong (bat dong bo)
    var provider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    // Use case Preview tao mot lan, dung lai khi doi camera
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
                    provider = p   // doi state -> LaunchedEffect ben duoi chay
                }, ContextCompat.getMainExecutor(ctx))
            }
        }
    )

    // Chay lai moi khi provider san sang hoac cameraIndex doi
    LaunchedEffect(provider, cameraIndex) {
        val p = provider ?: return@LaunchedEffect
        val label = bindCamera(p, lifecycleOwner, preview, cameraIndex)
        onCameraBound(label)
    }
}

// Chon camera thu `index` trong danh sach, gan voi Preview. Tra ve nhan mo ta.
fun bindCamera(
    provider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    preview: Preview,
    index: Int
): String {
    val infos = provider.availableCameraInfos
    val info = infos[index % infos.size]

    provider.unbindAll()
    // info.cameraSelector = bo loc tro dung ve camera nay
    provider.bindToLifecycle(lifecycleOwner, info.cameraSelector, preview)
    Log.i(TAG, "preview size = ${preview.resolutionInfo?.resolution}")  // Log thông tin độ phân giải của Preview nếu có

    val label = describe(info)
    Log.i(TAG, "bound -> $label")
    return label
}

// In danh sach camera ma CameraX nhin thay ra logcat
fun logCameras(provider: ProcessCameraProvider) {
    provider.availableCameraInfos.forEach { info ->
        Log.i(TAG, describe(info))
    }
}

// Mo ta mot camera: id, huong, tieu cu, khoang zoom.
// Camera2CameraInfo la cau noi xuong Camera2 de lay id va thong so chi tiet.
fun describe(info: CameraInfo): String {
    val c2 = Camera2CameraInfo.from(info)
    val focal = c2.getCameraCharacteristic(
        CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS
    )?.joinToString()
    val pixels = c2.getCameraCharacteristic(
        CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE
    )
    val zoom = info.zoomState.value
    val facing = if (info.lensFacing == CameraSelector.LENS_FACING_BACK) "BACK" else "FRONT"
    return "id=${c2.cameraId} $facing focal=${focal}mm zoom=${zoom?.minZoomRatio}..${zoom?.maxZoomRatio} pixels=$pixels"
}
