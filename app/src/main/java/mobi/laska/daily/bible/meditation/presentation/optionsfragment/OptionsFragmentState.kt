package mobi.laska.daily.bible.meditation.presentation.optionsfragment

import mobi.laska.daily.bible.meditation.domain.settings.Settings

sealed class OptionsFragmentState {
    object Progress : OptionsFragmentState()
    data class Content(
        val settings: Settings,
    ) : OptionsFragmentState()
    data class Error(
        val message: String
    ): OptionsFragmentState()
}