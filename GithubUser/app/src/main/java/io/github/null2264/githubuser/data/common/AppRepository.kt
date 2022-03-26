package io.github.null2264.githubuser.data.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.UserDetailsQuery
import io.github.null2264.githubuser.UserFollowsQuery
import io.github.null2264.githubuser.UserSearchQuery
import io.github.null2264.githubuser.UserSearchQuery.Data.Search.Node.Companion.asUser
import io.github.null2264.githubuser.data.database.UserDao
import io.github.null2264.githubuser.data.database.entity.FavoriteEntity
import io.github.null2264.githubuser.data.database.entity.FollowEntity
import io.github.null2264.githubuser.data.database.entity.RecentEntity
import io.github.null2264.githubuser.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class AppRepository private constructor(
    private val apollo: ApolloClient,
    private val dao: UserDao,
) {
    fun observableFavorited(userId: Int) = dao.observableFavorited(userId)

    suspend fun addToFavorite(userId: Int) {
        dao.addToFavorite(FavoriteEntity(0, userId))
    }

    suspend fun removeFromFavorite(userId: Int) {
        dao.removeFromFavorite(userId)
    }

    fun getFavorites(query: String?): LiveData<Result<List<UserEntity>>> = liveData {
        emit(Result.Loading)

        val rt = dao.getFilteredFavorites(if (query == null) "%" else "%$query%").map {
            if (it.isEmpty()) {
                if (query == null)
                    Result.Error(0, null)
                else
                    Result.Error(R.string.no_users_prefix, " '$query'")
            } else
                Result.Success(it)
        }
        emitSource(rt)
    }

    fun refreshTarget(user: UserEntity): Flow<Result<UserEntity>> = flow {
        emit(Result.Loading)

        try {
            apollo.query(UserDetailsQuery(user.login)).toFlow().collect { resp ->
                val respUser = resp.data?.user
                if (respUser?.databaseId != null) {
                    val updatedUser = UserEntity(
                        respUser.databaseId,
                        respUser.login,
                        respUser.name,
                        respUser.avatarUrl as String,
                        respUser.followers.totalCount,
                        respUser.following.totalCount,
                        respUser.company,
                        respUser.location,
                        respUser.repositories.totalCount,
                    )
                    dao.updateUser(updatedUser)
                    emit(Result.Success(updatedUser))
                }
            }
        } catch (_: ApolloNetworkException) {
            emit(Result.Error(R.string.no_connection, null))
        } catch (exc: ApolloHttpException) {
            var stringId = R.string.no_connection
            if (exc.statusCode == 500)
                stringId = R.string.github_down
            emit(Result.Error(stringId, null))
        }
    }

    fun getFollows(user: UserEntity): Flow<Result<Pair<List<UserEntity>, List<UserEntity>>>> = flow {
        emit(Result.Loading)

        try {
            val followList = ArrayList<FollowEntity>()
            val followers = ArrayList<UserEntity>()
            val following = ArrayList<UserEntity>()
            apollo.query(UserFollowsQuery(user.login)).toFlow().collect { resp ->
                val respUser = resp.data?.user
                if (respUser?.databaseId != null) {
                    // I hate this, can someone tell me how to not have this duplicate codes please!
                    respUser.followers.nodes?.forEach {
                        if (it?.databaseId != null) {
                            followers.add(UserEntity(
                                it.databaseId,
                                it.login,
                                it.name,
                                it.avatarUrl as String,
                                it.followers.totalCount,
                                it.following.totalCount,
                                it.company,
                                it.location,
                                it.repositories.totalCount,
                            ))
                            followList.add(FollowEntity(0, respUser.databaseId, it.databaseId))
                        }
                    }
                    respUser.following.nodes?.forEach {
                        if (it?.databaseId != null) {
                            following.add(UserEntity(
                                it.databaseId,
                                it.login,
                                it.name,
                                it.avatarUrl as String,
                                it.followers.totalCount,
                                it.following.totalCount,
                                it.company,
                                it.location,
                                it.repositories.totalCount,
                            ))
                            followList.add(FollowEntity(0, it.databaseId, respUser.databaseId))
                        }
                    }
                }
            }
            dao.insertUsers(followers + following)
            dao.insertFollowList(followList)
        } catch (_: ApolloNetworkException) {
            emit(Result.Error(R.string.no_connection, null))
        } catch (exc: ApolloHttpException) {
            var stringId = R.string.no_connection
            if (exc.statusCode == 500)
                stringId = R.string.github_down
            emit(Result.Error(stringId, null))
        }
        val localFollowers = dao.getUserFollowers(user.id)
        val localFollowing = dao.getUserFollowing(user.id)
        val rt = Pair(localFollowers, localFollowing)
        emit(Result.Success(rt))
    }

    fun getUsers(query: String?): LiveData<Result<List<UserEntity>>> = liveData {
        emit(Result.Loading)

        if (query != null)
            try {
                val userList = ArrayList<UserEntity>()
                apollo.query(UserSearchQuery(query)).toFlow().collect { resp ->
                    val search = resp.data?.search
                    search?.nodes?.forEach {
                        val user = it?.asUser()
                        if (user?.databaseId != null) {
                            userList.add(UserEntity(
                                user.databaseId,
                                user.login,
                                user.name,
                                user.avatarUrl as String,
                                user.followers.totalCount,
                                user.following.totalCount,
                                user.company,
                                user.location,
                                user.repositories.totalCount,
                            ))
                        }
                    }
                    dao.deleteAll()
                    dao.clearRecent()
                    dao.insertUsers(userList)
                    dao.insertRecentIds(userList.map { RecentEntity(null, it.id) })
                }
                val localData = dao.observableRecent().map {
                    if (it.isNotEmpty())
                        Result.Success(it)
                    else
                        Result.Error(R.string.no_users_prefix, " '$query'")
                }
                emitSource(localData)
            } catch (_: ApolloNetworkException) {
                emit(Result.Error(R.string.no_connection, null))
            } catch (exc: ApolloHttpException) {
                var stringId = R.string.no_connection
                if (exc.statusCode == 500)
                    stringId = R.string.github_down
                emit(Result.Error(stringId, null))
            }
        else {
            val localData = dao.observableRecent().map {
                if (it.isNotEmpty())
                    Result.Success(it)
                else
                    Result.Error(R.string.greeting, "")
            }
            emitSource(localData)
        }
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null

        @JvmStatic
        fun getInstance(
            apollo: ApolloClient,
            dao: UserDao,
        ): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(apollo, dao)
            }.also { instance = it }
    }
}