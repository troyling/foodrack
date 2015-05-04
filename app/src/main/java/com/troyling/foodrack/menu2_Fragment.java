package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.foodrack.adapter.OrderListAdapter;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Order;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class menu2_Fragment extends Fragment {
    View rootView;
    ListView listView;
    OrderListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_your_orders_layout, container, false);
        listView = (ListView) rootView.findViewById(R.id.orderListView);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseQuery<Order> myOrders = ParseQuery.getQuery(Order.class);
            myOrders.whereEqualTo(Order.OWNER, currentUser);
            myOrders.findInBackground(new FindCallback<Order>() {
                @Override
                public void done(List<Order> orders, ParseException e) {
                    if (e == null) {
                        adapter = new OrderListAdapter(getActivity(), orders);
                        listView.setAdapter(adapter);
                    } else {
                        ErrorHelper.getInstance().promptError(getActivity(), "Error connecting to backend", "Unable to find your orders now. Please try again later.");
                    }
                }
            });
        }

        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrderStatusActivity.class);
                Order order = (Order) adapter.getItem(position);
                intent.putExtra(OrderStatusActivity.ORDER_OBJECTID, order.getObjectId());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
