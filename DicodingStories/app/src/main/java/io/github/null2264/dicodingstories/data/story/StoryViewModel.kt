package io.github.null2264.dicodingstories.data.story

import androidx.lifecycle.*
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.null2264.dicodingstories.data.Repository
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(repo: Repository) : ViewModel() {
    // TODO(for sub2) - Map stuff

    private var storyState: MutableLiveData<StoryFilter> = MutableLiveData(StoryFilter())
    fun setState(newValue: StoryFilter) = storyState.postValue(newValue)
    fun getState(): LiveData<StoryFilter> = storyState
    fun refreshStories() = storyState.postValue(storyState.value)

    val stories = Transformations.switchMap(storyState) {
        repo.fetchPagedStories(it).cachedIn(viewModelScope).asLiveData()
    }
}