package com.troyling.foodrack;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.lang.annotation.Target;

/**
 * Created by ChandlerWu on 4/17/15.
 */
public class AddressActivity extends ActionBarActivity {

    EditText addressET;
    EditText addressNotesET;
    String address;
    String addressNotes;
    double lat;
    double lng;
    Button confirmButton;
    LatLng mll;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_layout);

        setUpMapIfNeeded();

        addressET = (EditText) this.findViewById(R.id.address);
        addressNotesET = (EditText) this.findViewById(R.id.addressNotes);

        confirmButton = (Button)this.findViewById(R.id.buttonConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = addressET.getText().toString();
                addressNotes = addressNotesET.getText().toString();
                LatLng camloc = mMap.getCameraPosition().target;
                lat = camloc.latitude;
                lng = camloc.longitude;
                // TODO 
                Intent intent = new Intent(AddressActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpMapIfNeeded() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
