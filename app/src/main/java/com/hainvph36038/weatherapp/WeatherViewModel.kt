package com.hainvph36038.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hainvph36038.weatherapp.api.Constant
import com.hainvph36038.weatherapp.api.NetworkResponse
import com.hainvph36038.weatherapp.api.RetrofitInstance
import com.hainvph36038.weatherapp.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    init {
        getData("Hanoi")
    }

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    } ?: run {
                        _weatherResult.value = NetworkResponse.Error("No data received")
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Failed to load data: ${response.message()}")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Failed to load data: ${e.message}")
            }
        }
    }

    // Function to reset data to default city
    fun resetToDefault() {
        getData("Hanoi") // Replace "Hanoi" with your default city
    }
}

