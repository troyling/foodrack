package com.troyling.foodrack;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Order;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Map;

/**
 * Created by ChandlerWu on 4/23/15.
 */
public class OrderStatusActivity extends ActionBarActivity {
    public final static String ORDER_OBJECTID = "objectId";
    private GoogleMap mMap;

    Order mOrder;
    TextView textOrderStatus;
    private Firebase locationRef;
    private Firebase statusRef;
    private Marker marker = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_layout);

        String orderObjectId = getIntent().getStringExtra(ORDER_OBJECTID);

        textOrderStatus = (TextView) findViewById(R.id.textOrderStatus);

        // location listener
        locationRef = new Firebase(AdminMapActivity.FIRE_BASE_URL + orderObjectId + "/location");
        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    try {
                        // update map
                        Map<String, Object> location = (Map<String, Object>) dataSnapshot.getValue();
                        double lat,lng = 0;

                        lat = (double) location.get("lat");
                        lng = (double) location.get("lng");
                        LatLng ll = new LatLng(lat, lng);

                        if (marker == null) {
                            marker = mMap.addMarker(new MarkerOptions().position(ll).title("Deliver Guy")
                                    .draggable(false).icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.android_red2)));
                        } else {
                            marker.setPosition(ll);
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));

                    } catch (NullPointerException nullExp) {
                        Log.d("Null pointer", "No data from DataSnapshot");
                    }
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
                    String status = (String)dataSnapshot.getValue();
                    if (status != null) {
                        if (status.equals(Order.STATUS_DELIVERED)) {
                            mOrder.setStatus(Order.STATUS_DELIVERED);
                            mOrder.saveInBackground();
                        }
                        textOrderStatus.setText(status);
                    }
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
                    try {
                        mOrder = order;
                        LatLng deliverLl = new LatLng(order.getDeliverLocation().getLatitude(), order.
                                getDeliverLocation().getLongitude());
                        mMap.addMarker(new MarkerOptions().position(deliverLl).title("Deliver location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliverLl, 16));
                    } catch (NullPointerException nullExp) {
                        Log.d("Null pointer", "No data from DataSnapshot");
                    }
                } else {
                    ErrorHelper.getInstance().promptError(OrderStatusActivity.this, "Error",
                            "Unable to fetch data of your order. Please try again later.");
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
    }
}
