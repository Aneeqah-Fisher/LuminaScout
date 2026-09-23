package com.example.luminascout.Data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ---------------------------------------------------------
// WEATHER RESPONSE
// ---------------------------------------------------------

data class WeatherResponse(
    val current_weather: CurrentWeather?,
    val hourly: HourlyWeather?,
    val daily: DailyWeather?
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int
)

// ---------------------------------------------------------
// HOURLY WEATHER
// ---------------------------------------------------------

data class HourlyWeather(
    val cloud_cover: List<Double>?,
    val precipitation_probability: List<Double>?
)

// ---------------------------------------------------------
// DAILY WEATHER / SUN TIMES
// ---------------------------------------------------------

data class DailyWeather(
    val time: List<String>?,
    val sunrise: List<String>?,
    val sunset: List<String>?,
    val weathercode: List<Int>?,
    val temperature_2m_max: List<Double>?,
    val temperature_2m_min: List<Double>?
)

// ---------------------------------------------------------
// API SERVICE
// ---------------------------------------------------------

interface ApiService {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double = -33.9249,
        @Query("longitude") lon: Double = 18.4241,

        @Query("current_weather") currentWeather: Boolean = true,

        @Query("hourly") hourly: String =
            "cloud_cover,precipitation_probability",

        @Query("daily") daily: String =
            "sunrise,sunset,weathercode,temperature_2m_max,temperature_2m_min",

        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

// ---------------------------------------------------------
// RETROFIT CLIENT
// ---------------------------------------------------------

object RetrofitClient {

    private const val BASE_URL = "https://api.open-meteo.com/"

    val apiService: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}