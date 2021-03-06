package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
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

    private Button buttonRefresh;
    private ListView listView;
    private List<Order> orders;
    private OrderListAdapter adapter;
    private ParseQuery<Order> allOrders;

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

        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AdminListActivity.this, AdminMapActivity.class);
                Order order = (Order) adapter.getItem(position);
                intent.putExtra(AdminMapActivity.ORDER_OBJECTID, order.getObjectId());
                startActivity(intent);
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
                    ErrorHelper.getInstance().promptError(AdminListActivity.this, "Error connecting to backend", e.getMessage());
                }
            }
        });
    }
}
