package com.textfield.json.outabus.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView

import com.textfield.json.outabus.entities.Bus
import com.textfield.json.outabus.R
import com.textfield.json.outabus.entities.Stop
import com.textfield.json.outabus.activities.TimeActivity

import java.util.ArrayList
import java.util.HashSet

/**
 * Created by Jason on 20/09/2015.
 */
class StopAdapter(context: Context, list: ArrayList<Stop>) : ArrayAdapter<Stop>(context, 0, list) {
    private val wholeList = list
    private var data = wholeList

    private class ViewHolder {
        var routenum: TextView? = null
        var buses: TextView? = null
        var ll: View? = null
        var stopId: TextView? = null
    }

    override fun getItem(position: Int): Stop {
        return data[position]
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val viewHolder: ViewHolder
        if (convertView == null) {

            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(context).inflate(R.layout.stop_item, parent, false)

            viewHolder.routenum = convertView!!.findViewById<TextView>(R.id.listtext)
            viewHolder.buses = convertView.findViewById<TextView>(R.id.textView)
            viewHolder.ll = convertView.findViewById(R.id.ll)
            viewHolder.stopId = convertView.findViewById<TextView>(R.id.stop_id)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val stop = getItem(position)
        viewHolder.routenum!!.text = stop.name
        viewHolder.stopId!!.text = stop.id
        viewHolder.buses!!.text = stop.buses.joinToString(separator = "\t", transform = {it.routeNumber})

        viewHolder.ll!!.setOnClickListener {
            val i = Intent(context, TimeActivity::class.java)

            val items = ArrayList<String>()
            for (x in stop.buses.indices) {
                items.add(stop.buses[x].toString())
            }
            val buses = HashSet<Bus>()
            val dialog = AlertDialog.Builder(context)
                    .setTitle(stop.name)
                    .setMultiChoiceItems(items.toTypedArray(), null) { dialog, indexSelected, isChecked ->
                        if (isChecked) {
                            buses.add(stop.buses[indexSelected])
                        } else if (buses.contains(stop.buses[indexSelected])) {
                            buses.remove(stop.buses[indexSelected])
                        }
                    }.setNegativeButton("Cancel") { dialogInterface, i -> }.setPositiveButton("Done") { dialog, id ->
                        if (buses.isNotEmpty()) {
                            i.putParcelableArrayListExtra("buses", ArrayList(buses))
                            i.putExtra("stop", stop.id)
                            i.putExtra("stopname", stop.name)
                            context.startActivity(i)
                        }
                    }.create()
            dialog.show()
        }


        return convertView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                data = results.values as ArrayList<Stop>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                val filteredResults = wholeList.filter {
                    it.name.contains(constraint, true) || it.id.contains(constraint)
                }

                val results = Filter.FilterResults()
                results.values = filteredResults

                return results
            }
        }
    }

}
