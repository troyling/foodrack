package com.troyling.foodrack;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Order;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by ChandlerWu on 5/2/15.
 */
public class AdminMapActivity extends ActionBarActivity {
    public final static String FIRE_BASE_URL = "https://foodrack.firebaseio.com/";
    public final static String ORDER_OBJECTID = "objectIb";

    private Firebase locationRef; // Backend used to fetch order location and status
    private boolean isLocationShared = false;

    private Button bConfrim;
    private Button bDelivering;
    private Button bDelivered;
    private GoogleMap mMap;
    private LatLng mll;
    private String orderObjectId;
    private TextView textName;
    private TextView textPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);

        textName = (TextView) findViewById(R.id.textPersonName);
        textPhone = (TextView) findViewById(R.id.textPhone);

        orderObjectId = getIntent().getStringExtra(ORDER_OBJECTID);
        locationRef = new Firebase(FIRE_BASE_URL + orderObjectId);

        // Set map
        setUpGoogleMap();

        // Set buttons
        bConfrim = (Button)this.findViewById(R.id.button7);
        bConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationRef.child("status").setValue(Order.STATUS_CONFIRMED);
            }
        });

        bDelivering = (Button)this.findViewById(R.id.button8);
        bDelivering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO set button functions
                locationRef.child("status").setValue(Order.STATUS_IN_TRANSIT);
                isLocationShared = true;
                Toast.makeText(getApplicationContext(), "Your current location is now being shared with the customer.", Toast.LENGTH_SHORT);
            }
        });

        bDelivered = (Button)this.findViewById(R.id.button9);
        bDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationRef.child("status").setValue(Order.STATUS_DELIVERED);
                isLocationShared = false;
            }
        });

        ParseQuery<Order> orderParseQuery = ParseQuery.getQuery(Order.class);
        orderParseQuery.include(Order.OWNER);
        orderParseQuery.getInBackground(orderObjectId, new GetCallback<Order>() {
            @Override
            public void done(Order order, ParseException e) {
                if (e == null) {
                    ParseUser user = order.getOwner();
                    String phoneNum = user.getString("phone");
                    String name = user.getString("name");

                    if (name != null) {
                        textName.setText(name);
                    }

                    if (phoneNum != null) {
                        textPhone.setText(phoneNum);
                    }
                } else {
                    ErrorHelper.getInstance().promptError(getApplicationContext(), "Error", e.getMessage());
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setUpGoogleMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDeliver))
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

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null) {
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));

                    // upload my location if needed
                    if (isLocationShared) {
                        locationRef.child("location").child("lat").setValue(location.getLatitude());
                        locationRef.child("location").child("lng").setValue(location.getLongitude());
                    }
                } else {
                    Log.i("hi", "No location");
                    Toast toast = Toast.makeText(getApplicationContext(), "Location is currently not available.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
