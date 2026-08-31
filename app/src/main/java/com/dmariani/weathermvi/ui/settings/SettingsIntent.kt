package com.dmariani.weathermvi.ui.settings

sealed interface SettingsIntent {
    data class ToggleTemperatureUnit(val isFahrenheit: Boolean) : SettingsIntent
}