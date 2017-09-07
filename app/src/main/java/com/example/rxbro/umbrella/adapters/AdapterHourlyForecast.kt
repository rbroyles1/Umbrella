package com.example.rxbro.umbrella.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.android.volley.toolbox.NetworkImageView
import com.example.rxbro.umbrella.models.HourlyForecastModel
import com.example.rxbro.umbrella.utils.NetworkControllerSingleton

import java.util.ArrayList

/**
 * Created by rxbro on 9/7/2017.
 */

class AdapterHourlyForecast(private val context: Context, private val hourlyForecastList: List<HourlyForecastModel>) : RecyclerView.Adapter<AdapterHourlyForecast.ViewHolder>() {
    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return hourlyForecastList.size
    }

    inner class ViewHolder(itemView: View, var context: Context, hourlyForecast: ArrayList<HourlyForecastModel>) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var hourlyForecast = ArrayList<HourlyForecastModel>()
        val hourlyWeekDayForecastTV: TextView
        val hourlyTimeForecastTV: TextView
        val hourlyTempForecastTV: TextView
        val hourlyWeatherIconNIV: NetworkImageView

        init {
            this.hourlyForecast = hourlyForecast
            hourlyWeekDayForecastTV = itemView.findViewById(R.id.hourlyWeekDayForecastTV) as TextView
            hourlyTimeForecastTV = itemView.findViewById(R.id.hourlyTimeForecastTV) as TextView
            hourlyTempForecastTV = itemView.findViewById(R.id.hourlyTempForecastTV) as TextView
            hourlyWeatherIconNIV = itemView.findViewById(R.id.hourlyWeatherIconNIV) as NetworkImageView
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            Toast.makeText(context, position.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view, context, hourlyForecastList as ArrayList<HourlyForecastModel>)
    }

    override fun onBindViewHolder(viewHolder: AdapterHourlyForecast.ViewHolder, position: Int) {
        viewHolder.itemView.tag = position
        val hourlyForecastModel = hourlyForecastList[position]
        viewHolder.hourlyWeekDayForecastTV.text = hourlyForecastModel.hourlyWeekDay
        viewHolder.hourlyTimeForecastTV.text = hourlyForecastModel.hourlyTime
        viewHolder.hourlyTempForecastTV.text = hourlyForecastModel.hourlyTemperature!! + context.getString(R.string.degree_symbol)
        viewHolder.hourlyWeatherIconNIV.setImageUrl(hourlyForecastModel.hourlyIconURL, NetworkControllerSingleton.getInstance(context).getImageLoader())
    }

}
