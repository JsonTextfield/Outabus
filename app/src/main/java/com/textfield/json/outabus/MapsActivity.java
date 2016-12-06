package com.textfield.json.outabus;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.textfield.json.outabus.util.SortByDistance;

import java.util.ArrayList;
import java.util.Collections;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Stop> stops = new ArrayList<>();
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getString("type");
            stops = getIntent().getExtras().getParcelableArrayList("stops");
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (type.equals("stop")) {
            Toast.makeText(this, "Showing 10 closest stops" , Toast.LENGTH_LONG).show();
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Collections.sort(stops, new SortByDistance(lat, lng));

            for (int i = 0; i < 10; i++) {
                LatLng point = new LatLng(stops.get(i).getLat(), stops.get(i).getLng());
                Marker marker = mMap.addMarker(new MarkerOptions().position(point).title(stops.get(i).getName()));

                builder.include(marker.getPosition());

            }
        } else
            for (int i = 0; i < stops.size(); i++) {
                LatLng point = new LatLng(stops.get(i).getLat(), stops.get(i).getLng());
                Marker marker = mMap.addMarker(new MarkerOptions().position(point).title(stops.get(i).getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_image_lens)).anchor(0.5f, 0.5f));

                builder.include(marker.getPosition());
                if (i > 0) {
                    mMap.addPolyline(new PolylineOptions()
                            .add(point, new LatLng(stops.get(i - 1).getLat(),
                                    stops.get(i - 1).getLng()))
                            .width(4)
                            .color(ContextCompat.getColor(this, R.color.red)));
                }

            }
        final LatLngBounds bounds = builder.build();

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                // Move camera.
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                // Remove listener to prevent position reset on camera move.
                mMap.setOnCameraChangeListener(null);
            }
        });

    }
}
