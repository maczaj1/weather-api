package com.example.weather_api.repository

data class Weather(
    val coord: Coord,
    val id: Int,
    val name: String,
    val weather: List<Weathers>,
    val main: Main
)

