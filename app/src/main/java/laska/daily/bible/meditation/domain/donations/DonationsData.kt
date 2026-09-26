package laska.daily.bible.meditation.domain.donations

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class DonationsData @Inject constructor(
    val donationsFlag: Boolean,
    val donationsUrl: String,
    val donationsEripPath: List<String>,
    val eripAccount: String
): Parcelable
