package com.example.voicerecognizerkotlin.data

import android.content.Context
import com.example.voicerecognizerkotlin.R
import com.example.voicerecognizerkotlin.base.BaseRepository
import com.example.voicerecognizerkotlin.constants.Constants
import com.example.voicerecognizerkotlin.data.model.BaseErrorModel
import com.example.voicerecognizerkotlin.data.model.Result
import com.example.voicerecognizerkotlin.data.model.WeatherData
import com.example.voicerecognizerkotlin.data.network.RetrofitClient
import com.example.voicerecognizerkotlin.data.network.WeatherApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

class WeatherRepository(private val context: Context): BaseRepository<WeatherData>() {
    var client: WeatherApi = RetrofitClient.weatherApi
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Result<WeatherData, BaseErrorModel> {
        return try {
            val response = withContext(Dispatchers.IO) {
                client.getLocationDetails(latitude, longitude, Constants.API_KEY)
            }
            if (response.isSuccessful && response.body() != null) {
                storeFileToExternalStorage(
                    response.body() as WeatherData,
                    context
                )
                Result.success(response.body())
            } else {
                Result.error(response.message(), BaseErrorModel().apply {
                    errorMessage = when (response.code()) {
                        Constants.ERROR_CODE_404 -> context.getString(R.string.data_not_found)
                        Constants.ERROR_CODE_500 -> context.getString(R.string.server_error)
                        else -> context.getString(R.string.unknown_error)
                    }
                    serverErrorCode = response.code()
                })
            }

        } catch (e: Throwable) {
            Result.error("failed", BaseErrorModel().apply {
                errorMessage = context.getString(R.string.network_failure_message)
                errorTitle = context.getString(R.string.network_failure_error)

            })
        }

    }

    private fun storeFileToExternalStorage(weatherData: WeatherData, context: Context?) {
        val gson = Gson()
        val weatherJson = gson.toJson(weatherData)

        val weatherFile = File(context?.filesDir, Constants.WEATHER_FILE_NAME)
        if (weatherFile.exists()) weatherFile.delete()
        weatherFile.createNewFile()

        val outputStream =
            context?.openFileOutput(Constants.WEATHER_FILE_NAME, Context.MODE_PRIVATE)
        outputStream?.write(weatherJson.toByteArray())
        outputStream?.close()
    }

    fun getFileFromStorage(context: Context?): Result<WeatherData, BaseErrorModel> {
        return try {
            val weatherFile = File(context?.filesDir, Constants.WEATHER_FILE_NAME)
            val weatherJson = weatherFile.readText()
            val gson = Gson()
            Result.success(gson.fromJson(weatherJson, WeatherData::class.java))
        } catch (e: Exception) {
            Result.error(" Error", BaseErrorModel().apply {
                errorMessage = context?.getString(R.string.location_not_available)
                errorTitle = context?.getString(R.string.default_error_title)
                errorType = Constants.EMPTY_MEMORY
            })
        }
    }

}