package com.example.voicerecognizerkotlin.data.network

import com.example.voicerecognizerkotlin.data.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getLocationDetails(@Query("lat") lat: Double, @Query("lon") lng: Double,
                           @Query("appid") key: String): Response<WeatherData>
}