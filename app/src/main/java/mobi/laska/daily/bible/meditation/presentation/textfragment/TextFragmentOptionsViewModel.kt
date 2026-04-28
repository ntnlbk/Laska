package mobi.laska.daily.bible.meditation.presentation.textfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.domain.settings.GetSettingsUseCase
import mobi.laska.daily.bible.meditation.domain.settings.Settings
import mobi.laska.daily.bible.meditation.domain.settings.UpdateSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class TextFragmentOptionsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
): ViewModel() {
    private val _state = MutableStateFlow<TextFragmentOptionsState>(TextFragmentOptionsState.Progress)
    val state = _state.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            getSettingsUseCase()
                .onStart {
                    _state.value = TextFragmentOptionsState.Progress
                }
                .catch { e ->
                    _state.value = TextFragmentOptionsState.Error(
                        message = e.message ?: "Unknown error"
                    )
                }
                .collect { settings ->
                    _state.value = TextFragmentOptionsState.Content(settings)
                }
        }
    }

    fun updateSettings(newSettings: Settings) {
        viewModelScope.launch {
            try {
                updateSettingsUseCase(newSettings)
            } catch (e: Exception) {
                _state.value = TextFragmentOptionsState.Error(
                    message = e.message ?: "Update failed"
                )
            }
        }
    }

}