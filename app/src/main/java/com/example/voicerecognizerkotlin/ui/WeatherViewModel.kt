package com.example.voicerecognizerkotlin.ui

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.voicerecognizerkotlin.base.BaseApplication
import com.example.voicerecognizerkotlin.constants.Constants
import com.example.voicerecognizerkotlin.data.WeatherRepository
import com.example.voicerecognizerkotlin.data.model.BaseErrorModel
import com.example.voicerecognizerkotlin.data.model.Result
import com.example.voicerecognizerkotlin.data.model.WeatherData
import kotlinx.coroutines.Dispatchers

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    fun refresh(location: Location?): LiveData<Result<WeatherData, BaseErrorModel>> {
        return liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
            try {
                emit(Result.loading())
                if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
                    val weatherData =
                        repository.getWeatherData(location.latitude, location.longitude)
                    emit(weatherData)
                } else {
                    repository.getFileFromStorage(BaseApplication.baseContext)?.let { emit(it) }
                        ?:emit(  Result.error(" Error", BaseErrorModel().apply {
                            errorType = Constants.EMPTY_MEMORY
                        }))
                }
            } catch (ioException: Throwable) {
                emit(Result.error(ioException.message!!, BaseErrorModel()))
            }

        }
    }


}
