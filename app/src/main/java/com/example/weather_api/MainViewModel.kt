package com.example.weather_api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_api.repository.Coord
import com.example.weather_api.repository.Main
import com.example.weather_api.repository.UiState
import com.example.weather_api.repository.Weather
import com.example.weather_api.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val weatherRepository = WeatherRepository()
    private val mutableWeathersData = MutableLiveData<UiState<List<Weather>>>()
    val immutableWeathersData: LiveData<UiState<List<Weather>>> = mutableWeathersData

    fun getData(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = weatherRepository.getWeatherResponse(city)
                if (request.raw().code == 404) {
                    mutableWeathersData.postValue(UiState(error = "Nie ma takiego miasta"))
                }
                if (request.isSuccessful) {
                    val weatherResponse = request.body()

                    val weather = Weather(
                        coord = weatherResponse?.coord ?: Coord(0.0, 0.0),
                        id = weatherResponse?.id ?: 0,
                        name = weatherResponse?.name ?: "",
                        weather = weatherResponse?.weather ?: emptyList(),
                        main = weatherResponse?.main ?: Main(0.0, 0.0, 0, 0)
                    )
                    mutableWeathersData.postValue(UiState(data = listOf(weather)))
                } else {
                    Log.d("MainViewModel", "request failed : ${request.code()}")
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "request failed :", e)
            }
        }
    }
}