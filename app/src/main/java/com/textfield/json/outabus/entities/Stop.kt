package com.textfield.json.outabus.entities

import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

/**
 * Created by Jason on 19/04/2016.
 */
class Stop : Parcelable {
    var name = ""
    var id = ""
    var lat = 0.0
    var lng = 0.0
    var buses = ArrayList<Bus>()

    constructor(id: String, name: String, lat: Double, lng: Double, buses: ArrayList<Bus>) {
        this.id = id
        this.name = name
        this.lat = lat
        this.lng = lng
        this.buses = buses
    }

    constructor(id: String, name: String, lat: Double, lng: Double) {
        this.id = id
        this.name = name
        this.lat = lat
        this.lng = lng
        this.buses = ArrayList()
    }

    constructor(`in`: Parcel) {
        lat = `in`.readDouble()
        lng = `in`.readDouble()
        name = `in`.readString()
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as Stop).id
    }

    override fun hashCode(): Int {
        var res = 0
        for (x in id.toCharArray()) {
            res += x.toInt()
        }
        return res
    }

    override fun toString(): String {
        return "$name $id $buses"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeDouble(lat)
        dest.writeDouble(lng)
        dest.writeString(name)
    }

    companion object CREATOR : Parcelable.Creator<Stop> {
        override fun createFromParcel(`in`: Parcel): Stop {
            return Stop(`in`)
        }

        override fun newArray(size: Int): Array<Stop?> {
            return arrayOfNulls(size)
        }
    }
}
