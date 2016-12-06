package com.textfield.json.outabus;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jason on 19/04/2016.
 */
public class Stop implements Parcelable{
    private String name, id;
    private double lat, lng;
    public ArrayList<Bus> buses;

    public Stop(String id, String name, double lat, double lng, ArrayList<Bus> buses) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.buses = buses;
    }

    public Stop(String id, String name, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.buses = new ArrayList<>();
    }
    public Stop(Parcel in){
        double[] data = new double[2];
        in.readDoubleArray(data);
        lat = data[0];
        lng = data[1];
        name = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        return id.equals(((Stop) o).id);
    }

    @Override
    public int hashCode() {
        int res = 0;
        for (char x : id.toCharArray()) {
            res += x;
        }
        return res;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Bus> getBuses() {
        return buses;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + id + " " + buses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(new double[]{lat,lng});
        dest.writeString(name);
    }
}
