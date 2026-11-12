package com.camstamp.app.camera

import android.util.Log
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner

// Chọn camera thứ `index` trong danh sách, gắn với Preview. Trả về nhãn mô tả.
// TODO: chọn camera theo thông số (DEFAULT_BACK/FRONT_CAMERA) thay vì theo index
fun bindCamera(
    provider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    preview: Preview,
    index: Int
): String {
    val infos = provider.availableCameraInfos
    val info = infos[index % infos.size]

    provider.unbindAll()
    // info.cameraSelector = bộ lọc trỏ đúng về camera này
    provider.bindToLifecycle(lifecycleOwner, info.cameraSelector, preview)
    // Log độ phân giải thực tế của Preview (nếu có)
    Log.i(TAG, "preview size = ${preview.resolutionInfo?.resolution}")

    val label = describe(info)
    Log.i(TAG, "bound -> $label")
    return label
}
