package laska.daily.bible.meditation.domain.courses

data class Course(
    val id: String,
    val imageRes: Int,
    val sessionsCount: String,
    val title: String,
    val description: String,
    val authors: List<Author>
)