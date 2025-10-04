package ru.netology.learningandtrying.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.learningandtrying.dto.FeedItem
import ru.netology.learningandtrying.dto.Post
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun likeById(id: Long, likedByMe: Boolean): Post
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post, photo: File?)
    suspend fun insertNewPosts(posts: List<Post>)
}