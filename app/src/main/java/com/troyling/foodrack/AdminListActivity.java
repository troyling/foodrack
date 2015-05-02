package com.troyling.foodrack;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.foodrack.adapter.OrderListAdapter;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Order;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by ChandlerWu on 5/2/15.
 */
public class AdminListActivity extends ActionBarActivity {

    Button buttonRefresh;
    ListView listView;
    List<Order> listOfOrder;
    OrderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);


        // Find orders in back ground
        listView = (ListView)this.findViewById(R.id.listOfOrders);
        ParseQuery<Order> myOrders = ParseQuery.getQuery(Order.class);
        myOrders.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> orders, ParseException e) {
                if (e == null) {
                    adapter = new OrderListAdapter(AdminListActivity.this, orders);
                    listView.setAdapter(adapter);
                } else {
                    ErrorHelper.getInstance().promptError(AdminListActivity.this, "Error connecting to backend", "Unable to find your orders now. Please try again later.");
                }
            }
        });

        buttonRefresh = (Button)this.findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Sync orders
            }
        });
    }
}
