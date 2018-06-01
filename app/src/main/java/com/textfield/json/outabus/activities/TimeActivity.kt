package com.textfield.json.outabus.activities

import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.textfield.json.outabus.entities.Bus
import com.textfield.json.outabus.R
import com.textfield.json.outabus.adapters.TimeAdapter
import com.textfield.json.outabus.util.DB

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

class TimeActivity : GenericActivity() {
    internal lateinit var arrayAdapter: TimeAdapter

    fun refresh() {
        val mDbHelper = DB(this)
        val b = intent.extras

        setSmallTitle(b!!.getString("stopname")!!)

        val calendar = Calendar.getInstance()
        var day = ""
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            day = "Dimanche"
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            day = "Samedi"
        } else {
            day = "Semaine"
        }

        val formatter = SimpleDateFormat("HH:mm:ss")
        val ONE_MINUTE_IN_MILLIS: Long = 60000//millisecs
        val t = calendar.timeInMillis
        val afterAddingTenMins = Date(t - 10 * ONE_MINUTE_IN_MILLIS)
        val limittime = Date(t + 6 * 3600000)
        val time = formatter.format(afterAddingTenMins)
        val limit = formatter.format(limittime)

        var routes = ""
        val busList = b.getParcelableArrayList<Bus>("buses")

        for (i in busList!!.indices) {
            if (i < busList.size - 1)
                routes += "route_id = '" + busList[i].id + "' or "
            else
                routes += "route_id = '" + busList[i].id
        }
        mDbHelper.open()
        var cursor: Cursor? = null
        try {
            if (formatter.parse(time).time >= 6.48e+7) {
                cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                        + b.getString("stop")
                        + "' and datetime(arrival) > datetime('" + time + "') and datetime(arrival) < datetime('" + limit + "', '+24 hours')"
                        + "and trip_id like ('%" + day + "%') and ("
                        + routes
                        + "') group by arrival order by datetime(arrival) asc;")
                //cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                //        + b.getString("stop")+ "' and (" + routes + "') group by arrival order by datetime(arrival) asc;");
            } else {
                cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                        + b.getString("stop")
                        + "' and datetime(arrival) > datetime('" + time + "') and datetime(arrival) < datetime('" + limit + "')"
                        + "and trip_id like ('%" + day + "%') and ("
                        + routes
                        + "') group by arrival order by datetime(arrival) asc;")
                //cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                //        + b.getString("stop")+ "' and (" + routes + "') group by arrival order by datetime(arrival) asc;");

            }
        } catch (e: ParseException) {
        }

        val list = ArrayList<String>()

        do {
            if (cursor!!.count > 0)
                list.add(cursor.getString(0) + "\n" + cursor.getString(1) + " " + cursor.getString(2))
        } while (cursor!!.moveToNext())
        mDbHelper.close()

        arrayAdapter = TimeAdapter(this, list)
        listView.adapter = arrayAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_time, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == android.R.id.home) {
            super.onBackPressed()

        }
        if (id == R.id.refresh) {
            refresh()
        }
        return super.onOptionsItemSelected(item)
    }
}
