package com.textfield.json.outabus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.textfield.json.outabus.util.DB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * Created by Jason on 19/04/2016.
 */
public class StopListActivity extends GenericActivity {
    StopAdapter arrayAdapter;
    double lat, lng;
    ArrayList<Stop> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        lat = location.getLatitude();
        lng = location.getLongitude();

        File file = new File(getCacheDir(), "stops");
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String x;
                while ((x = bufferedReader.readLine()) != null) {
                    //System.out.println(x);
                    ArrayList<Bus> busList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(x);
                    JSONArray buses = jsonObject.getJSONArray("buses");
                    for (int i = 0; i < buses.length(); i++) {
                        JSONObject obj = buses.getJSONObject(i);
                        busList.add(new Bus(obj.getString("routenum"), obj.getString("route_id"), obj.getString("destination"), obj.getInt("direction")));
                    }
                    list.add(new Stop(jsonObject.getString("stop_id"),
                            jsonObject.getString("name"),
                            jsonObject.getDouble("lat"),
                            jsonObject.getDouble("lng"),
                            busList));

                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            HashMap<Stop, LinkedHashSet<Bus>> results = new HashMap<>();
            HashMap<String, JSONObject> stops = new HashMap<>();

            DB mDbHelper = new DB(this);
            mDbHelper.open();
            Cursor cursor = mDbHelper.runQuery("select * from routes natural join busroutes natural join stops group by stop_id,route_id order by routenum*1 asc;");

            do {
                Stop s = new Stop(cursor.getString(cursor.getColumnIndex("stop_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getDouble(cursor.getColumnIndex("latitude")),
                        cursor.getDouble(cursor.getColumnIndex("longitude")));
                if (results.get(s) == null) {
                    results.put(s, new LinkedHashSet<Bus>());

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", s.getName());
                        jsonObject.put("stop_id", s.getId());
                        jsonObject.put("lat", s.getLat());
                        jsonObject.put("lng", s.getLng());

                        stops.put(s.getId(), jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Bus b = new Bus(cursor.getString(cursor.getColumnIndex("routenum")),
                        cursor.getString(cursor.getColumnIndex("route_id")),
                        cursor.getString(cursor.getColumnIndex("destination")),
                        cursor.getInt(cursor.getColumnIndex("direction")));
                results.get(s).add(b);

                try {
                    JSONObject bus = new JSONObject();
                    bus.put("routenum", b.getRouteNumber());
                    bus.put("route_id", b.getId());
                    bus.put("direction", b.getDirection());
                    bus.put("destination", b.getDestination());
                    try {
                        stops.get(s.getId()).getJSONArray("buses");
                    } catch (JSONException e) {
                        stops.get(s.getId()).put("buses", new JSONArray());
                    }

                    stops.get(s.getId()).getJSONArray("buses").put(bus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            while (cursor.moveToNext());
            mDbHelper.close();

            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                for (Stop i : results.keySet()) {
                    bufferedWriter.write(stops.get(i.getId()).toString() + "\n");
                    i.buses.addAll(results.get(i));

                    if (i.getBuses().size() > 0)
                        list.add(i);
                }
                bufferedWriter.close();
            } catch (IOException e) {
            }
        }
        /*for(Stop stop : stopList){
            if(stop.getBuses().size() == 0){
                stopList.remove(stop);
            }
        }*/
        Collections.sort(list, new Comparator<Stop>() {
            @Override
            public int compare(Stop lhs, Stop rhs) {
                float[] res = new float[1];
                float[] res2 = new float[1];

                Location.distanceBetween(lat, lng, lhs.getLat(), lhs.getLng(), res);
                Location.distanceBetween(lat, lng, rhs.getLat(), rhs.getLng(), res2);
                if (res[0] > res2[0]) return 1;
                if (res[0] < res2[0]) return -1;

                return 0;
            }
        });

        arrayAdapter = new StopAdapter(this, list);
        listView.setAdapter(arrayAdapter);

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
            System.out.println(list.size());
            b.putParcelableArrayList("stops", new ArrayList<Stop>(list.subList(0, 50)));
            b.putString("type", "stop");
            Intent i = new Intent(this, MapsActivity.class);
            i.putExtras(b);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop, menu);
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
}
