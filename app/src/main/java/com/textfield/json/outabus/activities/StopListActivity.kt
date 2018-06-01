package com.textfield.json.outabus.activities

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem

import com.textfield.json.outabus.entities.Bus
import com.textfield.json.outabus.R
import com.textfield.json.outabus.entities.Stop
import com.textfield.json.outabus.adapters.StopAdapter
import com.textfield.json.outabus.util.DB

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashMap
import java.util.LinkedHashSet

/**
 * Created by Jason on 19/04/2016.
 */
class StopListActivity : GenericActivity() {
    internal lateinit var arrayAdapter: StopAdapter
    private var lat = 0.0
    private var lng = 0.0
    private var list = ArrayList<Stop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var location: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        lat = 45.5
        lng = -79.6

        val file = File(cacheDir, "stops")
        if (file.exists()) {
            try {
                val bufferedReader = BufferedReader(FileReader(file))
                var x: String
                /*while ((x = bufferedReader.readLine()) != null) {
                    //System.out.println(x);
                    val busList = ArrayList<Bus>()
                    val jsonObject = JSONObject(x)
                    val buses = jsonObject.getJSONArray("buses")
                    for (i in 0 until buses.length()) {
                        val obj = buses.getJSONObject(i)
                        busList.add(Bus(obj.getString("routenum"), obj.getString("route_id"), obj.getString("destination"), obj.getInt("direction")))
                    }
                    list.add(Stop(jsonObject.getString("stop_id"),
                            jsonObject.getString("name"),
                            jsonObject.getDouble("lat"),
                            jsonObject.getDouble("lng"),
                            busList))

                }*/
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {
            val results = HashMap<Stop, LinkedHashSet<Bus>>()
            val stops = HashMap<String, JSONObject>()

            val mDbHelper = DB(this)
            mDbHelper.open()
            val cursor = mDbHelper.runQuery("select * from routes natural join busroutes natural join stops group by stop_id,route_id order by routenum*1 asc;")

            do {
                val s = Stop(cursor!!.getString(cursor.getColumnIndex("stop_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getDouble(cursor.getColumnIndex("latitude")),
                        cursor.getDouble(cursor.getColumnIndex("longitude")))
                if (results[s] == null) {
                    results[s] = LinkedHashSet()

                    try {
                        val jsonObject = JSONObject()
                        jsonObject.put("name", s.name)
                        jsonObject.put("stop_id", s.id)
                        jsonObject.put("lat", s.lat)
                        jsonObject.put("lng", s.lng)

                        stops[s.id] = jsonObject
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                val b = Bus(cursor.getString(cursor.getColumnIndex("routenum")),
                        cursor.getString(cursor.getColumnIndex("route_id")),
                        cursor.getString(cursor.getColumnIndex("destination")),
                        cursor.getInt(cursor.getColumnIndex("direction")))
                results[s]?.add(b)

                try {
                    val bus = JSONObject()
                    bus.put("routenum", b.routeNumber)
                    bus.put("route_id", b.id)
                    bus.put("direction", b.direction)
                    bus.put("destination", b.destination)
                    try {
                        stops[s.id]?.getJSONArray("buses")
                    } catch (e: JSONException) {
                        stops[s.id]?.put("buses", JSONArray())
                    }

                    stops[s.id]?.getJSONArray("buses")?.put(bus)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } while (cursor!!.moveToNext())
            mDbHelper.close()

            try {
                val bufferedWriter = BufferedWriter(FileWriter(file))
                for (i in results.keys) {
                    bufferedWriter.write(stops[i.id].toString() + "\n")
                    //i.buses.addAll(results[i])

                    if (i.buses.size > 0)
                        list.add(i)
                }
                bufferedWriter.close()
            } catch (e: IOException) {
            }

        }
        /*for(Stop stop : stopList){
            if(stop.getBuses().size() == 0){
                stopList.remove(stop);
            }
        }*/
        Collections.sort(list, Comparator { lhs, rhs ->
            val res = FloatArray(1)
            val res2 = FloatArray(1)

            Location.distanceBetween(lat, lng, lhs.lat, lhs.lng, res)
            Location.distanceBetween(lat, lng, rhs.lat, rhs.lng, res2)
            if (res[0] > res2[0]) return@Comparator 1
            if (res[0] < res2[0]) -1 else 0
        })

        arrayAdapter = StopAdapter(this, list)
        listView.adapter = arrayAdapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == android.R.id.home) {
            super.onBackPressed()
        }
        if (id == R.id.map) {
            val b = Bundle()
            println(list.size)
            b.putParcelableArrayList("stops", ArrayList(list.subList(0, 50)))
            b.putString("type", "stop")
            val i = Intent(this, MapsActivity::class.java)
            i.putExtras(b)
            startActivity(i)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_stop, menu)
        val searchView = MenuItemCompat.getActionView(menu.findItem(R.id.searchView)) as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                arrayAdapter.filter.filter(newText)
                return true
            }
        })

        return true
    }
}
