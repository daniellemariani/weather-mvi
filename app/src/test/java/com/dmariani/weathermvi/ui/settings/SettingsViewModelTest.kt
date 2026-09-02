package com.dmariani.weathermvi.ui.settings

import com.dmariani.weathermvi.data.repository.FakeSettingsRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    private lateinit var viewModel: SettingsViewModel
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = SettingsViewModel(fakeSettingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when ToggleTemperatureUnit to Fahrenheit enabled updates preferences`() = runTest(testDispatcher) {
        viewModel.onIntent(SettingsIntent.ToggleTemperatureUnit(true))
        advanceUntilIdle()
        assertEquals(true, viewModel.state.value.isFahrenheit)
    }

    @Test
    fun `when ToggleTemperatureUnit to Fahrenheit disabled updates preferences`() = runTest(testDispatcher) {
        viewModel.onIntent(SettingsIntent.ToggleTemperatureUnit(true))
        advanceUntilIdle()
        viewModel.onIntent(SettingsIntent.ToggleTemperatureUnit(false))
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isFahrenheit)
    }
}