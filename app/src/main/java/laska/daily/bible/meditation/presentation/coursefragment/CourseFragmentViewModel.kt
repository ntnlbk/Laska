package laska.daily.bible.meditation.presentation.coursefragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.courses.GetCourseUseCase
import laska.daily.bible.meditation.domain.courses.GetEpisodesByCourseIdUseCase
import laska.daily.bible.meditation.domain.settings.GetSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class CourseFragmentViewModel @Inject constructor(
    private val getCourseUseCase: GetCourseUseCase,
    private val getEpisodesByCourseIdUseCase: GetEpisodesByCourseIdUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args: CourseFragmentArgs = CourseFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val _courseUIState = MutableStateFlow<CourseFragmentState>(CourseFragmentState.Progress)
    val courseUIState = _courseUIState.asStateFlow()

    lateinit var currentLanguage: Language

    private val currentCourseId = args.courseId

    init {
        viewModelScope.launch {
            observeSettings()
            setContent()
        }
    }

    suspend fun setContent() {
        _courseUIState.value = CourseFragmentState.Progress
        try {
            val course = getCourseUseCase(currentCourseId)
            val episodes = getEpisodesByCourseIdUseCase(currentCourseId)
            _courseUIState.value = CourseFragmentState.Content(
                course = course,
                episodes = episodes
            )
        } catch (e: Exception) {
            _courseUIState.value = CourseFragmentState.Error(e.message.toString())
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            currentLanguage = getSettingsUseCase().first().language
            getSettingsUseCase().collect { settings ->
                if (currentLanguage != settings.language) {
                    currentLanguage = settings.language
                    setContent()
                }
            }
        }
    }

}