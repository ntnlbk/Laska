package laska.daily.bible.meditation.presentation.coursefragment

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
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
    private val _courseUIState = MutableStateFlow<CourseFragmentState>(CourseFragmentState.Progress)
    val courseUIState = _courseUIState.asStateFlow()

    lateinit var currentLanguage: Language

    var currentCourseId: Int = 0

    init {
        viewModelScope.launch {
            observeSettings()
        }
    }

    suspend fun setContent(courseId: Int) {
        _courseUIState.value = CourseFragmentState.Progress
        try {
            val course = getCourseUseCase(courseId)
            val episodes = getEpisodesByCourseIdUseCase(courseId)
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
                    setContent(currentCourseId)
                }
            }
        }
    }

}