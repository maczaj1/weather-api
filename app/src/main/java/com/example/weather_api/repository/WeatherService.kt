package com.example.weather_api.repository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    @GET("/data/2.5/weather")
    suspend fun getWeatherResponse(
        @Query("q") city: String,
        @Query("appid") apiKey: String
    ): Response<Weather>

    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecastResponse(
        @Query("q") city: String,
        @Query("appid") apiKey: String
    ): Response<ResponseBody>

    @GET("/data/2.5/air_pollution")
    suspend fun getAirPollutionResponse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response< AirPollution>

    companion object {
        private const val URL = "https://api.openweathermap.org"

        private val logger = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttp = OkHttpClient.Builder().apply {
            this.addInterceptor(logger)
        }.build()

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp)
                .build()
        }

        val weatherService: WeatherService by lazy {
            retrofit.create(WeatherService::class.java)
        }
    }
}