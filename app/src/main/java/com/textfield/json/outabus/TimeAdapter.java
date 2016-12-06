package com.textfield.json.outabus;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jason on 20/09/2015.
 */
public class TimeAdapter extends ArrayAdapter<String> {
    private ArrayList<String> data;

    private static class ViewHolder {
        public TextView time;
        public TextView timeUntil;
    }


    public TimeAdapter(Context context, ArrayList<String> data) {
        super(context, 0, data);
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.time_item, parent, false);

            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.timeUntil = (TextView) convertView.findViewById(R.id.timeuntil);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Calendar calendar = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date1;
        Date date2 = new Date(calendar.getTimeInMillis());
        String d2 = formatter.format(date2);

        try {
            date1 = formatter.parse(data.get(position));
            date2 = formatter.parse(d2);
            viewHolder.time.setText(data.get(position));

            long different = date1.getTime() - date2.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            String string = "";

            if (elapsedHours > 0) string += elapsedHours + " h ";
            if (elapsedMinutes != 0) string += Math.abs(elapsedMinutes) + " m ";
            if (elapsedMinutes >= 0 && elapsedHours < 1) string += Math.abs(elapsedSeconds) + " s ";
            if (elapsedMinutes < 0 || elapsedSeconds < 0)
                viewHolder.timeUntil.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            else{
                viewHolder.timeUntil.setTextColor(Color.parseColor("#444444"));
            }
            viewHolder.timeUntil.setText(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

}
