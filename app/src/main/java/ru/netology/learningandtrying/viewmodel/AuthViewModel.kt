package ru.netology.learningandtrying.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import ru.netology.learningandtrying.auth.AppAuth
import ru.netology.learningandtrying.model.Token
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {
    val state: LiveData<Boolean> = appAuth
        .state
        .map { it != null }
        .asLiveData(Dispatchers.Default)

}