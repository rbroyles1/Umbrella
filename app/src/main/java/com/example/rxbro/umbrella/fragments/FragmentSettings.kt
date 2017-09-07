package com.example.rxbro.umbrella.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log

import com.example.rxbro.umbrella.sharedPreferences.SharedPref

/**
 * Created by rxbro on 9/7/2017.
 */

class FragmentSettings : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var context:Context? = null
    lateinit var preferences: SharedPreferences
    private var sharedPrefUmbrella: SharedPref? = null
    lateinit var editTextPreference: EditTextPreference

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        addPreferencesFromResource(R.xml.preferences)

        for (i in 0..preferenceScreen.preferenceCount - 1) {
            initializeSummary(preferenceScreen.getPreference(i))
        }
        Log.d(TAG, "Preference fragment settings have been looped through to show current settings state.")
        Log.d(TAG, "Preference fragment view has been inflated.")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle) {
        super.onViewStateRestored(savedInstanceState)
        context = activity
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefUmbrella = SharedPref(context!!)
        Log.d(TAG, "Views successfully restored on fragment inflation.")
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        editTextPreference.text = sharedPrefUmbrella!!.zipCode.toString()
        Log.d(TAG, "Resuming fragment lifecycle, registering the shared preferences change listener, and ensuring the zip code from shared preferences is shown in the summary.")
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        Log.d(TAG, "Pausing the fragment lifecycle and unregistering the shared preferences change listener.")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        try {
            sharedPrefUmbrella = SharedPref(context!!)
            preferences = PreferenceManager.getDefaultSharedPreferences(context)

            updatePreference(findPreference(key))

            val zipCodeEntry = editTextPreference.text
            val parseZipCodeStringToInt = Integer.parseInt(zipCodeEntry)
            sharedPrefUmbrella!!.saveZipCode(parseZipCodeStringToInt)

            val temperatureUnitPreferences: Int
            if (preferences.getString(TEMPERATURE_UNIT_CHOICES, "") == "Fahrenheit (℉)") {
                temperatureUnitPreferences = ZERO_INTEGER_VALUE
                sharedPrefUmbrella!!.saveTemperatureUnit(temperatureUnitPreferences)
                Log.d(TAG, "Changed to F -- " + sharedPrefUmbrella!!.zipCode)
            } else if (preferences.getString(TEMPERATURE_UNIT_CHOICES, "") == "Celsius (℃)") {
                temperatureUnitPreferences = ONE_INTEGER_VALUE
                sharedPrefUmbrella!!.saveTemperatureUnit(temperatureUnitPreferences)
                Log.d(TAG, "Changed to C -- " + sharedPrefUmbrella!!.zipCode)
            }
            Log.d(TAG, "Updated the shared preferences file.")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, e.message)
        }

    }

    private fun initializeSummary(preference: Preference) {
        if (preference is PreferenceCategory) {
            val preferenceCategory = preference
            for (i in 0..preference.preferenceCount - 1) {
                initializeSummary(preference.getPreference(i))
            }
        } else {
            updatePreference(preference)
            Log.d(TAG, "Updating preference screen fragment summaries.")
        }
    }

    private fun updatePreference(preference: Preference) {
        if (preference is EditTextPreference) {
            editTextPreference = preference
            preference.setSummary(editTextPreference.text)
            Log.d(TAG, "Setting the zip code edit text preference summary to zip code from what is entered by user.")
        }
    }

    companion object {

        private val TAG = FragmentSettings::class.java.name
        private val TEMPERATURE_UNIT_CHOICES = "preferencesTemperatureUnitChoices"
        private val ZERO_INTEGER_VALUE = 0
        private val ONE_INTEGER_VALUE = 1
    }
}
