package com.foodrack.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foodrack.models.Item;
import com.troyling.foodrack.R;

import java.util.List;

/**
 * Created by ChandlerWu on 4/28/15.
 */
public class CartListAdapter extends BaseAdapter {

    private Context context;
    private List<Item> listItem;

    public CartListAdapter(Context context, List<Item> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item entry = listItem.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cart_list_row, null);
        }
        TextView itemName = (TextView) convertView.findViewById(R.id.cartItemName);
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // to be changed
        //itemName.setText(entry.getMenuItem().getName());
        itemName.setText(entry.getName());


        TextView itemQty = (TextView) convertView.findViewById(R.id.cartItemQty);
        itemQty.setText(Integer.toString(entry.getNumOfItems()));

        TextView itemPrice = (TextView) convertView.findViewById(R.id.cartItemPrice);
        // To be changed to following
        //itemPrice.setText(Double.toString(entry.getMenuItem().getPrice()));
        itemPrice.setText("$" + Double.toString(entry.getPrice()));

        return convertView;
    }

}
