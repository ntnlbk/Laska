package laska.daily.bible.meditation.domain.courses

data class Episode(
    val number: Int,
    val title: String,
    val subtitle: String,
    val time: String,
    val isCompleted: Boolean,
    val isClosed: Boolean
)
