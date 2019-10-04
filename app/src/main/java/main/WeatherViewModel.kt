package main

import android.location.Location
import androidx.lifecycle.*
import base.BaseApplication
import data.WeatherRepository
import data.model.BaseErrorModel
import data.model.Result
import data.model.WeatherData
import kotlinx.coroutines.Dispatchers

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    fun refresh(location: Location?): LiveData<Result<WeatherData, BaseErrorModel>> {
        return liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
            try {
                emit(Result.loading())
                if ( location != null &&location.latitude != 0.0 && location.longitude != 0.0) {
                    val weatherData = repository.getWeatherData(location.latitude, location.longitude)
                    emit(weatherData)
                }else
                    emit(repository.getFileFromStorage(BaseApplication.baseContext))
            } catch (ioException: Throwable) {
                emit(Result.error(ioException.message!!, BaseErrorModel()))
            }

        }
    }





}
