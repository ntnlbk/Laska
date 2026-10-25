package laska.daily.bible.meditation.data.retrofit.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeAvailabilityDTO(
    val state: String,
    @SerialName("available_from")
    val availableFrom: String?,
)
