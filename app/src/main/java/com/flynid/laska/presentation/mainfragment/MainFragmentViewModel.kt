package com.flynid.laska.presentation.mainfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flynid.laska.domain.GetReadingUseCase
import com.flynid.laska.domain.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val getReadingUseCase: GetReadingUseCase
): ViewModel (){

    private val _state = MutableStateFlow<MainFragmentState>(MainFragmentState.Progress)

    val state = _state.asStateFlow()

    fun showReadingText(date: String, language: Language){

        _state.value = MainFragmentState.Progress

        viewModelScope.launch {
            val item = getReadingUseCase(date, language)
            _state.value =
                MainFragmentState.Content(item.reflectionTextBody)
        }


    }

}