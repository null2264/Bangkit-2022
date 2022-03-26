package io.github.null2264.githubuser.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.null2264.githubuser.data.database.entity.FavoriteEntity
import io.github.null2264.githubuser.data.database.entity.FollowEntity
import io.github.null2264.githubuser.data.database.entity.RecentEntity
import io.github.null2264.githubuser.data.database.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT user.* FROM user JOIN recent ON recent.userId = user.id")
    fun observableRecent(): LiveData<List<UserEntity>>

    @Query("SELECT user.* FROM user JOIN recent ON recent.userId = user.id")
    suspend fun getRecent(): List<UserEntity>

    @Query("SELECT user.* FROM user JOIN favorite ON favorite.userId = user.id WHERE user.login LIKE :query OR user.name LIKE :query")
    fun getFilteredFavorites(query: String): LiveData<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowList(followList: List<FollowEntity>)

    @Query("SELECT user.* FROM user JOIN follow ON follow.followerId = user.id WHERE follow.userId = :id")
    suspend fun getUserFollowers(id: Int): List<UserEntity>

    @Query("SELECT user.* FROM user JOIN follow ON follow.userId = user.id WHERE follow.followerId = :id")
    suspend fun getUserFollowing(id: Int): List<UserEntity>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorite(user: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE userId = :userId")
    suspend fun removeFromFavorite(userId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentIds(ids: List<RecentEntity>)

    @Query("DELETE FROM user WHERE NOT EXISTS(SELECT * FROM user JOIN favorite ON favorite.userId = user.id WHERE userId = user.id)")
    suspend fun deleteAll()

    @Query("DELETE FROM recent")
    suspend fun clearRecent()

    @Query("SELECT EXISTS(SELECT * FROM user JOIN favorite ON favorite.userId = user.id WHERE userId = :id)")
    suspend fun isFavorited(id: Int): Boolean

    @Query("SELECT EXISTS(SELECT * FROM user JOIN favorite ON favorite.userId = user.id WHERE userId = :id)")
    fun observableFavorited(id: Int): LiveData<Boolean>
}