package com.example.rxbro.umbrella.fragments

import android.app.Fragment
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.example.rxbro.umbrella.R
import com.example.rxbro.umbrella.activities.MainActivity
import com.example.rxbro.umbrella.adapters.AdapterHourlyForecast
import com.example.rxbro.umbrella.helpers.DividerItemDecoration
import com.example.rxbro.umbrella.models.CurrentForecastModel
import com.example.rxbro.umbrella.models.HourlyForecastModel
import com.example.rxbro.umbrella.sharedPreferences.SharedPref
import com.example.rxbro.umbrella.utils.NetworkControllerSingleton

import org.json.JSONException
import java.lang.Double

import java.util.ArrayList


/**
 * Created by rxbro on 9/7/2017.
 */

class FragmentWeatherForecast : Fragment() {

    private var _view = null
    private var view: Nothing?
        get() = _view
        set(value) {
            _view = value
        }
    private var sharedPrefUmbrella: SharedPref? = null
    private var requestQueue: RequestQueue? = null
    private var editText: EditText? = null
    private var temperatureUnitPreferences: Int = 0
    private var stringToInt: Int = 0
    var parseZipCodeStringToInt: Int = 0

    private val modelHourlyForecastList = ArrayList<HourlyForecastModel>()
    private var adapterHourlyForecast: AdapterHourlyForecast? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        _view = inflater.inflate(R.layout.fragment_main, container, false) as Nothing?
        retainInstance = true

        requestQueue = NetworkControllerSingleton.getInstance(context).getRequestQueue()
        sharedPrefUmbrella = SharedPref(context)
        @Suppress("INACCESSIBLE_TYPE")
        val calendarIV = view!!.findViewById(R.id.calendarIV) as ImageButton
        @Suppress("INACCESSIBLE_TYPE")
        val clockIV = view!!.findViewById(R.id.clockIV) as ImageButton
        @Suppress("INACCESSIBLE_TYPE")
        val temperatureIV = view!!.findViewById(R.id.temperatureIV) as ImageButton
        @Suppress("INACCESSIBLE_TYPE")
        val weatherIconIV = view!!.findViewById(R.id.weatherIconIV) as ImageButton
        @Suppress("INACCESSIBLE_TYPE")
        val hourlyForecastRV = view!!.findViewById(R.id.hourlyForecastRV) as RecyclerView
        adapterHourlyForecast = AdapterHourlyForecast(context, modelHourlyForecastList)

        calendarIV.setOnClickListener { view -> Snackbar.make(view, "Weekday.", Snackbar.LENGTH_SHORT).show() }

        clockIV.setOnClickListener { view -> Snackbar.make(view, "Forecast time.", Snackbar.LENGTH_SHORT).show() }

        temperatureIV.setColorFilter(ContextCompat.getColor(context, R.color.black))
        temperatureIV.setOnClickListener { view -> Snackbar.make(view, "Temperature.", Snackbar.LENGTH_SHORT).show() }

        weatherIconIV.setOnClickListener { view -> Snackbar.make(view, "Weather Condition.", Snackbar.LENGTH_SHORT).show() }

        val gridLayoutManager = GridLayoutManager(context, 4)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        hourlyForecastRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        hourlyForecastRV.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        hourlyForecastRV.itemAnimator = DefaultItemAnimator()
        hourlyForecastRV.adapter = adapterHourlyForecast

