package ru.netology.learningandtrying.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.learningandtrying.dao.PostDao
import ru.netology.learningandtrying.dao.PostRemoteKeyDao
import ru.netology.learningandtrying.entity.PostEntity
import ru.netology.learningandtrying.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract val postDao: PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}

