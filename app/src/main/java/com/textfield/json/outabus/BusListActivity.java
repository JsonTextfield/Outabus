package com.textfield.json.outabus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.textfield.json.outabus.util.DB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Jason on 19/04/2016.
 */
public class BusListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DB mDbHelper = new DB(this);


        Calendar calendar = Calendar.getInstance();
        String day = "";
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            day = "Dimanche";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            day = "Samedi";
        } else {
            day = "Semaine";
        }

        ArrayList<Bus> list = new ArrayList<>();
        File file = new File(getCacheDir(), "buses");
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String x;
                while ((x = bufferedReader.readLine()) != null) {
                    JSONObject jsonObject = new JSONObject(x);
                    list.add(new Bus(jsonObject));
                }
                bufferedReader.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            mDbHelper.open();
            Cursor cursor = mDbHelper.runQuery("select * from routes join trips on trips.route_id = routes.route_id where trip_id like '%" + day + "%' group by routenum, direction order by routenum;");
            //Cursor cursor = mDbHelper.runQuery("select * from routes natural join trips group by routenum,direction order by routenum*1;");
            HashMap<String, JSONObject> buses = new HashMap<>();
            do {
                JSONObject bus = new JSONObject();
                Bus b = new Bus(cursor.getString(cursor.getColumnIndex("routenum")), cursor.getString(cursor.getColumnIndex("route_id")),
                        cursor.getString(cursor.getColumnIndex("destination")), cursor.getInt(cursor.getColumnIndex("direction")));
                list.add(b);
                try {
                    bus.put("routenum", b.getRouteNumber());
                    bus.put("direction", b.getDirection());
                    bus.put("destination", b.getDestination());
                    bus.put("route_id", b.getId());
                    buses.put(bus.getString("route_id") + bus.getString("direction"), bus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            while (cursor.moveToNext());

            for (Bus b : list) {
                cursor = mDbHelper.runQuery("select latitude,longitude from stops natural join busroutes where route_id = '" + b.getId() + "' and direction = " + b.getDirection() + " order by stop_number asc;");

                cursor.moveToFirst();
                double[] point1 = {Math.toRadians(cursor.getDouble(0)), Math.toRadians(cursor.getDouble(1))};
                cursor.moveToLast();
                double[] point2 = {Math.toRadians(cursor.getDouble(0)), Math.toRadians(cursor.getDouble(1))};


                double longDiff = point2[1] - point1[1];
                double y = Math.sin(longDiff) * Math.cos(point2[0]);
                double x = Math.cos(point1[0]) * Math.sin(point2[0]) - Math.sin(point1[0]) * Math.cos(point2[0]) * Math.cos(longDiff);

                double compass_bearing = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

                //System.out.println("bearing " + compass_bearing);
                if (compass_bearing >= 45 && compass_bearing < 135) {
                    b.bearing = "Eastbound";
                } else if (compass_bearing >= 135 && compass_bearing < 225) {
                    b.bearing = "Southbound";
                } else if (compass_bearing >= 225 && compass_bearing < 315) {
                    b.bearing = "Westbound";
                } else if (compass_bearing >= 315 || compass_bearing < 45) {
                    b.bearing = "Northbound";
                }
                try {
                    buses.get(b.getId() + b.getDirection()).put("bearing", b.bearing);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                for (String thing : buses.keySet()) {
                    bufferedWriter.write(buses.get(thing).toString() + "\n");
                }
                bufferedWriter.close();
            } catch (IOException e) {
            }
        }
        mDbHelper.close();
        final BusAdapter arrayAdapter = new BusAdapter(this, list);

        Collections.sort(list, new Comparator<Bus>() {
            @Override
            public int compare(Bus lhs, Bus rhs) {
                int x = (lhs.getRouteNumber().equals("OTrn")) ? 0 : Integer.parseInt(lhs.getRouteNumber());
                int y = (rhs.getRouteNumber().equals("OTrn")) ? 0 : Integer.parseInt(rhs.getRouteNumber());
                return (x - y);
                //return (Integer.parseInt(lhs.getRouteNumber()) - Integer.parseInt(rhs.getRouteNumber())) % 2;
            }
        });

        ListView listView = ((ListView) findViewById(R.id.list));
        listView.setAdapter(arrayAdapter);


        final EditText editText = (EditText) findViewById(R.id.filter);
        editText.addTextChangedListener(new MyTextWatcher(arrayAdapter));
        ImageButton imageButton = (ImageButton) findViewById(R.id.clearBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buslist, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
