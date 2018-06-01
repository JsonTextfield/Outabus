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
    private var data = list
    private val wholeList = list

    private class ViewHolder {
        var routenum: TextView? = null
        var buses: TextView? = null
        var ll: View? = null
        var stopId: TextView? = null
    }

    override fun getItem(position: Int): Stop? {
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
        viewHolder.routenum!!.text = getItem(position)!!.name
        viewHolder.stopId!!.text = getItem(position)!!.id
        viewHolder.buses!!.text = getItem(position)!!.buses.joinToString(separator = "\t", transform = {it.routeNumber})

        viewHolder.ll!!.setOnClickListener {
            val i = Intent(context, TimeActivity::class.java)
            val b = Bundle()

            val items = ArrayList<String>()
            for (x in 0 until getItem(position)!!.buses.size) {
                items.add(getItem(position)!!.buses[x].toString())
            }
            val buses = HashSet<Bus>()
            val dialog = AlertDialog.Builder(context)
                    .setTitle(getItem(position)!!.name)
                    .setMultiChoiceItems(items.toTypedArray(), null) { dialog, indexSelected, isChecked ->
                        if (isChecked) {
                            buses.add(getItem(position)!!.buses[indexSelected])
                            println(buses)
                            // If the user checked the item, add it to the selected items

                        } else if (buses.contains(getItem(position)!!.buses[indexSelected])) {
                            buses.remove(getItem(position)!!.buses[indexSelected])
                        }
                    }.setNegativeButton("Cancel") { dialogInterface, i -> }.setPositiveButton("Done") { dialog, id ->
                        if (buses.size > 0) {
                            b.putParcelableArrayList("buses", ArrayList(buses))
                            b.putString("stop", getItem(position)!!.id)
                            b.putString("stopname", getItem(position)!!.name)
                            i.putExtras(b)
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
                //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                data = results.values as ArrayList<Stop>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                val filteredResults = wholeList.filter {
                    (it.name.contains(constraint, true) || it.id.contains(constraint))
                }

                val results = Filter.FilterResults()
                results.values = filteredResults

                return results
            }
        }
    }

}
