package com.dmariani.weathermvi.data.repository

import com.dmariani.weathermvi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Test double for [SettingsRepository]. Backed by an in-memory [MutableStateFlow]
 * instead of DataStore. [setIsFahrenheit] updates it directly, so tests can both
 * drive and observe the temperature-unit preference without real persistence.
 */
class FakeSettingsRepository : SettingsRepository {

    private val _isFahrenheit = MutableStateFlow(false)

    override val isFahrenheit: Flow<Boolean> = _isFahrenheit

    override suspend fun setIsFahrenheit(enabled: Boolean) {
        _isFahrenheit.value = enabled
    }
}