package com.flynid.laska.presentation.mainfragment

import java.lang.ref.Reference

sealed class MainFragmentState {
    object Progress: MainFragmentState()
    data class Content(
        val date: String,
        val bibleReference: String,
        val feastName: String
    ): MainFragmentState()

    data class TextShowed(
        val textsToShow: TextsToShow
    ): MainFragmentState()

    data class Error(
        val message: String
    ): MainFragmentState()
}
