package org.social.studio.upload

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.social.studio.databinding.ActivityUploadBinding
import org.social.studio.manager.AppwriteManager
import org.social.studio.manager.VideoUploader

class UploadActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUploadBinding
    private lateinit var appwriteManager: AppwriteManager
    private lateinit var videoUploader: VideoUploader
    private var videoUri: Uri? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        appwriteManager = AppwriteManager(this)
        videoUploader = VideoUploader(appwriteManager)
        
        videoUri = intent.getParcelableExtra("video_uri")
        
        binding.btnUpload.setOnClickListener {
            val description = binding.etDescription.text.toString().trim()
            if (description.isEmpty()) {
                Toast.makeText(this, "يرجى كتابة وصف للفيديو", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadVideo(description)
        }
    }
    
    private fun uploadVideo(description: String) {
        lifecycleScope.launch {
            binding.btnUpload.isEnabled = false
            binding.progressBar.visibility = android.view.View.VISIBLE
            
            try {
                val videoFile = videoUri?.let { 
                    videoUploader.uriToFile(it, contentResolver)
                }
                
                if (videoFile == null) {
                    Toast.makeText(this@UploadActivity, "فشل تحويل الفيديو", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val videoId = videoUploader.uploadVideo(videoFile)
                
                if (videoId != null) {
                    val success = appwriteManager.saveVideoInfo(videoId, description)
                    if (success) {
                        Toast.makeText(this@UploadActivity, "تم رفع الفيديو بنجاح", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@UploadActivity, "فشل حفظ معلومات الفيديو", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UploadActivity, "فشل رفع الفيديو", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UploadActivity, "حدث خطأ: ${e.message}", Toast.LENGTH_LONG).show()
            }
            
            binding.btnUpload.isEnabled = true
            binding.progressBar.visibility = android.view.View.GONE
        }
    }
}