        Log.d(TAG, "All views have inflated.")
        return view
    }

    override fun onResume() {
        super.onResume()
        loadWeatherItems()
        Log.d(TAG, "Fragment has resumed successfully by loading/reloading weather downloaded data")
    }

    private fun downloadWeatherData() {

        val URL_JSON_ENDPOINT = "http://api.wunderground.com/api/0282885b06f66012/conditions/hourly/q/"
        val appendZipCodeToURL = URL_JSON_ENDPOINT + sharedPrefUmbrella!!.zipCode.toString() + ".json"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, appendZipCodeToURL, null, Listener { response ->
            Log.d("TAG goes here", response.toString())
            adapterHourlyForecast!!.notifyDataSetChanged()
            modelHourlyForecastList.clear()

            var i = 0
            try {
                val currentObservation = response.getJSONObject("current_observation")
                val city = currentObservation.getJSONObject("display_location")

                val cityName = city.getString("full")
                val currentTemperatureFahrenheit = currentObservation.getString("temp_f")
                val currentTemperatureCelcius = currentObservation.getString("temp_c")
                val currentWeatherCondition = currentObservation.getString("weather")
                var convertedTemp = getString(R.string.empty_string_placeholder)

                val modelCurrentForecast = CurrentForecastModel(cityName, currentTemperatureFahrenheit, currentTemperatureCelcius, currentWeatherCondition)

                temperatureUnitPreferences = sharedPrefUmbrella!!.temperatureUnit
                if (temperatureUnitPreferences == ZERO_INTEGER_VALUE) {
                    stringToInt = Double.parseDouble(modelCurrentForecast.currentTemperatureFahrenheit.toString()).toInt()
                    setToolbarColorFahrenheit(stringToInt)
                    convertedTemp = stringToInt.toString() + "°"
                    Log.d(TAG, "Shared preferences provides Fahrenheit as the temperature unit to be used.")
                } else if (temperatureUnitPreferences == ONE_INTEGER_VALUE) {
                    stringToInt = Double.parseDouble(modelCurrentForecast.currentTemperatureCelsius.toString()).toInt()
                    setToolbarColorCelsius(stringToInt)
                    convertedTemp = stringToInt.toString() + "°"
                    Log.d(TAG, "Shared preferences provides Celsius as the temperature unit to be used.")
                }
                (activity as MainActivity).changeToolbarTextView(CurrentForecastModel.currentCity, convertedTemp, modelCurrentForecast.currentWeatherCondition)
                Log.d(TAG, "Toolbar has been updated with the current city, current temp formatted based on unit from shared preferences, and the current weather condition is listed")


                val url = "http://nerdery-umbrella.s3.amazonaws.com/"


                var temperatureUnit = ""
                val jsonArray = response.getJSONArray("hourly_forecast")
                i = 0
                while (i < jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val timeObject = jsonObject.getJSONObject("FCTTIME")
                    val tempObject = jsonObject.getJSONObject("temp")

                    val icon = jsonObject.getString("icon")
                    val weekDay = timeObject.getString("weekday_name")
                    val time = timeObject.getString("civil")
                    val appendWeatherIconString = url + icon + ".png"

                    temperatureUnitPreferences = sharedPrefUmbrella!!.temperatureUnit
                    if (temperatureUnitPreferences == ZERO_INTEGER_VALUE) {
                        temperatureUnit = tempObject.getString("english")
                        Log.d(TAG, "If shared preferences are set to Fahrenheit, then the downloaded JSON imperial units will be used for temperature.")
                    } else if (temperatureUnitPreferences == ONE_INTEGER_VALUE) {
                        temperatureUnit = tempObject.getString("metric")
                        Log.d(TAG, "IF shared preferences are set to celsius, then the downloaded JSON metric units will be used for temperature.")
                    }

                    val modelHourlyForecast = HourlyForecastModel(weekDay, time, temperatureUnit, appendWeatherIconString)
                    modelHourlyForecastList.add(modelHourlyForecast)
                    adapterHourlyForecast!!.notifyItemChanged(i)
                    Log.d(TAG, "Setting the downloaded JSON values to the hourly weather forecast constructor and then notifying the adapter.")
                    i++
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                this!!.view?.let { Snackbar.make(it, R.string.json_error_exception_message, Snackbar.LENGTH_LONG).show() }
                //((ActivityMain) getActivity()).changeToolbarTextView(getString(R.string.double_dash_placeholder), getString(R.string.double_dash_placeholder), getString(R.string.double_dash_placeholder));

                Log.d(TAG, e.message)
            } finally {
                Log.d(TAG, "JSON download successful!")
                adapterHourlyForecast!!.notifyItemChanged(i)
            }
        },
                Response.ErrorListener { error ->
                    VolleyLog.d(TAG, "Volley Error: " + error.message)
                    Log.d(TAG, "Volley Error: " + error.message)
                    val snackbar = view?.let { Snackbar.make(it, R.string.no_internet_connection_message, Snackbar.LENGTH_INDEFINITE) }
                    snackbar.setAction(R.string.dismiss_label, View.OnClickListener { snackbar.dismiss() })
                    snackbar.show()
                    Log.d(TAG, "Snackbar shows a message to user that an error has occured and data cannot be downloaded.")
                })
        requestQueue!!.add(jsonObjectRequest)
        Log.d(TAG, "Web requests had been stacked and caching is set up.")
    }

    private fun setToolbarColorFahrenheit(toolbarColor: Int): Int {
        val SIXTY = 60
        if (stringToInt >= SIXTY) {
            (activity as MainActivity).supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.weather_warm)))
            Log.d(TAG, "The toolbar has been set to orange because the temperature is greater than or equal to 60 degrees fahrenheit.")
        } else {
            (activity as MainActivity).supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.weather_cool)))
            Log.d(TAG, "The toolbar has been set to blue because the temperature is less than 60 degrees fahrenheit.")
        }
        return toolbarColor
    }

    private fun setToolbarColorCelsius(toolbarColor: Int): Int {
        val SIXTEEN = 16
        if (stringToInt >= SIXTEEN) {
            (activity as MainActivity).supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.weather_warm)))
            Log.d(TAG, "The toolbar has been set to orange because the temperature is greater than or equal to 16 degrees celsius.")
        } else {
            (activity as MainActivity).supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.weather_cool)))
            Log.d(TAG, "The toolbar has been set to blue because the temperature is less than 16 degrees celsius.")
        }
        return toolbarColor
    }

    private fun checkForZipCode() {
        val zipCodeSharedPrefs = sharedPrefUmbrella!!.zipCode

        if (zipCodeSharedPrefs == 0) {
            val enterZipCodeDialog = AlertDialog.Builder(context)
            val enterZipCodeView = View.inflate(context, R.layout.dialog_fragment_save_zip_code, null)
            enterZipCodeDialog.setTitle(R.string.zip_code_title)
            enterZipCodeDialog.setView(enterZipCodeView)
            enterZipCodeDialog.setCancelable(false)

            enterZipCodeDialog.setPositiveButton(R.string.save_button_label, DialogInterface.OnClickListener { dialogInterface, i ->
                @Suppress("INACCESSIBLE_TYPE")
                editText = enterZipCodeView.findViewById(R.id.editTextMeetingDescription) as EditText
                val zipCodeEntry = editText!!.text.toString()
                try {
                    parseZipCodeStringToInt = Integer.parseInt(zipCodeEntry)
                    sharedPrefUmbrella!!.saveZipCode(parseZipCodeStringToInt)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                downloadWeatherData()
                Log.d(TAG, "Alert dialog positive button has been pressed.")
            })


            enterZipCodeDialog.setNegativeButton(R.string.cancel_button_label, DialogInterface.OnClickListener { dialogInterface, i -> Log.d(TAG, "Alert dialog negative button has been pressed.") })
            enterZipCodeDialog.show()
            Log.d(TAG, "Checking status of zip code by querying shared preferences. If shared preferences are zero, then an alert dialog opens for the user to enter a zip code.")
        } else {
            downloadWeatherData()
            Log.d(TAG, "Zip code is not zero thus download weather data")
        }
    }

    fun loadWeatherItems() {
        checkForZipCode()
        Log.d(TAG, "Loading JSON data and making sure it's completed successfully.")
    }

    companion object {
        private val TAG = FragmentWeatherForecast::class.java.name
        private val ZERO_INTEGER_VALUE = 0
        private val ONE_INTEGER_VALUE = 1
    }
}

private fun Nothing.findViewById(hourlyForecastRV: Int) {}
