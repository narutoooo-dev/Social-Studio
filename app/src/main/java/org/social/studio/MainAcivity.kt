package org.social.studio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.social.studio.auth.LoginActivity
import org.social.studio.feed.VideoFeedActivity
import org.social.studio.manager.AppwriteManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var appwriteManager: AppwriteManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        appwriteManager = AppwriteManager(this)
        
        // التحقق من حالة تسجيل الدخول
        lifecycleScope.launch {
            try {
                val user = appwriteManager.account.get()
                if (user.id.isNotEmpty()) {
                    startActivity(Intent(this@MainActivity, VideoFeedActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
