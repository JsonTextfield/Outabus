package com.textfield.json.outabus.entities

import android.os.Parcel
import android.os.Parcelable

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

/**
 * Created by Jason on 19/04/2016.
 */
class Bus : Parcelable, Comparable<Bus> {
    var direction: Int = 0
    var routeNumber = ""
    var id = ""
    var destination = ""
    var bearing = ""
    var trips = ArrayList<String>()

    constructor()

    constructor(num: String, id: String, dest: String, direction: Int) {
        this.id = id
        routeNumber = num
        destination = dest
        this.direction = direction
    }

    constructor(num: String, id: String, dest: String, direction: Int, bearing: String) {
        this.id = id
        routeNumber = num
        destination = dest
        this.bearing = bearing
        this.direction = direction
    }

    constructor(jsonObject: JSONObject) {
        try {
            routeNumber = jsonObject.getString("routenum")
            id = jsonObject.getString("route_id")
            destination = jsonObject.getString("destination")
            direction = jsonObject.getInt("direction")
            bearing = jsonObject.getString("bearing")
        } catch (e: JSONException) {
            bearing = ""
        }

    }

    constructor(`in`: Parcel) {
        routeNumber = `in`.readString()
        id = `in`.readString()
        destination = `in`.readString()
        direction = `in`.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(routeNumber)
        dest.writeString(id)
        dest.writeString(destination)
        dest.writeInt(direction)
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as Bus).id && destination == other.destination
        //return super.equals(o);
    }

    override fun toString(): String {
        return "$routeNumber $destination $bearing"
    }

    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()

        return JSONObject()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun compareTo(other: Bus): Int {
        val x = if (this.routeNumber == "OTrn") 0 else Integer.parseInt(this.routeNumber)
        val y = if (other.routeNumber == "OTrn") 0 else Integer.parseInt(other.routeNumber)
        return x.compareTo(y)
    }

    companion object CREATOR : Parcelable.Creator<Bus> {
        override fun createFromParcel(`in`: Parcel): Bus {
            return Bus(`in`)
        }

        override fun newArray(size: Int): Array<Bus?> {
            return arrayOfNulls(size)
        }
    }
}
