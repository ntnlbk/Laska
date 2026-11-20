package laska.daily.bible.meditation.domain.donations.popup

interface PopUpLogicRepository {
    suspend fun getPopUpStatus(): PopUpMode
}