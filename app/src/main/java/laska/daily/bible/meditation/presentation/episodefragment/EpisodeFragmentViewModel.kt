package laska.daily.bible.meditation.presentation.episodefragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.domain.courses.GetEpisodeUseCase
import javax.inject.Inject

@HiltViewModel
class EpisodeFragmentViewModel @Inject constructor(
    private val getEpisodeUseCase: GetEpisodeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args: EpisodeFragmentArgs = EpisodeFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val _episodeUIState =
        MutableStateFlow<EpisodeFragmentState>(EpisodeFragmentState.Progress)
    val episodeUIState = _episodeUIState.asStateFlow()

    init {
        viewModelScope.launch {
            setContent()
        }
    }

    private suspend fun setContent() {
        _episodeUIState.value = EpisodeFragmentState.Progress
        try {
            val episode = getEpisodeUseCase(args.courseId, args.episodeId)
            _episodeUIState.value = EpisodeFragmentState.Content(
                imageUrl = episode.imageURL,
                title = episode.title,
                reference = episode.scriptureReference,
                subtitle = episode.subtitle,
                authors = episode.authors,
                text = episode.text
            )
        } catch (e: Exception) {
            _episodeUIState.value = EpisodeFragmentState.Error(e.message.toString())
        }
    }

}