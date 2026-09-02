package com.dmariani.weathermvi.ui.main

import app.cash.turbine.test
import com.dmariani.weathermvi.data.repository.FakeSettingsRepository
import com.dmariani.weathermvi.data.repository.FakeWeatherRepository
import com.dmariani.weathermvi.domain.model.City
import com.dmariani.weathermvi.domain.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeWeatherRepository: FakeWeatherRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeWeatherRepository = FakeWeatherRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = WeatherViewModel(fakeWeatherRepository, fakeSettingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when SelectCity success updates weather`() = runTest (testDispatcher) {
        val city = City(name = "Los Angeles", lat = 34.0522, lon = -118.2437)
        val weather = Weather(city = city.name, temperature = 32.0, condition = "Clear", isDay = true)

        fakeWeatherRepository.forcedWeather = weather
        viewModel.onIntent(WeatherIntent.SelectCity(city))

        advanceUntilIdle()

        val result = (viewModel.state.value.contentState as WeatherContentState.Success).weather
        assertEquals(weather, result)
    }

    @Test
    fun `when SelectCity failure updates error message`() = runTest (testDispatcher) {
        val city = City(name = "Los Angeles", lat = 34.0522, lon = -118.2437)

        fakeWeatherRepository.forcedError = RuntimeException("Network error")
        viewModel.onIntent(WeatherIntent.SelectCity(city))

        advanceUntilIdle()

        val expectedError = "Unable to load weather from ${city.name}"
        val errorMessage = (viewModel.state.value.contentState as WeatherContentState.Error).message
        assertEquals(expectedError, errorMessage)
    }

    @Test
    fun `when SelectCity failure emits snackbar event`() = runTest (testDispatcher) {
        val city = City(name = "Los Angeles", lat = 34.0522, lon = -118.2437)

        fakeWeatherRepository.forcedError = RuntimeException("Network error")

        viewModel.snackbarEvent.test {
            val expectedError = "Unable to load weather from ${city.name}"
            viewModel.onIntent(WeatherIntent.SelectCity(city))
            val message = awaitItem()
            assertEquals(expectedError, message)
        }
    }

    @Test
    fun `when SelectCity failure updates error and emits snackbar event`() = runTest (testDispatcher) {
        val city = City(name = "Los Angeles", lat = 34.0522, lon = -118.2437)

        fakeWeatherRepository.forcedError = RuntimeException("Network error")

        viewModel.snackbarEvent.test {
            viewModel.onIntent(WeatherIntent.SelectCity(city))
            val expectedError = "Unable to load weather from ${city.name}"
            val message = awaitItem()
            val errorMessage = (viewModel.state.value.contentState as WeatherContentState.Error).message
            assertEquals(expectedError, errorMessage)
            assertEquals(expectedError, message)
        }
    }
}