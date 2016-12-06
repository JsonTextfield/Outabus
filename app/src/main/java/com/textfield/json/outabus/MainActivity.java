package com.textfield.json.outabus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.textfield.json.outabus.util.DB;

public class MainActivity extends GenericActivity {
    private static final int REQUEST_LOCATION = 4;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                finish();
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        DB db = new DB(this);
        db.createDatabase();

        listView.setAdapter(new MainAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    startActivity(new Intent(MainActivity.this, BusListActivity.class));
                }
                if (i == 1) {
                    startActivity(new Intent(MainActivity.this, StopListActivity.class));
                }
                if (i == 2) {

                }
            }
        });
    }

    private class MainAdapter extends ArrayAdapter<String> {
        public MainAdapter() {
            super(MainActivity.this, 0, getResources().getStringArray(R.array.main));
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, parent, false);

            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(getItem(position));
            return convertView;
        }
    }
}
