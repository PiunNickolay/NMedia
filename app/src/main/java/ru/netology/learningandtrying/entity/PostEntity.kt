package ru.netology.learningandtrying.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.learningandtrying.dto.Attachment
import ru.netology.learningandtrying.dto.Post


@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shareCount: Int = 0,
    val viewCount: Int = 0,
    val video: String? = null,
    @Embedded
    val attachment: Attachment?,
) {
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        attachment = attachment,
    ).apply {
        shareCount = this@PostEntity.shareCount
        viewCount = this@PostEntity.viewCount
        video = this@PostEntity.video
    }

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.authorId,
            post.author,
            post.authorAvatar,
            post.content,
            post.published,
            post.likedByMe,
            post.likes,
            post.shareCount,
            post.viewCount,
            post.video,
            post.attachment
        )
    }
}
fun List<PostEntity>.toDto() = map(PostEntity::toDto)
fun List<Post>.fromDtoToEntity() = map{PostEntity.fromDto(it)}
