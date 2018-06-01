package com.textfield.json.outabus.util

import android.location.LocationListener
import android.os.Bundle

/**
 * Created by Jason on 06/12/2016.
 */

abstract class LocationService : LocationListener {

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

    }
}
