package ru.netology.learningandtrying.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.learningandtrying.api.PostApi
import ru.netology.learningandtrying.dto.PushToken
import ru.netology.learningandtrying.model.Token
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"
    private val _state = MutableStateFlow<Token?>(null)
    val state = _state.asStateFlow()

    init {
        val id = prefs.getLong(idKey, 0L)
        val token = prefs.getString(tokenKey, null)

        if (id == 0L || token == null) {
            prefs.edit() { clear() }
        } else {
            _state.value = Token(id = id, token = token)
        }

        sendPushToken()
    }

    @Synchronized
    fun saveAuth(id: Long, token: String) {
        prefs.edit() {
            putLong(idKey, id)
            putString(tokenKey, token)
        }
        _state.value = Token(id = id, token = token)
    }

    @Synchronized
    fun removeAuth() {
        prefs.edit() { clear() }
        _state.value = null
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntruPoint {
        fun getApiService(): PostApi
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntruPoint::class.java)
                entryPoint.getApiService().sendPushToken(
                    PushToken(
                        token = token ?: FirebaseMessaging.getInstance().token.await()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}