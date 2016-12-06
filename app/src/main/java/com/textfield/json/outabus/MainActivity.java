package com.textfield.json.outabus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.textfield.json.outabus.util.DB;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB db = new DB(this);
        db.createDatabase();

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new MainAdapter());
    }

    private class MainAdapter extends ArrayAdapter<String> {
        String[] data;

        public MainAdapter() {
            super(MainActivity.this, 0, getResources().getStringArray(R.array.main));
            data = getResources().getStringArray(R.array.main);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, parent, false);

            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(data[position]);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        startActivity(new Intent(MainActivity.this, BusListActivity.class));
                    }
                    if (position == 1) {
                        startActivity(new Intent(MainActivity.this, StopListActivity.class));
                    }
                    if (position == 2) {

                    }
                }
            });
            return convertView;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            //super.onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
}
