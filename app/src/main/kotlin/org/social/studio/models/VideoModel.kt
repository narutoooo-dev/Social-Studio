package org.social.studio.models

data class VideoModel(
    val id: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val userId: String = "",
    val userName: String = "",
    val description: String = "",
    val likes: Int = 0,
    val comments: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
