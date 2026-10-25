package laska.daily.bible.meditation.domain.courses

data class Episode(
    val id: Int,
    val courseId: Int,
    val number: Int,
    val title: String,
    val subtitle: String,
    val scriptureReference: String,
    val imageURL: String,
    val authors: List<Author>,
    val audioURL: String,
    val duration: Int,
    val text: String,
    val state: EpisodeState,
)
