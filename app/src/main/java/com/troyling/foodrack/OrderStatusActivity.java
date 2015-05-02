package com.troyling.foodrack;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_layout);

        String objectId = getIntent().getStringExtra(ORDER_OBJECTID);

        // query the object
        ParseQuery<Order> orderParseQuery = ParseQuery.getQuery(Order.class);
        orderParseQuery.getInBackground(objectId, new GetCallback<Order>() {
            @Override
            public void done(Order order, ParseException e) {
                if (e == null) {
                    Toast.makeText(OrderStatusActivity.this, "Fetched", Toast.LENGTH_LONG).show();
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
