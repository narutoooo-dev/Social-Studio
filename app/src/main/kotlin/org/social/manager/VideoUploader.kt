package org.social.studio.manager

import android.content.ContentResolver
import android.net.Uri
import io.appwrite.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class VideoUploader(private val manager: AppwriteManager) {
    
    suspend fun uploadVideo(videoFile: File): String? {
        return withContext(Dispatchers.IO) {
            try {
                val result = manager.storage.createFile(
                    bucketId = AppwriteManager.BUCKET_ID,
                    fileId = ID.unique(),
                    file = videoFile
                )
                result.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    // تحويل Uri إلى File
    fun uriToFile(uri: Uri, contentResolver: ContentResolver): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("video_", ".mp4")
            tempFile.deleteOnExit()
            
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
