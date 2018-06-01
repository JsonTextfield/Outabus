package com.textfield.json.outabus.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView

import com.textfield.json.outabus.R

open class GenericActivity : AppCompatActivity() {
    protected lateinit var listView: ListView
    protected lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.generic_layout)
        toolbar = findViewById<Toolbar>(R.id.generic_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        listView = findViewById<ListView>(R.id.generic_listview)
    }

    fun showProgressBar(show: Boolean) {

    }

    fun setSmallTitle(text: String) {
        toolbar.title = ""
        val title = toolbar.findViewById<TextView>(R.id.textView)
        title.text = text
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
