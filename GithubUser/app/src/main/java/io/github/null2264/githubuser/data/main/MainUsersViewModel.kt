package io.github.null2264.githubuser.data.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.github.null2264.githubuser.data.common.AppRepository
import io.github.null2264.githubuser.data.common.GithubViewModel
import io.github.null2264.githubuser.data.common.Result
import io.github.null2264.githubuser.data.database.entity.UserEntity

class MainUsersViewModel(private val repo: AppRepository) : GithubViewModel(repo) {
    val searchResult: LiveData<Result<List<UserEntity>>>
    private val searchQuery = MutableLiveData<String?>(null)
    fun getSearchQuery() = searchQuery.value
    fun setSearchQuery(newValue: String?) = searchQuery.postValue(newValue)
    fun refreshSearch() = searchQuery.postValue(favoriteQuery.value)

    var favorites: LiveData<Result<List<UserEntity>>>
    private var favoriteQuery = MutableLiveData<String?>(null)
    fun getFavoriteQuery() = favoriteQuery.value
    fun setFavoriteQuery(newValue: String?) = favoriteQuery.postValue(newValue)
    fun refreshFavorite() = favoriteQuery.postValue(favoriteQuery.value)

    init {
        searchResult = Transformations.switchMap(searchQuery) { query ->
            repo.getUsers(query)
        }
        favorites = Transformations.switchMap(favoriteQuery) { query ->
            repo.getFavorites(query)
        }
    }
}