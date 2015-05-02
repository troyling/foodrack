package com.troyling.foodrack;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ChandlerWu on 5/2/15.
 */
public class AdminMapActivity extends ActionBarActivity {

    Button bConfrim;
    Button bDelivering;
    Button bDelivered;
    private GoogleMap mMap;
    LatLng mll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);

        // Set map
        setUpGoogleMap();

        // Set buttons
        bConfrim = (Button)this.findViewById(R.id.button7);
        bConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO set button functions
            }
        });

        bDelivering = (Button)this.findViewById(R.id.button8);
        bDelivering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO set button functions
            }
        });

        bDelivered = (Button)this.findViewById(R.id.button9);
        bDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO set button functions
            }
        });

    }

    private void setUpGoogleMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAddress))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        // Get current location
        mMap.getMyLocation();
        // Hard coded the location for the marker
        mll = new LatLng(42.2743400,-71.8097730);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mll, 15));
    }
}
