package com.example.sedo.ui.home.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,       // 도시 이름
        @Query("appid") apiKey: String,     // 발급받은 API 키
        @Query("units") units: String = "metric" // 화씨 대신 섭씨
    ): WeatherResponse
}