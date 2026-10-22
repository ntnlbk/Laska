package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    val url: String,
    val width: Int,
    val height: Int,
    val alt: String
)
