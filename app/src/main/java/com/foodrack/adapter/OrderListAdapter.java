package com.foodrack.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foodrack.models.Order;
import com.troyling.foodrack.R;

import java.util.List;

/**
 * Created by troyling on 5/2/15.
 */
public class OrderListAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orders;

    public OrderListAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order order = orders.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // TODO Layout need to be changed
            convertView = inflater.inflate(R.layout.order_list_admin_row, null);

        }

        TextView orderTime = (TextView) convertView.findViewById(R.id.textTime);
        orderTime.setText(order.getCreatedAt().toString());
        TextView orderStatus = (TextView) convertView.findViewById(R.id.textStatus);
        orderStatus.setText(order.getStatus());

        return convertView;
    }
}
