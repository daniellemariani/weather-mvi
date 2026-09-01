package com.dmariani.weathermvi.data

import com.dmariani.weathermvi.data.remote.CurrentWeatherResponse
import com.dmariani.weathermvi.data.remote.WeatherResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class WeatherCodeMappingTest(
    private val weatherCode: Int,
    private val expectedCondition: String
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "code {0} maps to {1}")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(0, "Clear"),
            arrayOf(1, "Cloudy"),
            arrayOf(45, "Fog"),
            arrayOf(51, "Rain"),
            arrayOf(71, "Snow"),
            arrayOf(95, "Storm"),
            arrayOf(4, "Unknown")
        )
    }

    @Test
    fun `weather code maps to expected condition`() {
        val current =
            CurrentWeatherResponse(temperature = 20.0, weatherCode = weatherCode, isDay = 1)
        val weather = WeatherResponse(current).toDomain("Test City")
        assertEquals(expectedCondition, weather.condition)
    }

}