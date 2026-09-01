package com.dmariani.weathermvi.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TemperatureConverterTest {

    @Test
    fun `converts 0 Celsius to 32 Fahrenheit`() {
        assertEquals(32.0, celsiusToFahrenheit(0.0), 0.1)
    }

    @Test
    fun `converts 100 Celsius to 212 Fahrenheit`() {
        assertEquals(212.0, celsiusToFahrenheit(100.0), 0.1)
    }
}