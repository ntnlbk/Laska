package laska.daily.bible.meditation.presentation.supportfragment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SupportFragmentLaunchMode: Parcelable {
    FROM_MAIN_MENU,
    FROM_POPUP
}