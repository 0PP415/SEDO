package com.example.sedo.ui.home.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weatherList: List<WeatherData>
)

data class MainData(
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int
)

data class WeatherData(
    @SerializedName("main") val mainState: String,
    @SerializedName("description") val description: String
)