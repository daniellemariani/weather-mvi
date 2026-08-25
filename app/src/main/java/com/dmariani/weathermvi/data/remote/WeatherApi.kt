package com.dmariani.weathermvi.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for Open-Meteo's forecast endpoint. Returns only the
 * `current` weather block (temperature, WMO weather code, day/night flag)
 */
interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,weather_code,is_day"
    ): WeatherResponse
}