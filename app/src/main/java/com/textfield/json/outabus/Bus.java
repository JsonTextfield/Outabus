package com.textfield.json.outabus;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jason on 19/04/2016.
 */
public class Bus implements Parcelable{
    int direction;
    String routeNumber, id, destination;
    String bearing = "";
    ArrayList<String> trips;

    public Bus(String num, String id, String dest, int direction) {
        this.id = id;
        routeNumber = num;
        destination = dest;
        this.direction = direction;
    }
    public Bus(String num, String id, String dest, int direction, String bearing) {
        this.id = id;
        routeNumber = num;
        destination = dest;
        this.bearing = bearing;
        this.direction = direction;
    }
    public Bus(JSONObject jsonObject){
        try {
            routeNumber = jsonObject.getString("routenum");
            id = jsonObject.getString("route_id");
            destination = jsonObject.getString("destination");
            direction = jsonObject.getInt("direction");
            bearing = jsonObject.getString("bearing");
        } catch (JSONException e) {
            bearing = "";
        }
    }

    public Bus(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);
        routeNumber = data[0];
        id = data[1];
        destination = data[2];
        direction = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };

    public String getBearing() {
        return bearing;
    }

    @Override
    public boolean equals(Object o) {
        return (id.equals(((Bus) o).getId()) && destination.equals(((Bus) o).getDestination()));
        //return super.equals(o);
    }

    public String getId() {
        return id;
    }

    public int getDirection() {
        return direction;
    }

    public String getDestination() {
        return destination;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    @Override
    public String toString() {
        return routeNumber + " " + destination + " "+ bearing;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();

        return jsonObject;
    }

    public String getTrip(int i) {
        return trips.get(i);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{routeNumber,id,destination});
        dest.writeInt(direction);
    }
}
