package com.example.weather_api.repository

import retrofit2.Response

class WeatherRepository {

    private val API_KEY = "3ce04cbdc1dab205d1f5ace47a035da6"

    suspend fun getWeatherResponse(city: String): Response<Weather> =
        WeatherService.weatherService.getWeatherResponse(city, API_KEY)

    suspend fun getWeatherForecastResponse(city: String): Response<ResponseBody> =
        WeatherService.weatherService.getWeatherForecastResponse(city, API_KEY)

    suspend fun getAirPollutionResponse(lat: Double, lon: Double): Response<AirPollution> =
        WeatherService.weatherService.getAirPollutionResponse(lat, lon, API_KEY)
}