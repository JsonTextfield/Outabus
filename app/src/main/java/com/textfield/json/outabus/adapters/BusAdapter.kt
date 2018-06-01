package com.textfield.json.outabus.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.RelativeLayout
import android.widget.TextView

import com.textfield.json.outabus.entities.Bus
import com.textfield.json.outabus.R
import com.textfield.json.outabus.activities.BusActivity

import java.util.ArrayList

/**
 * Created by Jason on 20/09/2015.
 */
class BusAdapter(context: Context, val list: ArrayList<Bus>) : ArrayAdapter<Bus>(context, 0, list) {
    private var data = list
    private val wholeList = list

    private class ViewHolder {
        var dest: TextView? = null
        var bound: TextView? = null
        var num: TextView? = null
        var layout: RelativeLayout? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder

        if (convertView == null) {

            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(context).inflate(R.layout.bus_item, parent, false)

            viewHolder.num = convertView!!.findViewById<View>(R.id.number) as TextView
            viewHolder.dest = convertView.findViewById<View>(R.id.destination) as TextView
            viewHolder.bound = convertView.findViewById<View>(R.id.bound) as TextView
            viewHolder.layout = convertView.findViewById<View>(R.id.busLayout) as RelativeLayout
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.bound!!.text = data[position].bearing
        viewHolder.num!!.text = data[position].routeNumber
        viewHolder.dest!!.text = data[position].destination
        viewHolder.layout!!.setOnClickListener {
            val i = Intent(context, BusActivity::class.java)
            i.putExtra("bus", data[position])
            context.startActivity(i)
        }
        return convertView
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                data = results.values as ArrayList<Bus>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                val filteredResults = wholeList.filter {
                    (it.destination.contains(constraint, true) || it.routeNumber == constraint.toString())
                }

                print(filteredResults)

                val results = Filter.FilterResults()
                results.values = filteredResults

                return results
            }
        }
    }

}
