package io.github.null2264.githubuser.data.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.null2264.githubuser.data.common.AppRepository
import io.github.null2264.githubuser.data.common.GithubViewModel
import io.github.null2264.githubuser.data.common.Result
import io.github.null2264.githubuser.data.database.entity.UserEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailViewModel(private val repo: AppRepository, initUser: UserEntity) : GithubViewModel(repo) {
    private val _user = MutableLiveData(initUser)
    val user: LiveData<UserEntity> = _user

    private val _result = MutableLiveData<Result<Pair<List<UserEntity>, List<UserEntity>>>>()
    val result: LiveData<Result<Pair<List<UserEntity>, List<UserEntity>>>> = _result

    init {
        getFollows()
        refreshTarget()
    }

    fun isFavorite(userId: Int) = repo.observableFavorited(userId)

    private fun refreshTarget() {
        val user = _user.value!!
        viewModelScope.launch {
            repo.refreshTarget(user).collect {
                if (it is Result.Success)
                    _user.value = it.data
            }
        }
    }

    fun getFollows() {
        val user = _user.value!!
        if (user.followers < 1 && user.following < 1)
            return

        viewModelScope.launch {
            repo.getFollows(user).collect {
                _result.value = it
            }
        }
    }

    fun toggleFavorite(currentState: Boolean) {
        val user = _user.value!!
        viewModelScope.launch {
            if (!currentState)
                repo.addToFavorite(user.id)
            else
                repo.removeFromFavorite(user.id)
        }
    }
//
//    init {
//        getFollows()
//    }
//
//    fun getFollows() {
//        val u: User = user.value ?: return
//
//        if (u.followers < 1 && u.following < 1)
//            return
//
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                val resp = Apollo.getInstance(getToken()).query(UserDetailsQuery(u.username)).execute().data?.user
//                if (resp != null) {
//                    // If you have any idea on how to make this not duplicate, please let me this part driving me nuts
//                    _following.value = resp.following.nodes?.filterNotNull()?.map { user ->
//                        User(
//                            user.databaseId,
//                            user.login,
//                            user.name,
//                            user.avatarUrl as String,
//                            user.repositories.totalCount,
//                            user.followers.totalCount,
//                            user.following.totalCount,
//                            user.company,
//                            user.location
//                        )
//                    }
//                    _followers.value = resp.followers.nodes?.filterNotNull()?.map { user ->
//                        User(
//                            user.databaseId,
//                            user.login,
//                            user.name,
//                            user.avatarUrl as String,
//                            user.repositories.totalCount,
//                            user.followers.totalCount,
//                            user.following.totalCount,
//                            user.company,
//                            user.location
//                        )
//                    }
//                }
//            } catch (_: ApolloHttpException) {
//                _error.value = R.string.no_connection
//            } catch (exc: ApolloHttpException) {
//                var stringId = R.string.no_connection
//                if (exc.statusCode == 500)
//                    stringId = R.string.github_down
//                _error.value = stringId
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
}