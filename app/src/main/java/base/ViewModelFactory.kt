package base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import data.WeatherRepository
import main.WeatherViewModel

class ViewModelFactory<M>(private var repository: BaseRepository<M>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java))
            return WeatherViewModel(repository as WeatherRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}