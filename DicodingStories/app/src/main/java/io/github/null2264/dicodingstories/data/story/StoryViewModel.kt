package io.github.null2264.dicodingstories.data.story

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.null2264.dicodingstories.data.Repository
import io.github.null2264.dicodingstories.data.model.Story
import io.github.null2264.dicodingstories.lib.Result
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(repo: Repository) : ViewModel() {
    // TODO(for sub2) - make filter for location only
    private var _storyFilter: MutableLiveData<String> = MutableLiveData("")
    var storyFilter: String
        get() = _storyFilter.value ?: ""
        set(newValue) { _storyFilter.postValue(newValue) }
    fun refreshStories() = _storyFilter.postValue(_storyFilter.value)

    val stories: LiveData<Result<List<Story>>>

    init {
        stories = Transformations.switchMap(_storyFilter) { query ->
            repo.fetchStories().asLiveData()
        }
    }
}