package com.troyling.foodrack;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_order_layout);

        //generate list
        ArrayList<String> list = new ArrayList<String>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        list.add("item4");
        list.add("item5");

        //generate list
        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("item1");
        list2.add("item2");
        list2.add("item3");
        list2.add("item4");
        list2.add("item5");

        //instantiate custom adapter
        MyAddedItemsListAdapter adapter = new MyAddedItemsListAdapter(list, this);
        MyMenuListAdapter adapter2 = new MyMenuListAdapter(list2, this);

        //handle listview and assign adapter
        ListView lView = (ListView) findViewById(R.id.addedListView);
        lView.setAdapter(adapter);

        //handle listview and assign adapter
        ListView lView2 = (ListView) findViewById(R.id.menuListView);
        lView2.setAdapter(adapter2);

    }
}
