package laska.daily.bible.meditation.domain.courses

import laska.daily.bible.meditation.domain.Language
import javax.inject.Inject

class GetCourseForHomeScreenUseCase @Inject constructor(private val repository: CoursesRepository) {
    suspend operator fun invoke(language: Language) = repository.getCourseForHomeScreen(language)
}
