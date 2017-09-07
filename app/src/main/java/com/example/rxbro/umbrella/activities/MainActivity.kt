package com.example.rxbro.umbrella.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    internal var toolbar : Toolbar
    internal var appTitle : TextView
    internal var currentTemperature : TextView
    internal var currentConditions : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        appTitle = toolbar.findViewById(R.id.appTitle) as TextView
        currentTemperature = toolbar.findViewById(R.id.currentTemperature) as TextView
        currentConditions = toolbar.findViewById(R.id.currentConditions) as TextView
        val fragmentManager = getSupportFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragmentWeatherForecast = FragmentWeatherForecast()
        fragmentTransaction.replace(R.id.fragment_container, FragmentWeatherForecast, "fragmentWeatherForecast")
        if (fragmentManager.findFragmentByTag("fragmentWeatherForecast") == null) {
            fragmentTransaction.commit()
            Log.d(TAG, "Fragment transaction committed")

        }
        Log.d(TAG, "Activity has been inflated.")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Activity onResume called")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        Log.d(TAG, "Activity Main is terminated")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu)
        Log.d(TAG, "Create options menu is called in Activity Main")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, ActivitySettings::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            Log.d(TAG, "Overflow menu settings icon is pressed to open the settings activity/fragment")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    fun changeToolbarTextView(city:String, temperature:String, condition:String) {
        appTitle.setText(city)
        currentTemperature.setText(temperature)
        currentConditions.setText(condition)
        Log.d(TAG, "Method that accepts arguments for its parameters to change the toolbar information.")
    }
    companion object {
        private val TAG = MainActivity::class.java.name
    }
}
