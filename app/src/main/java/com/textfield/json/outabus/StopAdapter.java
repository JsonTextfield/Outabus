package com.textfield.json.outabus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
        public LinearLayout ll;
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
            viewHolder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.routenum.setText(data.get(position).getName());
        String buses = "";

        for (Bus x : data.get(position).getBuses()) {
            buses += x.getRouteNumber() + "   ";
        }
        viewHolder.buses.setText(buses);
        viewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(getContext(), TimeActivity.class);
                final Bundle b = new Bundle();

                final ArrayList<String> items = new ArrayList<String>();
                for (int x = 0; x < data.get(position).getBuses().size(); x++) {
                    items.add(data.get(position).getBuses().get(x).toString());
                }
                final HashSet<Bus> buses = new HashSet<Bus>();
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle(data.get(position).getName())
                        .setMultiChoiceItems(items.toArray(new String[0]), null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    buses.add(data.get(position).getBuses().get(indexSelected));
                                    System.out.println(buses);
                                    // If the user checked the item, add it to the selected items

                                } else if (buses.contains(data.get(position).getBuses().get(indexSelected))) {
                                    buses.remove(data.get(position).getBuses().get(indexSelected));
                                }
                            }
                        }).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (buses.size() > 0) {
                                    b.putParcelableArrayList("buses", new ArrayList<Bus>(buses));
                                    b.putString("stop", data.get(position).getId());
                                    b.putString("stopname", data.get(position).getName());
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
                    if (s.getName().contains(constraint.toString().toUpperCase())) {
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
