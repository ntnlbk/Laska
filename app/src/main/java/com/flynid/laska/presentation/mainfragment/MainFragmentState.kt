package com.flynid.laska.presentation.mainfragment

sealed class MainFragmentState {
    object Progress: MainFragmentState()
    data class Content(
        val textToShow: String
    ): MainFragmentState()

    data class Error(
        val message: String
    ): MainFragmentState()
}