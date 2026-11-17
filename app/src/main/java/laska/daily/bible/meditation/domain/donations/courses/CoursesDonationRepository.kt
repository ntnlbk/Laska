package laska.daily.bible.meditation.domain.donations.courses

interface CoursesDonationRepository {

    suspend fun shouldShowPopUp(): Boolean

}