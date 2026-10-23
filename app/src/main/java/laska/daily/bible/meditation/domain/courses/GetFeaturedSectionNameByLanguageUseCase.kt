package laska.daily.bible.meditation.domain.courses

import laska.daily.bible.meditation.domain.Language
import javax.inject.Inject

class GetFeaturedSectionNameByLanguageUseCase @Inject constructor(
    private val repository: CoursesRepository
) {
    suspend operator fun invoke(language: Language) = repository.getFeaturedSectionNameByLanguage(language)
}