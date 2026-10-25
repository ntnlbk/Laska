package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AudioDTO(
    val url: String,
    @SerialName("mime_type")
    val mimeType: String,
    val duration: Int
)
