package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.Serializable

@Serializable
data class AuthorShortDTO(
    val id: Int,
    val name: String,
    val photo: ImageDTO,

)

