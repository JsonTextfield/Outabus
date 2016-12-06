package com.textfield.json.outabus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.textfield.json.outabus.util.DB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class BusActivity extends GenericActivity {
    ArrayList<Stop> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        getSupportActionBar().setTitle(b.getString("number") + " " + b.getString("name"));

        DB mDbHelper = new DB(this);
        mDbHelper.open();

        Cursor cursor = mDbHelper.runQuery("select * from stops natural join busroutes where route_id = '"
                + b.getString("id") + "' and direction = "
                + b.getInt("direction")
                + " group by stop_id order by stop_number,stop_id;");

        LinkedHashMap<Stop, LinkedHashSet<Bus>> results = new LinkedHashMap<>();
        LinkedHashMap<Stop, LinkedHashSet<Bus>> results2 = new LinkedHashMap<>();

        do {
            Stop s = new Stop(cursor.getString(cursor.getColumnIndex("stop_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("latitude")),
                    cursor.getDouble(cursor.getColumnIndex("longitude")));
            if (results.get(s) == null) {
                results.put(s, new LinkedHashSet<Bus>());
            }
            list.add(s);
        }
        while (cursor.moveToNext());

        String string = "where ";
        for (Stop i : results.keySet()) {
            string += "stop_id = '" + i.getId() + "' or ";
        }

        string = string.substring(0, string.length() - 4);
        cursor = mDbHelper.runQuery("select * from routes natural join busroutes natural join stops " + string + " group by route_id,stop_id order by route_id*1 asc;");
        do {
            Stop stop = new Stop(cursor.getString(cursor.getColumnIndex("stop_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("latitude")),
                    cursor.getDouble(cursor.getColumnIndex("longitude")));

            if (results2.get(stop) == null) {
                results2.put(stop, new LinkedHashSet<Bus>());
            }
            results2.get(stop).add(new Bus(cursor.getString(cursor.getColumnIndex("routenum")),
                    cursor.getString(cursor.getColumnIndex("route_id")),
                    cursor.getString(cursor.getColumnIndex("destination")),
                    cursor.getInt(cursor.getColumnIndex("direction"))));
        }
        while (cursor.moveToNext());
        mDbHelper.close();

        for (Stop i : results2.keySet()) {
            if (results2.get(i).size() == 0) list.remove(list.indexOf(i));
            else list.get(list.indexOf(i)).buses = new ArrayList<>(results2.get(i));

        }

        StopAdapter arrayAdapter = new StopAdapter(this, list);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();

        }
        if (id == R.id.map) {
            Bundle b = new Bundle();
            b.putParcelableArrayList("stops", list);
            b.putString("type", "bus");
            Intent i = new Intent(this, MapsActivity.class);
            i.putExtras(b);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}