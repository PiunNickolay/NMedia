package ru.netology.learningandtrying.model

import android.content.Context
import okio.IOException
import retrofit2.HttpException
import ru.netology.learningandtrying.R
import ru.netology.learningandtrying.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)

data class FeedModelState(
    val error: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
)