package com.flynid.laska.presentation.mainfragment

sealed class MainFragmentState {
    object Progress: MainFragmentState()
    data class Content(
        val date: String
    ): MainFragmentState()

    data class TextShowed(
        val textsToShow: TextsToShow
    ): MainFragmentState()

    data class Error(
        val message: String
    ): MainFragmentState()
}
