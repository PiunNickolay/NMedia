package ru.netology.learningandtrying.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.learningandtrying.api.PostApi
import ru.netology.learningandtrying.dao.PostDao
import ru.netology.learningandtrying.dao.PostRemoteKeyDao
import ru.netology.learningandtrying.db.AppDb
import ru.netology.learningandtrying.dto.Attachment
import ru.netology.learningandtrying.dto.AttachmentType
import ru.netology.learningandtrying.dto.Media
import ru.netology.learningandtrying.dto.Post
import ru.netology.learningandtrying.entity.PostEntity
import ru.netology.learningandtrying.entity.fromDtoToEntity
import ru.netology.learningandtrying.error.AppError
import ru.netology.learningandtrying.error.NetworkError
import ru.netology.learningandtrying.error.UnknownError
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: PostApi,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { dao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            postApi = apiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb
        )
    ).flow.map { it.map(PostEntity::toDto) }

    override suspend fun likeById(id: Long, likedByMe: Boolean): Post {
        dao.likeById(id)
        return try {
            if (likedByMe) {
                apiService.dislikeById(id)
            } else {
                apiService.likeById(id)
            }
            dao.getById(id)?.toDto() ?: throw RuntimeException("Post not found locally")
        } catch (e: Exception) {
            dao.likeById(id)
            throw e
        }
    }

    override suspend fun removeById(id: Long) {
        val post = dao.getById(id) ?: return
        dao.removeById(id)
        try {
            apiService.delete(id)
        } catch (e: Exception) {
            dao.insert(post)
            throw e
        }
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun save(post: Post, photo: File?) {
        try {
            val media = photo?.let { saveMedia(it) }

            val postWithAttachment = media?.let {
                post.copy(
                    attachment = Attachment(it.id, AttachmentType.IMAGE)
                )
            } ?: post


            val posts = apiService.save(postWithAttachment)
            dao.insert(PostEntity.fromDto(posts))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun saveMedia(file: File): Media =
        apiService.uploadFile(
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                file.asRequestBody(),
            )
        )

    override suspend fun insertNewPosts(posts: List<Post>) {
        dao.insert(posts.fromDtoToEntity())
    }
}
