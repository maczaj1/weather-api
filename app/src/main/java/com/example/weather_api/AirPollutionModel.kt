package com.example.weather_api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_api.repository.AirPollution
import com.example.weather_api.repository.Main
import com.example.weather_api.repository.UiState
import com.example.weather_api.repository.Weather
import com.example.weather_api.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AirPollutionModel : ViewModel() {

    private val weatherRepository = WeatherRepository()
    private val mutableAirPollutionData = MutableLiveData<UiState<List<AirPollution>>>()
    val immutableAirPollutionData: LiveData<UiState<List<AirPollution>>> = mutableAirPollutionData

    fun clearAirPollutionData() {
        mutableAirPollutionData.postValue(UiState(data = null))
    }

    fun getData(lon: Double, lat: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = weatherRepository.getAirPollutionResponse(lat, lon)
                if (request.raw().code == 404) {
                    mutableAirPollutionData.postValue(UiState(error = "Nie ma takiego miasta"))
                }
                if (request.isSuccessful) {
                    val weatherResponse = request.body()
                    val air = AirPollution(
                        list = weatherResponse?.list ?: emptyList()
                    )
                    mutableAirPollutionData.postValue(UiState(data = listOf(air)))
                } else {
                    Log.d("MainViewModel", "request failed : ${request.code()}")
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "request failed :", e)
            }
        }
    }
}