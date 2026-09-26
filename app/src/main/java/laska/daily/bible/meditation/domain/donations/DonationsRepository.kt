package laska.daily.bible.meditation.domain.donations

interface DonationsRepository {
    suspend fun getDonationsData(): DonationsData
}