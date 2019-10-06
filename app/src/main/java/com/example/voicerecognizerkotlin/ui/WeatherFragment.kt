package com.example.voicerecognizerkotlin.ui

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.voicerecognizerkotlin.R
import com.example.voicerecognizerkotlin.base.ViewModelFactory
import com.example.voicerecognizerkotlin.constants.Constants
import com.example.voicerecognizerkotlin.data.model.BaseErrorModel
import com.example.voicerecognizerkotlin.data.model.Status
import com.example.voicerecognizerkotlin.data.model.WeatherData
import com.example.voicerecognizerkotlin.utils.InjectionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.layout_error_view.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToLong

class WeatherFragment : Fragment() {
    private var requestedPermissionGranted: Boolean = false
    private val RC_ENABLE_LOCATION = 1
    private val RC_LOCATION_PERMISSION = 2
    private val REQ_CODE_SPEECH_INPUT = 100
    private lateinit var mLocationManager: LocationManager
    private var mLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: WeatherViewModel
    private lateinit var speech: TextToSpeech

    private var mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location?.let {
                mLocation = location
                mLocationManager.removeUpdates(this)
            }

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }


    companion object {
        fun newInstance() = WeatherFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory<WeatherData>(requireContext())
        )
            .get(WeatherViewModel::class.java).apply {

            }
        mLocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                mLocation = location
            }
        }
            .addOnFailureListener {
                if (!shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (checkAndAskForLocationPermissions())
                        checkGpsEnabledAndPrompt()
                } else
                    showAlertDialog()
            }

        speech = TextToSpeech(context,
            TextToSpeech.OnInitListener { status -> })
        speech.language = Locale.UK

        btnSpeak.setOnClickListener { (startVoiceInput()) }
    }

    private fun checkGpsEnabledAndPrompt() {
        val isLocationEnabled =
            mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isLocationEnabled) {
            AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setTitle(getString(R.string.gps_dialog_title))
                .setMessage(getString(R.string.gps_dialog_message))
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, RC_ENABLE_LOCATION)

                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                    fusedLocationClient.lastLocation.addOnFailureListener { showAlertDialog() }
                        .addOnSuccessListener { location ->
                            location?.let { mLocation = location } ?: showAlertDialog()
                        }
                }
                .create()
                .show()
        } else {
            requestLocationUpdates()
        }
    }

    private fun requestLocationUpdates() {
        val provider = LocationManager.NETWORK_PROVIDER
        checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
        mLocationManager.requestLocationUpdates(provider, 0, 0.0f, mLocationListener)

        val location = mLocationManager.getLastKnownLocation(provider)
        mLocationListener.onLocationChanged(location)
    }

    private fun checkAndAskForLocationPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    RC_LOCATION_PERMISSION
                )
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGpsEnabledAndPrompt()
                    requestedPermissionGranted = true
                } else {
                    requestedPermissionGranted = false
                    showAlertDialog()


                }
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_dialog_message))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    processVoiceOutput(result)
                }
            }
        }
    }

    private fun processVoiceOutput(result: ArrayList<String>) {
        with(result[0]) {
            when {
                contains("weather") || contains("show weather") -> refreshWeatherData()
                else -> displayNotFoundText(BaseErrorModel().apply {
                    errorMessage =
                        getString(R.string.sentence_not_recognized_error_message)
                })
            }
        }

    }

    private fun refreshWeatherData() {
        mLocation.let {
            viewModel.refresh(it)
                .observe(this@WeatherFragment, Observer { data ->
                    when {
                        data.status == Status.LOADING -> showProgress()
                        data.status == Status.SUCCESS -> updateView(data.response)
                        else -> {
                            if (!requestedPermissionGranted && checkAndAskForLocationPermissions() && data.errorModel?.errorType != Constants.EMPTY_MEMORY)
                                checkGpsEnabledAndPrompt()
                            else
                                displayNotFoundText(data.errorModel as BaseErrorModel)
                        }
                    }

                })
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(getString(R.string.alert_dialog_title))
            .setMessage(getString(R.string.alret_dialog_message))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch {
                    delay(1000)
                    requireActivity().finish()
                }
            }.show()
        speech.speak(
            getString(R.string.alret_dialog_message),
            TextToSpeech.QUEUE_FLUSH, null, ""
        )


    }

    private fun showProgress() {

        progress.visibility = VISIBLE
        weatherContainer.visibility = GONE
        errorView.visibility = GONE
    }


    private fun updateView(data: WeatherData?) {
        progress.visibility = GONE
        weatherContainer.visibility = VISIBLE
        errorView.visibility = GONE
        if (data != null) {
            val statement = weatherToText(data)
            speech.speak(statement, TextToSpeech.QUEUE_FLUSH, null, "")
            voiceInput.text = statement
        }

    }

    private fun weatherToText(data: WeatherData): String {
        val coord = data.coord
        val weather = data.weather[0]
        val temp: Double = data.main?.let { it.temp } ?: 0.0
        val wind = data.wind
        val sys = data.sys
        return "Hello, You are located in ${data.name}, ${sys.country}, " +
                "temperature is ${(((temp - 273.15) * 9 / 5) + 32).roundToLong()}  degree" +
                " Your coordinate is ${coord.lat} and ${coord.lon} " +
                " Today the weather is ${weather.description}, wind speed is ${wind.speed}" +
                " clouds are ${data.clouds.all} "


    }

    private fun displayNotFoundText(errorModel: BaseErrorModel) {
        when (errorModel.errorType) {
            Constants.EMPTY_MEMORY -> {
                errorModel.errorMessage = getString(R.string.location_not_available)
                errorModel.errorTitle = getString(R.string.default_error_title)
            }
        }
        showErrorView(errorModel.errorTitle, errorModel.errorMessage, false)
        val defaultErrorTitle = getString(R.string.default_error_title)
        val defaualtErrorMessage = getString(R.string.default_error_message)
        speech.speak("${errorModel.errorTitle?.let { it }
            ?: defaultErrorTitle}  unable to fetch data because of ${errorModel.errorMessage?.let { it }
            ?: defaualtErrorMessage}",
            TextToSpeech.QUEUE_FLUSH, null, ""
        )

    }

    private fun showErrorView(errorTitle: String?, errorMessage: String?, b: Boolean) {
        progress.visibility = GONE
        weatherContainer.visibility = GONE
        errorView.visibility = VISIBLE

        errorTitleTv.text = errorTitle ?: getString(R.string.default_error_title)
        errorSubtitleTv.text = errorMessage ?: getString(R.string.default_error_message)
        retryButton.visibility = when (b) {
            true -> VISIBLE
            else -> GONE
        }
        retryButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                mLocation?.let {
                    viewModel.refresh(
                        it
                    )
                }
            }
        }

    }


    override fun onStop() {
        speech.stop()
        speech.shutdown()
        super.onStop()
    }
}
