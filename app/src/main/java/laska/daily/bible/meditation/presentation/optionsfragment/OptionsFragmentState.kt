package laska.daily.bible.meditation.presentation.optionsfragment

import laska.daily.bible.meditation.domain.settings.Settings

sealed class OptionsFragmentState {
    object Progress : OptionsFragmentState()
    data class Content(
        val settings: Settings,
        val isSupportButtonVisible: Boolean
    ) : OptionsFragmentState()
    data class Error(
        val message: String
    ): OptionsFragmentState()
}