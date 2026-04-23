package mobi.laska.daily.bible.meditation.presentation.mainfragment

sealed class MainFragmentState {
    object Progress: MainFragmentState()
    data class Content(
        val date: String,
        val bibleReference: String,
        val feastName: String
    ): MainFragmentState()

    data class TextShowed(
        val dialogArguments: DialogArguments
    ): MainFragmentState()

    data class Error(
        val message: String
    ): MainFragmentState()
}
