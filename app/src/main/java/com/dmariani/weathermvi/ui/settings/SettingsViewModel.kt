package com.dmariani.weathermvi.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmariani.weathermvi.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.isFahrenheit.collect { isFahrenheit ->
                _state.update { it.copy(isFahrenheit = isFahrenheit) }
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleTemperatureUnit -> handleToggleTemperatureUnit(intent.isFahrenheit)
        }
    }

    private fun handleToggleTemperatureUnit(isFahrenheit: Boolean) {
        viewModelScope.launch {
            repository.setIsFahrenheit(isFahrenheit)
        }
    }
}