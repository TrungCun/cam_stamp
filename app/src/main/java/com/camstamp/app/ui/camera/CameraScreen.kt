package com.camstamp.app.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.camstamp.app.R

// Màn hình chính: chưa có quyền thì hiện nút xin quyền, có rồi thì hiện khung camera + nút đổi camera
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

    // State: đang chọn camera thứ mấy trong danh sách, và nhãn mô tả camera đang dùng
    // TODO: chuyển state này vào CameraViewModel khi làm zoom
    var cameraIndex by remember { mutableStateOf(0) }
    var cameraLabel by remember { mutableStateOf("...") }

    Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        if (hasPermission) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Khung 4:3 khớp tỉ lệ cảm biến -> không bị phóng to + cắt
                CameraPreview(
                    modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f),
                    cameraIndex = cameraIndex,
                    onCameraBound = { label -> cameraLabel = label }
                )
                Text(
                    // cameraLabel điền vào chỗ %1$s trong strings.xml
                    text = stringResource(R.string.current_camera, cameraLabel),
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = { cameraIndex++ },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(R.string.btn_switch_camera))
                }
            }
        } else {
            Column {
                Text(text = stringResource(R.string.permission_rationale))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text(text = stringResource(R.string.btn_request_permission))
                }
            }
        }
    }
}
