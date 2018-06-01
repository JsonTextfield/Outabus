package com.textfield.json.outabus.util

import android.location.Location

import com.textfield.json.outabus.entities.Stop

import java.util.Comparator

/**
 * Created by Jason on 06/10/2015.
 */
class SortByDistance(private val userLocation: Location) : Comparator<Stop> {
    override fun compare(lhs: Stop, rhs: Stop): Int {
        val distance1 = FloatArray(1)
        val distance2 = FloatArray(1)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, lhs.lat, lhs.lng, distance1)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, rhs.lat, rhs.lng, distance2)
        return distance1[0].compareTo(distance2[0])
    }
}

