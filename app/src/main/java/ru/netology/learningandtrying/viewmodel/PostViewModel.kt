package ru.netology.learningandtrying.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.netology.learningandtrying.dto.Post
import ru.netology.learningandtrying.model.FeedModel
import ru.netology.learningandtrying.repository.PostRepository
import ru.netology.learningandtrying.util.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.learningandtrying.R
import ru.netology.learningandtrying.auth.AppAuth
import ru.netology.learningandtrying.dto.FeedItem
import ru.netology.learningandtrying.model.FeedModelState
import ru.netology.learningandtrying.model.PhotoModel
import java.io.File
import javax.inject.Inject


private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    content = "",
    published = 0L,
    likedByMe = false,
    likes = 0
)
private val noPhoto = PhotoModel()

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val data: Flow<PagingData<FeedItem>> = appAuth.state
        .flatMapLatest { token ->
            repository.data.map { pagingData ->
                pagingData.map { post ->
                    if (post is Post) {
                        post.copy(ownedByMe = post.authorId == (token?.id ?: 0L))
                    } else {
                        post
                    }
                }
            }
        }.flowOn(Dispatchers.Default)

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    private val _postsCreated = SingleLiveEvent<Unit>()
    val postsCreated: LiveData<Unit>
        get() = _postsCreated

    init {
        load()
    }

    fun savePhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
    }

    fun removePhoto() {
        _photo.value = noPhoto
    }

    fun load() {
        _state.value = FeedModelState(loading = true)
        viewModelScope.launch {
            try {
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            edited.value?.let {
                try {
                    repository.save(it, _photo.value?.file)
                    _postsCreated.value = Unit
                } catch (e: Exception) {
                    _errorEvent.value = context.getString(R.string.network_error)
                }
            }
            edited.value = empty
        }
    }

    val edited = MutableLiveData(empty)
    val draft = MutableLiveData<String?>()
    fun likeById(id: Long, likedByMe: Boolean) {
        viewModelScope.launch {
            try {
                repository.likeById(id, likedByMe)
            } catch (e: Exception) {
                _errorEvent.value = context.getString(R.string.network_error)
            }
        }

    }

    fun shareById(id: Long) {
        TODO()
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _errorEvent.value = context.getString(R.string.network_error)
            }
        }
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun cancelEdit() {
        edited.value = empty
    }

    fun refresh() {
        _state.value = FeedModelState(refreshing = true)
        viewModelScope.launch {
            try {
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: LiveData<String>
        get() = _errorEvent
}