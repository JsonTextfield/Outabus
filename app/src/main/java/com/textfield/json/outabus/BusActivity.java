package com.textfield.json.outabus;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.textfield.json.outabus.util.DB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;

public class BusActivity extends GenericActivity {
    Bus bus;
    ArrayList<Stop> stopList = new ArrayList<>();
    StopAdapter arrayAdapter;

    public void setup(Bus bus) {
        this.bus = bus;
        stopList.clear();

        setSmallTitle(bus.toString());

        DB mDbHelper = new DB(this);

        mDbHelper.open();

        Cursor cursor = mDbHelper.runQuery("select * from stops natural join busroutes where route_id = '"
                + bus.getId() + "' and direction = "
                + bus.getDirection()
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
            stopList.add(s);
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
            if (results2.get(i).size() == 0) stopList.remove(stopList.indexOf(i));
            else stopList.get(stopList.indexOf(i)).buses = new ArrayList<>(results2.get(i));

        }

        arrayAdapter = new StopAdapter(this, stopList);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bus = getIntent().getExtras().getParcelable("bus");
        setup(bus);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.searchView));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    arrayAdapter.getFilter().filter("");
                    //listView.clearTextFilter();
                } else {
                    arrayAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

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
        if (id == R.id.switch_dir) {
            DB mDbHelper = new DB(this);
            mDbHelper.open();
            Cursor cursor = mDbHelper.runQuery("select * from routes join trips on trips.route_id = routes.route_id " +
                    "where trips.route_id = \"" + bus.getId() + "\" and direction = " +
                    ((bus.getDirection() + 1) % 2) + " group by routenum, direction order by routenum;");
            try {
                Bus autobus = new Bus(cursor.getString(cursor.getColumnIndex("routenum")), cursor.getString(cursor.getColumnIndex("route_id")),
                        cursor.getString(cursor.getColumnIndex("destination")), cursor.getInt(cursor.getColumnIndex("direction")));
                System.out.println(autobus);
                setup(autobus);
            }catch (CursorIndexOutOfBoundsException e){
                e.printStackTrace();
            }

        }
        if (id == R.id.map) {
            Bundle b = new Bundle();
            b.putParcelableArrayList("stops", stopList);
            b.putString("type", "bus");
            Intent i = new Intent(this, MapsActivity.class);
            i.putExtras(b);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
