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
    List<Order> orders;
    OrderListAdapter adapter;
    ParseQuery<Order> allOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        // Find orders in back ground
        listView = (ListView)this.findViewById(R.id.listOfOrders);
        buttonRefresh = (Button)this.findViewById(R.id.buttonRefresh);

        allOrders = ParseQuery.getQuery(Order.class);
        allOrders.whereNotEqualTo("status", Order.STATUS_DELIVERED);
        fetchOrders();
        // refresh listener

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchOrders();
            }
        });
    }

    private void fetchOrders() {
        allOrders.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> orderList, ParseException e) {
                if (e == null) {
                    orders = orderList;
                    adapter = new OrderListAdapter(AdminListActivity.this, orders);
                    listView.setAdapter(adapter);
                } else {
                    ErrorHelper.getInstance().promptError(AdminListActivity.this, "Error connecting to backend", "Unable to find your orders now. Please try again later.");
                }
            }
        });
    }
}
