package com.textfield.json.outabus.activities

import android.content.Intent
import android.database.CursorIndexOutOfBoundsException
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.textfield.json.outabus.R
import com.textfield.json.outabus.adapters.StopAdapter
import com.textfield.json.outabus.entities.Bus
import com.textfield.json.outabus.entities.Stop
import com.textfield.json.outabus.util.DB
import java.util.*

class BusActivity : GenericActivity() {
    private var bus = Bus()
    private var stopList = ArrayList<Stop>()
    internal lateinit var arrayAdapter: StopAdapter

    private fun setup(bus: Bus) {
        this.bus = bus
        stopList.clear()

        setSmallTitle(bus.toString())

        val mDbHelper = DB(this)

        mDbHelper.open()

        var cursor = mDbHelper.runQuery("select * from stops natural join busroutes where route_id = '$bus.id' and direction = $bus.direction group by stop_id order by stop_number,stop_id;")

        val results = LinkedHashMap<Stop, LinkedHashSet<Bus>>()
        val results2 = LinkedHashMap<Stop, LinkedHashSet<Bus>>()

        do {
            val s = Stop(cursor!!.getString(cursor.getColumnIndex("stop_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("latitude")),
                    cursor.getDouble(cursor.getColumnIndex("longitude")))
            if (results[s] == null) {
                results[s] = LinkedHashSet()
            }
            stopList.add(s)
        } while (cursor!!.moveToNext())

        val string = results.keys.joinToString(prefix = "where stop_id = '", separator = "' or stop_id = '", postfix = "'")
        println(string)
        cursor = mDbHelper.runQuery("select * from routes natural join busroutes natural join stops $string group by route_id,stop_id order by route_id*1 asc;")
        do {
            val stop = Stop(cursor!!.getString(cursor.getColumnIndex("stop_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("latitude")),
                    cursor.getDouble(cursor.getColumnIndex("longitude")))

            if (results2[stop] == null) {
                results2[stop] = LinkedHashSet()
            }
            results2[stop]?.add(Bus(cursor.getString(cursor.getColumnIndex("routenum")),
                    cursor.getString(cursor.getColumnIndex("route_id")),
                    cursor.getString(cursor.getColumnIndex("destination")),
                    cursor.getInt(cursor.getColumnIndex("direction"))))
        } while (cursor!!.moveToNext())
        mDbHelper.close()

        for (i in results2.keys) {
            if (results2[i]!!.isEmpty())
                stopList.removeAt(stopList.indexOf(i))
            else
                stopList[stopList.indexOf(i)].buses = ArrayList(results2[i])

        }

        arrayAdapter = StopAdapter(this, stopList)
        listView.adapter = arrayAdapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.bus = intent.extras!!.getParcelable("bus")
        setup(bus)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_bus, menu)
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    arrayAdapter.filter.filter("")
                    //listView.clearTextFilter();
                } else {
                    arrayAdapter.filter.filter(newText)
                }
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId){
            android.R.id.home -> finish()

            R.id.switch_dir -> {
                val mDbHelper = DB(this)
                mDbHelper.open()
                val cursor = mDbHelper.runQuery("select * from routes join trips on trips.route_id = routes.route_id " +
                        "where trips.route_id = \"" + bus.id + "\" and direction = " +
                        (bus.direction + 1) % 2 + " group by routenum, direction order by routenum;")
                try {
                    val autobus = Bus(cursor!!.getString(cursor.getColumnIndex("routenum")), cursor.getString(cursor.getColumnIndex("route_id")),
                            cursor.getString(cursor.getColumnIndex("destination")), cursor.getInt(cursor.getColumnIndex("direction")))
                    println(autobus)
                    setup(autobus)
                } catch (e: CursorIndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }

            R.id.map -> {
                val b = Bundle()
                b.putParcelableArrayList("stops", stopList)
                b.putString("type", "bus")
                val i = Intent(this, MapsActivity::class.java)
                i.putExtras(b)
                startActivity(i)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
