package com.textfield.json.outabus.util;

import android.location.Location;

import com.textfield.json.outabus.Stop;

import java.util.Comparator;

/**
 * Created by Jason on 06/10/2015.
 */
public class SortByDistance implements Comparator<Stop> {
    double lat, lng;

    public SortByDistance(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public int compare(Stop lhs, Stop rhs) {
        float[] distance1 = new float[1];
        float[] distance2 = new float[1];
        Location.distanceBetween(lat, lng, lhs.getLat(), lhs.getLng(), distance1);
        Location.distanceBetween(lat, lng, rhs.getLat(), rhs.getLng(), distance2);
        if (distance1[0] > distance2[0]) return 1;
        if (distance1[0] < distance2[0]) return -1;
        return 0;
    }
}

