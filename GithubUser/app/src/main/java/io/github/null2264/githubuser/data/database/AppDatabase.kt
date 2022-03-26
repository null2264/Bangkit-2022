package io.github.null2264.githubuser.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.null2264.githubuser.data.database.entity.FavoriteEntity
import io.github.null2264.githubuser.data.database.entity.FollowEntity
import io.github.null2264.githubuser.data.database.entity.RecentEntity
import io.github.null2264.githubuser.data.database.entity.UserEntity

@Database(
    entities = [
        FavoriteEntity::class,
        FollowEntity::class,
        RecentEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "github_user.db"
                ).build()
            }.also { instance = it }
    }
}