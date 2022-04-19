package io.github.null2264.dicodingstories.data.story

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.null2264.dicodingstories.data.Repository
import io.github.null2264.dicodingstories.data.model.Story
import io.github.null2264.dicodingstories.lib.Result
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(repo: Repository) : ViewModel() {
    // TODO(for sub2) - Paging

    private var queryState: MutableLiveData<String> = MutableLiveData("")
    fun refreshStories() = queryState.postValue("")

    val stories: LiveData<Result<List<Story>>> = Transformations.switchMap(queryState) {
        repo.fetchStories().asLiveData()
    }
}