package org.social.studio.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.social.studio.databinding.ActivityLoginBinding
import org.social.studio.feed.VideoFeedActivity
import org.social.studio.manager.AppwriteManager

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var appwriteManager: AppwriteManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        appwriteManager = AppwriteManager(this)
        
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            loginUser(email, password)
        }
        
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    
    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            binding.btnLogin.isEnabled = false
            binding.progressBar.visibility = android.view.View.VISIBLE
            
            val success = appwriteManager.loginUser(email, password)
            
            binding.btnLogin.isEnabled = true
            binding.progressBar.visibility = android.view.View.GONE
            
            if (success) {
                Toast.makeText(this@LoginActivity, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, VideoFeedActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "فشل تسجيل الدخول، تحقق من البريد وكلمة المرور", Toast.LENGTH_LONG).show()
            }
        }
    }
}
