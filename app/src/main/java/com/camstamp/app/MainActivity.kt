package com.camstamp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.camstamp.app.ui.camera.CameraScreen

// Điểm vào của app: Activity chỉ làm một việc là hiện màn hình camera
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraScreen()
        }
    }
}
