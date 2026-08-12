package org.social.studio.feed

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import org.social.studio.databinding.ActivityVideoFeedBinding
import org.social.studio.manager.AppwriteManager
import org.social.studio.models.VideoModel

class VideoFeedActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoFeedBinding
    private lateinit var appwriteManager: AppwriteManager
    private lateinit var adapter: VideoPagerAdapter
    private var videoList = mutableListOf<VideoModel>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        appwriteManager = AppwriteManager(this)
        
        setupViewPager()
        loadVideos()
        
        // زر رفع فيديو
        binding.fabUpload.setOnClickListener {
            // فتح شاشة الكاميرا
            Toast.makeText(this, "سيتم فتح الكاميرا قريباً", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupViewPager() {
        adapter = VideoPagerAdapter(videoList, this)
        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        
        // تشغيل الفيديو عند التمرير
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.playVideoAtPosition(position)
            }
        })
    }
    
    private fun loadVideos() {
        lifecycleScope.launch {
            try {
                val videos = appwriteManager.getVideos()
                if (videos.isNotEmpty()) {
                    videoList.clear()
                    videoList.addAll(videos)
                    adapter.notifyDataSetChanged()
                    
                    // تشغيل أول فيديو
                    if (videoList.isNotEmpty()) {
                        adapter.playVideoAtPosition(0)
                    }
                } else {
                    Toast.makeText(this@VideoFeedActivity, "لا توجد فيديوهات حالياً", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VideoFeedActivity, "فشل تحميل الفيديوهات", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        adapter.pauseAllVideos()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        adapter.releaseAllPlayers()
    }
}
