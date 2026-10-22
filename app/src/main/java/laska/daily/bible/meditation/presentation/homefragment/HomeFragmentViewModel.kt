package laska.daily.bible.meditation.presentation.homefragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.domain.GetReadingUseCase
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.courses.GetCourseForHomeScreenUseCase
import laska.daily.bible.meditation.domain.settings.GetSettingsUseCase
import laska.daily.bible.meditation.presentation.uils.DateUtils.Companion.todayFormatted
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val getReadingUseCase: GetReadingUseCase,
    private val getCourseForHomeScreenUseCase: GetCourseForHomeScreenUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
): ViewModel() {
    private val _homeUIState = MutableStateFlow<HomeFragmentState>(HomeFragmentState.Progress)
    val homeUIState = _homeUIState.asStateFlow()

    lateinit var currentLanguage: Language

    init {
        viewModelScope.launch {
            observeSettings()
            delay(50)
            setContent()
        }
    }

    private suspend fun setContent() {
        _homeUIState.value = HomeFragmentState.Progress
        try {
            val currentReading = getReadingUseCase(todayFormatted(), currentLanguage)
            val course = getCourseForHomeScreenUseCase(currentLanguage)
            _homeUIState.value = HomeFragmentState.Content(
                date = currentReading.dateFormatted,
                bibleReference = currentReading.bibleReference,
                featuredSectionTitle = "FROM FIREBASE",
                featuredCourseImageURL = course.imageURL,
                featuredCourseSessionsNumber = course.episodeCount,
                featuredCourseTitle = course.title,
                featuredCourseDescription = course.shortDescription,
                featuredCourseAuthors = course.authors
            )
        }
        catch(e: Exception) {
            _homeUIState.value = HomeFragmentState.Error("Failed to load content")
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