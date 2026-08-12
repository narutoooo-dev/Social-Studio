package org.social.studio.manager

import android.content.Context
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.social.studio.models.VideoModel
import java.io.File

class AppwriteManager(private val context: Context) {
    
    private val client = Client(context)
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("YOUR_PROJECT_ID") // ⚠️ غيّر هذا لمعرف مشروعك
    
    val account = Account(client)
    val storage = Storage(client)
    val databases = Databases(client)
    
    companion object {
        const val DATABASE_ID = "social_studio_db"
        const val VIDEOS_COLLECTION = "videos"
        const val BUCKET_ID = "videos"
        
        // ⚠️ أنشئ هذه في لوحة Appwrite أولاً
    }
    
    // تسجيل مستخدم جديد
    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                account.create(
                    userId = ID.unique(),
                    email = email,
                    password = password,
                    name = name
                )
                account.createEmailPasswordSession(email, password)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    // تسجيل دخول
    suspend fun loginUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                account.createEmailPasswordSession(email, password)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    // تسجيل خروج
    suspend fun logoutUser(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                account.deleteSession("current")
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    // جلب جميع الفيديوهات
    suspend fun getVideos(): List<VideoModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = databases.listDocuments(
                    databaseId = DATABASE_ID,
                    collectionId = VIDEOS_COLLECTION,
                    queries = listOf(
                        Query.orderDesc("\$createdAt"),
                        Query.limit(50)
                    )
                )
                
                response.documents.map { doc ->
                    VideoModel(
                        id = doc.id,
                        videoUrl = doc.data["videoUrl"] as? String ?: "",
                        thumbnailUrl = doc.data["thumbnailUrl"] as? String ?: "",
                        userId = doc.data["userId"] as? String ?: "",
                        userName = doc.data["userName"] as? String ?: "",
                        description = doc.data["description"] as? String ?: "",
                        likes = (doc.data["likes"] as? Long)?.toInt() ?: 0,
                        comments = (doc.data["comments"] as? Long)?.toInt() ?: 0
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
    
    // حفظ معلومات الفيديو في قاعدة البيانات
    suspend fun saveVideoInfo(videoId: String, description: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val user = account.get()
                databases.createDocument(
                    databaseId = DATABASE_ID,
                    collectionId = VIDEOS_COLLECTION,
                    documentId = ID.unique(),
                    data = mapOf(
                        "videoId" to videoId,
                        "videoUrl" to "https://cloud.appwrite.io/v1/storage/buckets/$BUCKET_ID/files/$videoId/view?project=${client.projectId}",
                        "userId" to user.id,
                        "userName" to user.name,
                        "description" to description,
                        "likes" to 0,
                        "comments" to 0
                    )
                )
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
