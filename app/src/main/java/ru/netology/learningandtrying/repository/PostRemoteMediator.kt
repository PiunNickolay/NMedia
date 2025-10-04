package ru.netology.learningandtrying.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.learningandtrying.api.PostApi
import ru.netology.learningandtrying.dao.PostDao
import ru.netology.learningandtrying.dao.PostRemoteKeyDao
import ru.netology.learningandtrying.db.AppDb
import ru.netology.learningandtrying.entity.PostEntity
import ru.netology.learningandtrying.entity.PostRemoteKeyEntity
import ru.netology.learningandtrying.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postApi: PostApi,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id == null) {
                        postApi.getLatest(state.config.pageSize)
                    } else {
                        postApi.getAfter(id, state.config.pageSize)
                    }
                }


                LoadType.PREPEND -> {
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    postApi.getAfter(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    postApi.getBefore(id, state.config.pageSize)
                }

            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )

            appDb.withTransaction {
                if (body.isNotEmpty()) {
                    when (loadType) {
                        LoadType.REFRESH -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    body.first().id
                                )
                            )
                            if (postRemoteKeyDao.min() == null) {
                                postRemoteKeyDao.insert(
                                    PostRemoteKeyEntity(
                                        PostRemoteKeyEntity.KeyType.BEFORE,
                                        body.last().id
                                    )
                                )
                            }
                        }

                        LoadType.APPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        }

                        LoadType.PREPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    body.first().id
                                )
                            )
                        }
                    }

                    postDao.insert(body.map { PostEntity.fromDto(it) })
                }
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}