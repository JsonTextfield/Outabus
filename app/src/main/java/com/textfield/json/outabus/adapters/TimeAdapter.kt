package com.textfield.json.outabus.adapters

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.textfield.json.outabus.R

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

/**
 * Created by Jason on 20/09/2015.
 */
class TimeAdapter(context: Context, private val data: ArrayList<String>) : ArrayAdapter<String>(context, 0, data) {

    private class ViewHolder {
        var time: TextView? = null
        var timeUntil: TextView? = null
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder
        if (convertView == null) {

            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(context).inflate(R.layout.time_item, parent, false)

            viewHolder.time = convertView!!.findViewById(R.id.time) as TextView
            viewHolder.timeUntil = convertView.findViewById(R.id.timeuntil) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("HH:mm:ss")
        val date1: Date
        var date2 = Date(calendar.timeInMillis)
        val d2 = formatter.format(date2)

        try {
            date1 = formatter.parse(data[position])
            date2 = formatter.parse(d2)
            viewHolder.time!!.text = data[position]

            var different = date1.time - date2.time

            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60

            val elapsedHours = different / hoursInMilli
            different %= hoursInMilli

            val elapsedMinutes = different / minutesInMilli
            different %= minutesInMilli

            val elapsedSeconds = different / secondsInMilli

            var string = ""

            if (elapsedHours > 0) string += elapsedHours.toString() + " h "
            if (elapsedMinutes != 0L) string += Math.abs(elapsedMinutes).toString() + " m "
            if (elapsedMinutes >= 0 && elapsedHours < 1) string += Math.abs(elapsedSeconds).toString() + " s "
            if (elapsedMinutes < 0 || elapsedSeconds < 0)
                viewHolder.timeUntil!!.setTextColor(ContextCompat.getColor(context, R.color.red))
            else {
                viewHolder.timeUntil!!.setTextColor(Color.parseColor("#444444"))
            }
            viewHolder.timeUntil!!.text = string
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return convertView
    }

}
