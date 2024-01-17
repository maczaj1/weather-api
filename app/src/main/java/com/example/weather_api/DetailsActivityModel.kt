package com.example.weather_api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_api.repository.City
import com.example.weather_api.repository.ResponseBody
import com.example.weather_api.repository.UiState
import com.example.weather_api.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsActivityModel : ViewModel() {

    private val weatherForecastRepository = WeatherRepository()
    private val mutableWeathersForecastData = MutableLiveData<UiState<List<ResponseBody>>>()
    val immutableWeathersForecastData: LiveData<UiState<List<ResponseBody>>> =
        mutableWeathersForecastData

    fun getData(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = weatherForecastRepository.getWeatherForecastResponse(city)
                if (response.raw().code == 404) {
                    mutableWeathersForecastData.postValue(UiState(error = "Nie ma takiego miasta"))
                }
                if (response.isSuccessful) {
                    val weatherForecastResponse = response.body()
                    val responseBody = ResponseBody(
                        city = weatherForecastResponse?.city ?: City("", ""),
                        list = weatherForecastResponse?.list ?: emptyList()
                    )
                    mutableWeathersForecastData.postValue(UiState(data = listOf(responseBody)))
                } else {
                    Log.d("MainViewModel", "request failed : ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "request failed :", e)
            }
        }
    }
}