package com.example.sedo.ui.home.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    // baseUrl 뒤에 붙을 세부 주소
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,       // 도시 이름
        @Query("appid") apiKey: String,     // 발급받은 API 키
        @Query("units") units: String = "metric" // 화씨 대신 섭씨(°C)로 받기
    ): WeatherResponse
}