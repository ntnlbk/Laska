package mobi.laska.daily.bible.meditation.domain

interface ReadingRepository {

    suspend fun getReading(date: String, language: Language): ReadingItem



}