package com.camstamp.app.camera

import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider

// NOTE: TAG dùng chung cho logcat, lọc bằng: adb logcat -s CamStamp
const val TAG = "CamStamp"

// In danh sách camera mà CameraX nhìn thấy ra logcat
fun logCameras(provider: ProcessCameraProvider) {
    provider.availableCameraInfos.forEach { info ->
        Log.i(TAG, describe(info))
    }
}

// Mô tả một camera: id, hướng, tiêu cự, khoảng zoom, số pixel cảm biến.
// Camera2CameraInfo là cầu nối xuống Camera2 để lấy id và thông số chi tiết.
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
