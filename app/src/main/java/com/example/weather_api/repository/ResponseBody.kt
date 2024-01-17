package com.example.weather_api.repository

data class ResponseBody(
    val city: City,
    val list: List<Lists>
)
