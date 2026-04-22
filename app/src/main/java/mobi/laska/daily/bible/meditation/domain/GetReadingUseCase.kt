package mobi.laska.daily.bible.meditation.domain

import javax.inject.Inject

class GetReadingUseCase @Inject constructor(private val repository: ReadingRepository) {
    suspend operator fun invoke(date: String, language: Language) = repository.getReading(date, language)
}