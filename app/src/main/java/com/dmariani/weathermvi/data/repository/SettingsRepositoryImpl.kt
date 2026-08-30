package com.dmariani.weathermvi.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.dmariani.weathermvi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        private val IS_FAHRENHEIT_KEY = booleanPreferencesKey("is_fahrenheit")
    }

    override val isFahrenheit: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_FAHRENHEIT_KEY] ?: false
    }

    override suspend fun setIsFahrenheit(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FAHRENHEIT_KEY] = enabled
        }
    }
}