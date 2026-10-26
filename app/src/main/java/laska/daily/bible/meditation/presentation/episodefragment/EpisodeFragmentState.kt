package laska.daily.bible.meditation.presentation.episodefragment

import laska.daily.bible.meditation.domain.courses.Author


sealed class EpisodeFragmentState {
    object Progress : EpisodeFragmentState()
    data class Content(
        val imageUrl: String,
        val title: String,
        val reference: String,
        val subtitle: String,
        val authors: List<Author>,
        val text: String,
        val audioUrl: String
    ) : EpisodeFragmentState()

    data class Error(val message: String) : EpisodeFragmentState()

}