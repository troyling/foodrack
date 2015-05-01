package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.foodrack.adapter.MenuExpandableListAdapter;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.MenuItem;
import com.foodrack.models.Order;
import com.foodrack.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ChandlerWu on 4/17/15.
 */
public class SelectFoodActivity extends ActionBarActivity {
    public final static String RESTAURANT_KEY = "RESTAURANT";
    public final static String RESTAURANT_NAME = "RESTAURANT_NAME";

    public final static String FOOD_NAME_MESSAGE = "Foodrack.SelectFoodActivity.NameOfFood.MESSAGE";
    public final static String MENUITEM_OBJECTID = "MenuItem.MESSAGE";

    Order order;
    MenuItem menuItem;
    MenuExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Button orderButton;
    List<MenuItem> listOfMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_food);

        Intent intent = getIntent();
        String restaurantName = intent.getStringExtra(RESTAURANT_NAME);

        // preparing list data
        prepareListData(restaurantName);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListMenu);

        listAdapter = new MenuExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                String childName = listDataChild.get(listDataHeader.get(groupPosition)).get(
                        childPosition);
                Intent intent = new Intent(SelectFoodActivity.this, ItemActivity.class);
                intent.putExtra(FOOD_NAME_MESSAGE, childName);
                for(int i = 0; i < listOfMenuItem.size(); i++) {
                    if (listOfMenuItem.get(i).getName().equals(childName)) {
                        menuItem = listOfMenuItem.get(i);
                    }
                }
                intent.putExtra(MENUITEM_OBJECTID, menuItem.getObjectId());
                startActivity(intent);
                return false;
            }
        });

        orderButton = (Button) findViewById(R.id.buttonOrder);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFoodActivity.this, AddressActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
     * Preparing the list data
     */
    private void prepareListData(String restaurantName) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Food");
        listDataHeader.add("Drink");
        listDataHeader.add("Desert");
        final List<String> headFood = new ArrayList<String>();
        final List<String> headDrink = new ArrayList<String>();
        final List<String> headDesert = new ArrayList<String>();
        final List<String> headOther = new ArrayList<String>();

        // Find menus from Local
        ParseQuery<Restaurant> restaurantQuery = ParseQuery.getQuery(Restaurant.class);
        restaurantQuery.fromLocalDatastore();
        restaurantQuery.whereEqualTo(Restaurant.NAME, restaurantName);
        restaurantQuery.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> restaurants, ParseException e) {
                if (e == null && restaurants.size() == 1) {
                    // Find menuItems
                    final ParseQuery<MenuItem> menuItemQuery = ParseQuery.getQuery(MenuItem.class);
                    menuItemQuery.fromLocalDatastore();
                    menuItemQuery.whereEqualTo(MenuItem.RESTAURANT, restaurants.get(0));
                    menuItemQuery.findInBackground(new FindCallback<MenuItem>() {
                        @Override
                        public void done(List<MenuItem> menuItems, ParseException e) {
                            if (e == null) {
                                Log.i("Menu Items", "Size: " + menuItems.size());
                                listOfMenuItem = menuItems;
                                // Add each item to its category
                                for(int i = 0; i < menuItems.size(); i++) {
                                    String category = menuItems.get(i).getCategory();
                                    String itemName = menuItems.get(i).getName();
                                    if (category.equals(MenuItem.FOOD)) {
                                        headFood.add(itemName);
                                    }
                                    else if (category.equals(MenuItem.DRINK)) {
                                        headDrink.add(itemName);
                                    }
                                    else if (category.equals(MenuItem.DESSERT)) {
                                        headDesert.add(itemName);
                                    }
                                    else {
                                        listDataHeader.add("Others");
                                        headOther.add(itemName);
                                    }
                                }
                            }
                            else {
                                ErrorHelper.getInstance().promptError(SelectFoodActivity.this,
                                        "Error", e.getMessage());
                            }
                        }
                    });
                } else {
                    ErrorHelper.getInstance().promptError(SelectFoodActivity.this, "Error", "Unable " +
                            "to read menu for the selected restaurant...");
                }
            }
        });

        listDataChild.put(listDataHeader.get(0), headFood); // Header, Child data
        listDataChild.put(listDataHeader.get(1), headDrink);
        listDataChild.put(listDataHeader.get(2), headDesert);
        // If nothing is in the "other" category, then no such head
        if (headOther.size() > 0) {
            listDataChild.put(listDataHeader.get(3), headOther);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
