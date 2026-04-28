package mobi.laska.daily.bible.meditation.presentation.textfragment

import mobi.laska.daily.bible.meditation.domain.settings.Settings

sealed class TextFragmentOptionsState {
    data class Content(
        val settings: Settings
    ) : TextFragmentOptionsState()

    data class Error(
        val message: String
    ) : TextFragmentOptionsState()
    object Progress: TextFragmentOptionsState()
}