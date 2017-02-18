package com.textfield.json.outabus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TimeFormatException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.textfield.json.outabus.util.DB;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TimeActivity extends GenericActivity {
    TimeAdapter arrayAdapter;

    public void refresh() {
        DB mDbHelper = new DB(this);
        Bundle b = getIntent().getExtras();

        getSupportActionBar().setTitle(b.getString("stopname"));

        Calendar calendar = Calendar.getInstance();
        String day = "";
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            day = "Dimanche";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            day = "Samedi";
        } else {
            day = "Semaine";
        }
        System.out.println(day);

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
        long t = calendar.getTimeInMillis();
        Date afterAddingTenMins = new Date(t - (10 * ONE_MINUTE_IN_MILLIS));
        Date limittime = new Date(t + (6 * 3600000));
        String time = formatter.format(afterAddingTenMins);
        String limit = formatter.format(limittime);

        String routes = "";
        ArrayList<Bus> busList = b.getParcelableArrayList("buses");

        for (int i = 0; i < busList.size(); i++) {
            if (i < busList.size() - 1) routes += "route_id = '" + busList.get(i).getId() + "' or ";
            else routes += "route_id = '" + busList.get(i).getId();
        }
        mDbHelper.open();
        Cursor cursor = null;
        try {
            if (formatter.parse(time).getTime() >= 6.48e+7) {
                cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                        + b.getString("stop")
                        + "' and datetime(arrival) > datetime('" + time + "') and datetime(arrival) < datetime('" + limit + "', '+24 hours')"
                        + "and trip_id like ('%" + day + "%') and ("
                        + routes
                        + "') group by arrival order by datetime(arrival) asc;");
                //cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                //        + b.getString("stop")+ "' and (" + routes + "') group by arrival order by datetime(arrival) asc;");
            } else {
                cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                        + b.getString("stop")
                        + "' and datetime(arrival) > datetime('" + time + "') and datetime(arrival) < datetime('" + limit + "')"
                        + "and trip_id like ('%" + day + "%') and ("
                        + routes
                        + "') group by arrival order by datetime(arrival) asc;");
                //cursor = mDbHelper.runQuery("select arrival,routenum,destination from times natural join trips natural join routes where stop_id = '"
                //        + b.getString("stop")+ "' and (" + routes + "') group by arrival order by datetime(arrival) asc;");

            }
        } catch (ParseException e) {
        }
        ArrayList<String> list = new ArrayList<>();

        do {
            if (cursor.getCount() > 0)
                list.add(cursor.getString(0) + "\n" + cursor.getString(1) + " " + cursor.getString(2));
        }
        while (cursor.moveToNext());
        mDbHelper.close();

        arrayAdapter = new TimeAdapter(this, list);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time, menu);
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
        if (id == R.id.refresh) {
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }
}
