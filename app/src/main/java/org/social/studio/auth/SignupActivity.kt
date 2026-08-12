package org.social.studio.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.social.studio.databinding.ActivitySignupBinding
import org.social.studio.feed.VideoFeedActivity
import org.social.studio.manager.AppwriteManager

class SignupActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySignupBinding
    private lateinit var appwriteManager: AppwriteManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        appwriteManager = AppwriteManager(this)
        
        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password != confirmPassword) {
                Toast.makeText(this, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password.length < 8) {
                Toast.makeText(this, "كلمة المرور يجب أن تكون 8 أحرف على الأقل", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            registerUser(name, email, password)
        }
        
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun registerUser(name: String, email: String, password: String) {
        lifecycleScope.launch {
            binding.btnSignup.isEnabled = false
            binding.progressBar.visibility = android.view.View.VISIBLE
            
            val success = appwriteManager.registerUser(email, password, name)
            
            binding.btnSignup.isEnabled = true
            binding.progressBar.visibility = android.view.View.GONE
            
            if (success) {
                Toast.makeText(this@SignupActivity, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignupActivity, VideoFeedActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@SignupActivity, "فشل إنشاء الحساب، حاول مرة أخرى", Toast.LENGTH_LONG).show()
            }
        }
    }
}
