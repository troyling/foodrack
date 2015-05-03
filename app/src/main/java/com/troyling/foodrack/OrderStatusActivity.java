package com.troyling.foodrack;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Order;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * Created by ChandlerWu on 4/23/15.
 */
public class OrderStatusActivity extends FragmentActivity {
    public final static String ORDER_OBJECTID = "objectId";
    private GoogleMap mMap;

    private Firebase locationRef;
    private Firebase statusRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_layout);

        String orderObjectId = getIntent().getStringExtra(ORDER_OBJECTID);


        // location listener
        locationRef = new Firebase(AdminMapActivity.FIRE_BASE_URL + orderObjectId + "/location");
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO change location on the map
                if (dataSnapshot != null) {
//                    Toast.makeText(getApplicationContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
//                ErrorHelper.getInstance().promptError(OrderStatusActivity.this, "Error", firebaseError.getMessage());
            }
        });

        // status listener
        statusRef = new Firebase(AdminMapActivity.FIRE_BASE_URL + orderObjectId + "/status");
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO change the status of the order
                if (dataSnapshot != null) {
//                    Toast.makeText(getApplicationContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
//                ErrorHelper.getInstance().promptError(OrderStatusActivity.this, "Error", firebaseError.getMessage());
            }
        });

        // query the object
        ParseQuery<Order> orderParseQuery = ParseQuery.getQuery(Order.class);
        orderParseQuery.getInBackground(orderObjectId , new GetCallback<Order>() {
            @Override
            public void done(Order order, ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Fetched", Toast.LENGTH_LONG).show();
                } else {
                    ErrorHelper.getInstance().promptError(OrderStatusActivity.this, "Error", "Unable to fetch data of your order. Please try again later.");
                }
            }
        });

        // Setup map and get location
        setUpMapIfNeeded();
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getMyLocation();
        //mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null) {
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));
                } else {
                    Log.i("hi", "No location");
                    Toast toast = Toast.makeText(getApplicationContext(), "Location is currently not available.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
