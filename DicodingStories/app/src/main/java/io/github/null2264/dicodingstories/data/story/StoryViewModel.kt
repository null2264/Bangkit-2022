package io.github.null2264.dicodingstories.data.story

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.Repository
import io.github.null2264.dicodingstories.data.model.Story
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(repo: Repository) : ViewModel() {
    // TODO(for sub2) - Map stuff
    private val _transitionState: MutableLiveData<Int> = MutableLiveData(R.id.start)
    val transitionState: LiveData<Int> = _transitionState
    fun setTransitionState(newState: Int) {
        _transitionState.value = newState
    }

    private val filterState: MutableLiveData<StoryFilter> = MutableLiveData(StoryFilter())
    fun setFilterState(newValue: StoryFilter) = filterState.postValue(newValue)
    fun getFilterState(): LiveData<StoryFilter> = filterState

    val stories: LiveData<PagingData<Story>> = Transformations.switchMap(filterState) {
        repo.fetchStories(it).cachedIn(viewModelScope).asLiveData()
    }
}