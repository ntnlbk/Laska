package laska.daily.bible.meditation.presentation.coursesmainfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.courses.GetCoursesUseCase
import laska.daily.bible.meditation.domain.settings.GetSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class CoursesMainFragmentViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
    private val _coursesMainFragmentState =
        MutableStateFlow<CoursesMainFragmentState>(CoursesMainFragmentState.Progress)
    val coursesMainFragmentState = _coursesMainFragmentState.asStateFlow()

    private lateinit var currentLanguage: Language

    init {
        viewModelScope.launch {
            observeSettings()
            delay(100)
            setContent()
        }
    }

    private suspend fun setContent() {
        _coursesMainFragmentState.value = CoursesMainFragmentState.Progress
        try {
            val courses = getCoursesUseCase(currentLanguage)
            _coursesMainFragmentState.value = CoursesMainFragmentState.Content(
                courses = courses
            )

        } catch (e: Exception){
            _coursesMainFragmentState.value = CoursesMainFragmentState.Error(e.message.toString())
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