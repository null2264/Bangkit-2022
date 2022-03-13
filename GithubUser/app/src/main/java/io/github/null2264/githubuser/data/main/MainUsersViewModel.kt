package io.github.null2264.githubuser.data.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloNetworkException
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.UserSearchQuery
import io.github.null2264.githubuser.data.TokenViewModel
import io.github.null2264.githubuser.lib.User
import io.github.null2264.githubuser.lib.apolloClient
import kotlinx.coroutines.launch

class MainUsersViewModel(token: String) : TokenViewModel(token) {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?> = _error

    init {
        getUsers()
    }

    fun getLastQuery() = CURRENT_QUERIED_SEARCH

    fun getUsers(query: String) {
        CURRENT_QUERIED_SEARCH = query
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val resp = apolloClient(getToken()).query(UserSearchQuery(query)).execute().data?.search
                if (resp != null) {
                    _users.value = resp.nodes?.filter { it?.onUser != null }?.mapNotNull { userNode ->
                        val user = userNode?.onUser!!
                        User(
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
                _error.value = null
            } catch (_: ApolloNetworkException) {
                _error.value = R.string.no_connection
            } finally {
                _isLoading.value = false
                LAST_QUERIED_SEARCH = query
            }
        }
    }

    fun getUsers() {
        getUsers(LAST_QUERIED_SEARCH)
    }

    companion object {
        private var CURRENT_QUERIED_SEARCH = ""
        private var LAST_QUERIED_SEARCH = "placeholder"
    }
}