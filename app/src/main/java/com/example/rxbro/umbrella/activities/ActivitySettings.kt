package com.example.rxbro.umbrella.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar

/**
 * Created by rxbro on 9/7/2017.
 */
class ActivitySettings : AppCompatActivity() {
    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        }
        Log.d(TAG, "Activity has been inflated")
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        super.onBackPressed()
        Log.d(TAG, "Activity settings is terminated and returned to Main Activity")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == android.R.id.home) {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            Log.d(TAG, "Activity settings is terminated and returned to Main Activity")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    companion object {
        private val TAG = ActivitySettings::class.java.name
    }


}