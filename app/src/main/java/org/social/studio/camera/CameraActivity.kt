package org.social.studio.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.social.studio.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCameraBinding
    private var videoCapture: VideoCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isRecording = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSIONS,
                CAMERA_REQUEST_CODE
            )
        }
        
        binding.btnRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
        
        binding.btnClose.setOnClickListener {
            finish()
        }
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            
            videoCapture = VideoCapture.Builder()
                .setVideoFrameRate(30)
                .build()
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, videoCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun startRecording() {
        if (videoCapture == null) return
        
        val videoFile = File(
            externalMediaDirs.firstOrNull()?.absolutePath ?: cacheDir.absolutePath,
            "video_${System.currentTimeMillis()}.mp4"
        )
        
        videoCapture?.startRecording(
            VideoCapture.OutputFileOptions.Builder(videoFile).build(),
            ContextCompat.getMainExecutor(this),
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    Toast.makeText(this@CameraActivity, "تم تسجيل الفيديو", Toast.LENGTH_SHORT).show()
                    isRecording = false
                    binding.btnRecord.text = "⏺ تسجيل"
                    // إعادة الفيديو للشاشة السابقة
                }
                
                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Toast.makeText(this@CameraActivity, "فشل التسجيل: $message", Toast.LENGTH_SHORT).show()
                    isRecording = false
                    binding.btnRecord.text = "⏺ تسجيل"
                }
            }
        )
        
        isRecording = true
        binding.btnRecord.text = "⏹ إيقاف"
    }
    
    private fun stopRecording() {
        videoCapture?.stopRecording()
        isRecording = false
        binding.btnRecord.text = "⏺ تسجيل"
    }
    
    private fun allPermissionsGranted() = CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "لابد من منح الصلاحيات", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    
    companion object {
        private val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        private const val CAMERA_REQUEST_CODE = 100
    }
}
