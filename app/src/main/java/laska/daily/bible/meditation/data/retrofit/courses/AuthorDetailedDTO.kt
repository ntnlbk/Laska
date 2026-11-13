package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorDetailedDTO(
    @SerialName("schema_version")
    val schemaVersion: Int,
    val slug: String,
    val name: String,
    val id: Int,
    val photo: ImageDTO,
    val description: String
)
