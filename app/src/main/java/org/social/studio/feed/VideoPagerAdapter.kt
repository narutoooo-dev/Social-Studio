package org.social.studio.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import org.social.studio.databinding.ItemVideoBinding
import org.social.studio.models.VideoModel

class VideoPagerAdapter(
    private val videoList: List<VideoModel>,
    private val context: Context
) : RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder>() {
    
    private val players = mutableMapOf<Int, ExoPlayer>()
    private var currentPlayingPosition = -1
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videoList[position]
        holder.bind(video)
        
        // إنشاء مشغل للفيديو
        val player = ExoPlayer.Builder(context).build()
        players[position] = player
        holder.setPlayer(player, video.videoUrl)
    }
    
    override fun getItemCount(): Int = videoList.size
    
    fun playVideoAtPosition(position: Int) {
        // إيقاف الفيديو السابق
        if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
            players[currentPlayingPosition]?.pause()
        }
        
        currentPlayingPosition = position
        players[position]?.play()
    }
    
    fun pauseAllVideos() {
        players.values.forEach { it.pause() }
    }
    
    fun releaseAllPlayers() {
        players.values.forEach { it.release() }
        players.clear()
    }
    
    inner class VideoViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(video: VideoModel) {
            binding.tvUserName.text = video.userName
            binding.tvDescription.text = video.description
            binding.tvLikes.text = "${video.likes}"
            binding.tvComments.text = "${video.comments}"
        }
        
        fun setPlayer(player: ExoPlayer, videoUrl: String) {
            binding.playerView.player = player
            // هنا هتضبط الـ MediaItem
        }
    }
}
