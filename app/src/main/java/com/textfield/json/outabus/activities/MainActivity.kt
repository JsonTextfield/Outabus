package com.textfield.json.outabus.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.textfield.json.outabus.R
import com.textfield.json.outabus.util.DB

class MainActivity : GenericActivity() {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                finish()
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }

        val db = DB(this)
        db.createDatabase()

        listView.adapter = MainAdapter()
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> {
                    startActivity(Intent(this@MainActivity, BusListActivity::class.java))
                }
                1 -> {
                    startActivity(Intent(this@MainActivity, StopListActivity::class.java))
                }
                2 -> {
                }
            }
        }
    }

    private inner class MainAdapter : ArrayAdapter<String>(this@MainActivity, 0, resources.getStringArray(R.array.main)) {

        inner class ViewHolder {
            var title: TextView? = null
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            var convertView = view
            val viewHolder: ViewHolder
            if (convertView == null) {
                viewHolder = ViewHolder()
                convertView = LayoutInflater.from(context).inflate(R.layout.main_item, parent, false)
                viewHolder.title = convertView!!.findViewById<TextView>(R.id.title)
            } else {
                viewHolder = convertView.tag as ViewHolder
            }
            viewHolder.title?.text = getItem(position)
            return convertView
        }
    }

    companion object {
        private const val REQUEST_LOCATION = 4
    }
}
