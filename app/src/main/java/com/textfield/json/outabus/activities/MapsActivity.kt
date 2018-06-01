package com.textfield.json.outabus.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.textfield.json.outabus.R
import com.textfield.json.outabus.entities.Stop
import com.textfield.json.outabus.util.SortByDistance

import java.util.ArrayList
import java.util.Collections

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    internal var stops = ArrayList<Stop>()
    internal var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        type = intent.extras.getString("type")
        stops = intent.extras.getParcelableArrayList("stops")
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        val builder = LatLngBounds.Builder()

        if (type == "stop") {
            Toast.makeText(this, "Showing 20 closest stops", Toast.LENGTH_LONG).show()
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                super.onBackPressed()
            }
            Collections.sort(stops, SortByDistance(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)))

            for (i in 0..19) {
                val point = LatLng(stops[i].lat, stops[i].lng)
                val marker = mMap!!.addMarker(MarkerOptions().position(point).title(stops[i].name))

                builder.include(marker.position)

            }
        } else
            for (i in stops.indices) {
                val point = LatLng(stops[i].lat, stops[i].lng)
                val marker = mMap!!.addMarker(MarkerOptions()
                        .position(point)
                        .title(stops[i].name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image_lens)).anchor(0.5f, 0.5f))

                builder.include(marker.position)
                if (i > 0) {
                    mMap!!.addPolyline(PolylineOptions()
                            .add(point, LatLng(stops[i - 1].lat, stops[i - 1].lng))
                            .width(4f)
                            .color(ContextCompat.getColor(this, R.color.red)))
                }

            }
        val bounds = builder.build()
        mMap!!.setLatLngBoundsForCameraTarget(bounds)
        mMap!!.setOnMapLoadedCallback {
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }
}
