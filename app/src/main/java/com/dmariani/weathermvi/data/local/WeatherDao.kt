package com.dmariani.weathermvi.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Upsert
    suspend fun upsert(weather: WeatherEntity)

    @Query("SELECT * FROM weather WHERE city = :city")
    suspend fun getOnce(city: String): WeatherEntity?

    @Query("SELECT city FROM weather ORDER BY cachedAt DESC LIMIT :limit")
    fun recentCities(limit: Int = 5): Flow<List<String>>
}