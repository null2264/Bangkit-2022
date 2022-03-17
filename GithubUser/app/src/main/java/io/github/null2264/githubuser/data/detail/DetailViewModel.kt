package io.github.null2264.githubuser.data.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloHttpException
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.UserDetailsQuery
import io.github.null2264.githubuser.data.TokenViewModel
import io.github.null2264.githubuser.lib.User
import io.github.null2264.githubuser.lib.api.Apollo
import kotlinx.coroutines.launch

class DetailViewModel(token: String, private val user: User) : TokenViewModel(token) {
    private val _following = MutableLiveData<List<User>>()
    val following: LiveData<List<User>> = _following

    private val _followers = MutableLiveData<List<User>>()
    val followers: LiveData<List<User>> = _followers

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    init {
        getFollows()
    }

    fun getFollows() {
        if (user.followers < 1 && user.following < 1)
            return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val resp = Apollo.getInstance(getToken()).query(UserDetailsQuery(user.username)).execute().data?.user
                if (resp != null) {
                    // If you have any idea on how to make this not duplicate, please let me this part driving me nuts
                    _following.value = resp.following.nodes?.filterNotNull()?.map { user ->
                        User(
                            user.id,
                            user.login,
                            user.name,
                            user.avatarUrl as String,
                            user.repositories.totalCount,
                            user.followers.totalCount,
                            user.following.totalCount,
                            user.company,
                            user.location
                        )
                    }
                    _followers.value = resp.followers.nodes?.filterNotNull()?.map { user ->
                        User(
                            user.id,
                            user.login,
                            user.name,
                            user.avatarUrl as String,
                            user.repositories.totalCount,
                            user.followers.totalCount,
                            user.following.totalCount,
                            user.company,
                            user.location
                        )
                    }
                }
            } catch (_: ApolloHttpException) {
                _error.value = R.string.no_connection
            } finally {
                _isLoading.value = false
            }
        }
    }
}