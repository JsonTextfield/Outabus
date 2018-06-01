package com.textfield.json.outabus.activities

import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem

import com.textfield.json.outabus.entities.Bus
import com.textfield.json.outabus.adapters.BusAdapter
import com.textfield.json.outabus.R
import com.textfield.json.outabus.util.DB

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap

/**
 * Created by Jason on 19/04/2016.
 */
class BusListActivity : GenericActivity() {
    internal lateinit var arrayAdapter: BusAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mDbHelper = DB(this)

        val calendar = Calendar.getInstance()
        val day = if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            "Dimanche"
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            "Samedi"
        } else {
            "Semaine"
        }

        val list = ArrayList<Bus>()
        val file = File(cacheDir, "buses")
        if (file.exists()) {
            try {
                file.inputStream().bufferedReader().use { list.add(Bus(JSONObject(it.readLine()))) }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {
            mDbHelper.open()
            var cursor = mDbHelper.runQuery("select * from routes join trips on trips.route_id = routes.route_id where trip_id like '%$day%' group by routenum, direction order by routenum;")
            //Cursor cursor = mDbHelper.runQuery("select * from routes natural join trips group by routenum,direction order by routenum*1;");
            val buses = HashMap<String, JSONObject>()
            do {
                val bus = JSONObject()
                val b = Bus(cursor!!.getString(cursor.getColumnIndex("routenum")), cursor.getString(cursor.getColumnIndex("route_id")),
                        cursor.getString(cursor.getColumnIndex("destination")), cursor.getInt(cursor.getColumnIndex("direction")))
                list.add(b)
                try {
                    bus.put("routenum", b.routeNumber)
                    bus.put("direction", b.direction)
                    bus.put("destination", b.destination)
                    bus.put("route_id", b.id)
                    buses[bus.getString("route_id") + bus.getString("direction")] = bus
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } while (cursor!!.moveToNext())

            for (b in list) {
                cursor = mDbHelper.runQuery("select latitude,longitude from stops natural join busroutes where route_id = '$b.id' and direction = $b.direction order by stop_number asc;")

                cursor!!.moveToFirst()
                val point1 = doubleArrayOf(Math.toRadians(cursor.getDouble(0)), Math.toRadians(cursor.getDouble(1)))
                cursor.moveToLast()
                val point2 = doubleArrayOf(Math.toRadians(cursor.getDouble(0)), Math.toRadians(cursor.getDouble(1)))


                val longDiff = point2[1] - point1[1]
                val y = Math.sin(longDiff) * Math.cos(point2[0])
                val x = Math.cos(point1[0]) * Math.sin(point2[0]) - Math.sin(point1[0]) * Math.cos(point2[0]) * Math.cos(longDiff)

                val compassBearing = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360

                //System.out.println("bearing " + compassBearing);
                b.bearing =
                        if (compassBearing >= 45 && compassBearing < 135) {
                            "Eastbound"
                        } else if (compassBearing >= 135 && compassBearing < 225) {
                            "Southbound"
                        } else if (compassBearing >= 225 && compassBearing < 315) {
                            "Westbound"
                        } else {
                            "Northbound"
                        }
                try {
                    buses[b.id + b.direction]?.put("bearing", b.bearing)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            try {
                val bufferedWriter = BufferedWriter(FileWriter(file))
                for (thing in buses.keys) {
                    bufferedWriter.write(buses[thing].toString() + "\n")
                }
                bufferedWriter.close()
            } catch (e: IOException) {
            }

        }
        mDbHelper.close()
        list.sort()

        arrayAdapter = BusAdapter(this, list)
        listView.adapter = arrayAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bus_list, menu)
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                arrayAdapter.filter.filter(newText)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
