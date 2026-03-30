package com.flynid.laska.presentation.mainfragment

sealed class MainFragmentState {
    object Progress: MainFragmentState()
    data class Content(
        val readingText: String
    ): MainFragmentState()
}