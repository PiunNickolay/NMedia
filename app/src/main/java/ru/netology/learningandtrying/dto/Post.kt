package ru.netology.learningandtrying.dto

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val attachment: Attachment? = null,
    val authorAvatar: String = "",
    val ownedByMe: Boolean = false,
): FeedItem {
    var shareCount: Int = 0
    var viewCount: Int = 0
    var video: String? = null
}

data class Ad(
    override val id: Long,
    val image: String,
): FeedItem