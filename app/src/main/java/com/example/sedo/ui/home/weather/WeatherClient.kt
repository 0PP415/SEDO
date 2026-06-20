package com.example.sedo.ui.home.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val service: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON을 코틀린 객체로 자동 변환
            .build()
            .create(WeatherService::class.java)
    }
}