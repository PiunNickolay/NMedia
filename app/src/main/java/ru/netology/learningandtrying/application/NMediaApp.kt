package ru.netology.learningandtrying.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.learningandtrying.auth.AppAuth

@HiltAndroidApp
class NMediaApp: Application() {
}