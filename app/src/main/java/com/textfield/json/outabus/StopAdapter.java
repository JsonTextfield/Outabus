package com.textfield.json.outabus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Jason on 20/09/2015.
 */
public class StopAdapter extends ArrayAdapter<Stop> {
    private ArrayList<Stop> data;
    private ArrayList<Stop> wholeList;

    private static class ViewHolder {
        public TextView routenum;
        public TextView buses;
        public View ll;
        public TextView stopId;
    }

    @Nullable
    @Override
    public Stop getItem(int position) {
        return data.get(position);
    }

    public StopAdapter(Context context, ArrayList<Stop> data) {
        super(context, 0, data);
        wholeList = this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stop_item, parent, false);

            viewHolder.routenum = (TextView) convertView.findViewById(R.id.listtext);
            viewHolder.buses = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.ll = convertView.findViewById(R.id.ll);
            viewHolder.stopId = (TextView) convertView.findViewById(R.id.stop_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.routenum.setText(getItem(position).getName());
        String buses = "";
        viewHolder.stopId.setText(getItem(position).getId());
        for (Bus x : getItem(position).getBuses()) {
            buses += x.getRouteNumber() + "   ";
        }
        viewHolder.buses.setText(buses);
        viewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(getContext(), TimeActivity.class);
                final Bundle b = new Bundle();

                final ArrayList<String> items = new ArrayList<String>();
                for (int x = 0; x < getItem(position).getBuses().size(); x++) {
                    items.add(getItem(position).getBuses().get(x).toString());
                }
                final HashSet<Bus> buses = new HashSet<Bus>();
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle(getItem(position).getName())
                        .setMultiChoiceItems(items.toArray(new String[0]), null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    buses.add(getItem(position).getBuses().get(indexSelected));
                                    System.out.println(buses);
                                    // If the user checked the item, add it to the selected items

                                } else if (buses.contains(getItem(position).getBuses().get(indexSelected))) {
                                    buses.remove(getItem(position).getBuses().get(indexSelected));
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                
                            }
                        }).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (buses.size() > 0) {
                                    b.putParcelableArrayList("buses", new ArrayList<Bus>(buses));
                                    b.putString("stop", getItem(position).getId());
                                    b.putString("stopname", getItem(position).getName());
                                    i.putExtras(b);
                                    getContext().startActivity(i);
                                }
                            }
                        }).create();
                dialog.show();

            }
        });


        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                data = (ArrayList<Stop>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                ArrayList<Stop> filteredResults = new ArrayList<>();

                for (Stop s : wholeList) {
                    if (s.getName().contains(constraint.toString().toUpperCase()) || s.getId().contains(constraint.toString())) {
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
