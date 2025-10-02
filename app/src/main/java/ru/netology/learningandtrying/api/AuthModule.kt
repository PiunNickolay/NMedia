package ru.netology.learningandtrying.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthModule = retrofit.create(AuthModule::class.java)
}