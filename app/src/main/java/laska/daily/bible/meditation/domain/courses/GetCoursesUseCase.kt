package laska.daily.bible.meditation.domain.courses

import laska.daily.bible.meditation.domain.Language
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(private val repository: CoursesRepository) {
    suspend operator fun invoke(language: Language) = repository.getCourses(language)
}