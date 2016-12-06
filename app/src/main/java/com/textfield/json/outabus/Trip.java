package com.textfield.json.outabus;

import java.util.ArrayList;

/**
 * Created by Jason on 19/04/2016.
 */
public class Trip {
    String id, destination;
    ArrayList<Stop> stops;

    public Trip(Bus bus, ArrayList<Stop> stops){
    }
    public Trip(String id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return (stops.get(0).equals(((Trip)o).stops.get(0)) && stops.get(stops.size()-1).equals(((Trip)o).stops.get(stops.size()-1)));
    }

}
