package com.textfield.json.outabus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class GenericActivity extends AppCompatActivity {
    protected ListView listView;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        toolbar = (Toolbar) findViewById(R.id.generic_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        listView = (ListView) findViewById(R.id.generic_listview);
    }

    public void showProgressBar(boolean show) {

    }

    public void setSmallTitle(String text) {
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.textView);
        title.setText(text);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
