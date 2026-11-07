package com.example.luminascout.Data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class LocationSearchResponse(
    val results: List<SearchedLocation>?
)

data class SearchedLocation(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?,
    val timezone: String?
)

interface LocationSearchService {

    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") name: String,
        @Query("count") count: Int = 8,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): LocationSearchResponse
}

object LocationSearchApi {

    private const val BASE_URL =
        "https://geocoding-api.open-meteo.com/"

    val service: LocationSearchService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationSearchService::class.java)
    }
}