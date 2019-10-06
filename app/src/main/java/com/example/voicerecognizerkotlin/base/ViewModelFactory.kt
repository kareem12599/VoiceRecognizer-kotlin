package com.example.voicerecognizerkotlin.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voicerecognizerkotlin.data.WeatherRepository
import com.example.voicerecognizerkotlin.ui.WeatherViewModel
import com.example.voicerecognizerkotlin.utils.InjectionUtils

class ViewModelFactory<M>(private var context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java))
            return InjectionUtils.injectViewModel(context) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}