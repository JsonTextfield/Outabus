package com.textfield.json.outabus.entities

import java.util.ArrayList

/**
 * Created by Jason on 19/04/2016.
 */
class Trip {
    internal var id = ""
    internal var destination = ""
    internal var stops = ArrayList<Stop>()

    constructor(bus: Bus, stops: ArrayList<Stop>) {}
    constructor(id: String) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        return stops[0] == (other as Trip).stops[0] && stops[stops.size - 1] == other.stops[stops.size - 1]
    }

}
