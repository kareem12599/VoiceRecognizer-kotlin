package data

import android.content.Context
import base.BaseRepository
import constants.Constants
import data.model.BaseErrorModel
import data.model.Result
import data.model.WeatherData
import data.network.RetrofitClient
import data.network.WeatherApi
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
                        404 -> " data not found"
                        500 -> "server is broken"
                        else -> "unknown error"
                    }
                    serverErrorCode = response.code()
                })
            }

        } catch (e: Throwable) {
            Result.error("failed", BaseErrorModel().apply {
                errorMessage = "Network failure, Open Wifi or mobile data and retry again "
                errorTitle = "Network error"

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
                errorMessage = "Can't find location data"
                errorTitle = "error"
                errorType = Constants.EMPTY_MEMORY
            })
        }
    }

}