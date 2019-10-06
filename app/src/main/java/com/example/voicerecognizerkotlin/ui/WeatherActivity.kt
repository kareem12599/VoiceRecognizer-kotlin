package com.example.voicerecognizerkotlin.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.voicerecognizerkotlin.DaggerWeatherComponent
import com.example.voicerecognizerkotlin.R
import com.example.voicerecognizerkotlin.WeatherComponent
import com.example.voicerecognizerkotlin.WeatherModule

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WeatherFragment.newInstance())
                .commitNow()
        }
     //   createWeatherComponent(this)
    }



}
