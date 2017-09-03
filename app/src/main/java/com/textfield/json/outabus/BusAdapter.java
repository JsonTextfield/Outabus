package com.textfield.json.outabus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jason on 20/09/2015.
 */
public class BusAdapter extends ArrayAdapter<Bus> {
    private ArrayList<Bus> data;
    private ArrayList<Bus> wholeList;

    private static class ViewHolder {
        public TextView dest;
        public TextView bound;
        public TextView num;
        public RelativeLayout layout;
    }


    public BusAdapter(Context context, ArrayList<Bus> data) {
        super(context, 0, data);
        this.data = wholeList = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bus_item, parent, false);

            viewHolder.num = (TextView) convertView.findViewById(R.id.number);
            viewHolder.dest = (TextView) convertView.findViewById(R.id.destination);
            viewHolder.bound = (TextView) convertView.findViewById(R.id.bound);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.busLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bound.setText(data.get(position).getBearing());
        viewHolder.num.setText(data.get(position).getRouteNumber());
        viewHolder.dest.setText(data.get(position).getDestination());
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putParcelable("bus", data.get(position));
                Intent i = new Intent(getContext(), BusActivity.class);
                i.putExtras(b);
                getContext().startActivity(i);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                data = (ArrayList<Bus>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                ArrayList<Bus> filteredResults = new ArrayList<>();

                for (Bus s : wholeList) {
                    if (s.getDestination().toLowerCase().contains(constraint.toString().toLowerCase()) || s.getRouteNumber().equals(constraint.toString())) {
                        filteredResults.add(s);
                    }
                }

                System.out.print(filteredResults);

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

}
