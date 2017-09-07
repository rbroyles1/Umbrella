package com.example.rxbro.umbrella.sharedPreferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by rxbro on 9/7/2017.
 */

class SharedPref(context: Context) {
    private val ZERO = 0
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor
    val temperatureUnit: Int
        get() {
            val temperatureUnit: Int
            temperatureUnit = sharedPreferences.getInt(TEMPERATURE_UNIT, ZERO)
            return temperatureUnit
        }
    val zipCode: Int
        get() {
            val zipCode: Int
            zipCode = sharedPreferences.getInt(ZIP_CODE, ZERO)
            return zipCode
        }

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, ZERO)
        editor = sharedPreferences.edit()

    }

    fun saveTemperatureUnit(temperatureUnit: Int) {
        editor.putInt(TEMPERATURE_UNIT, temperatureUnit)
        editor.apply()
    }

    fun saveZipCode(zipCode: Int) {
        editor.putInt(ZIP_CODE, zipCode)
        editor.apply()
    }

    companion object {
        private val SHARED_PREFERENCES_NAME = "com.example.rxbro.umbrella"
        private val TEMPERATURE_UNIT = "temperatureUnit"
        private val ZIP_CODE = "zipCode"
    }


}
