package com.example.voicerecognizerkotlin.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voicerecognizerkotlin.data.WeatherRepository
import com.example.voicerecognizerkotlin.ui.WeatherViewModel

class ViewModelFactory<M>(private var repository: BaseRepository<M>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java))
            return WeatherViewModel(repository as WeatherRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}