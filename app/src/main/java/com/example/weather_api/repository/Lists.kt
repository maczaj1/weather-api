package com.example.weather_api.repository

data class Lists(
    val dt: Long,
    val main: Main,
    val weather: List<Weathers>
)
