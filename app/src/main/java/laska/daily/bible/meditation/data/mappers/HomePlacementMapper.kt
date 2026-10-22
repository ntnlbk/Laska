package laska.daily.bible.meditation.data.mappers

import android.app.Application
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.domain.courses.HomePlacement
import javax.inject.Inject

class HomePlacementMapper @Inject constructor(
    private val context: Application
) {
    fun mapHomePlacementToString(homePlacement: HomePlacement): String {
        return when (homePlacement){
            HomePlacement.FEATURED -> context.getString(R.string.featured_home_placement)
            HomePlacement.REGULAR -> context.getString(R.string.regular_home_placement)
        }
    }
    fun mapStringToHomePlacement(string: String): HomePlacement {
        return when (string){
            context.getString(R.string.featured_home_placement) -> HomePlacement.FEATURED
            context.getString(R.string.regular_home_placement) -> HomePlacement.REGULAR
            else -> throw Exception("Incorrect homePlacement string")
        }
    }
}