package ru.netology.learningandtrying.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.learningandtrying.api.AuthApi
import ru.netology.learningandtrying.auth.AppAuth
import ru.netology.learningandtrying.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val authApi: AuthApi
) : ViewModel() {
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = SingleLiveEvent<String>()
    val error: LiveData<String> = _error

    private val _success = SingleLiveEvent<Unit>()
    val succes: LiveData<Unit> = _success

    fun signIn(login: String, pass: String) {
        if (login.isBlank() || pass.isBlank()) {
            _error.value = "Enter login and password"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            try {
                val response = authApi.auth(login, pass)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        appAuth.saveAuth(body.id, body.token)
                        _success.value = Unit
                    } else {
                        _error.value = "Empty server response"
                    }
                } else {
                    _error.value = "Server error ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }

}