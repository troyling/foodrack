package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Item;
import com.foodrack.models.MenuItem;
import com.foodrack.models.Order;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by ChandlerWu on 4/29/15.
 */
public class ItemActivity extends ActionBarActivity{

    Order order;
    Item item;
    int quantity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_specify);

        // Get the name of food from previous activity
        Intent intent = getIntent();
        String nameOfFood = intent.getStringExtra(SelectFoodActivity.FOOD_NAME_MESSAGE);
        // Get the menuItem
        MenuItem menuItem = (MenuItem) intent.getSerializableExtra(SelectFoodActivity.MENUITEM_MESSAGE);
        // Set the title of this layout
        TextView title = (TextView) findViewById(R.id.nameOfFood);
        title.setText(nameOfFood);

        // Number Picker Setup
        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        String[] nums = new String[20];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        np.setMinValue(1);
        np.setMaxValue(20);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(1);
        quantity = np.getValue();

        // Notes
        EditText notes = (EditText) findViewById(R.id.foodNotes);

        // Set the item
        item = new Item();
        item.setNumOfItems(quantity);
        item.setNotes(notes.getText().toString());
        item.setMenuItem(menuItem);

        // Query the order
        ParseQuery<Order> orderParseQuery = ParseQuery.getQuery(Order.class);
        orderParseQuery.fromLocalDatastore();
        orderParseQuery.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> orders, ParseException e) {
                if (e == null && orders.size() == 1) {
                    order = orders.get(0);
                }
                else {
                    ErrorHelper.getInstance().promptError(ItemActivity.this,"Order error",
                            "NO order found or more than one order");
                }
            }
        });

        // Continue button
        Button button = (Button) findViewById(R.id.buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update Order
                order.addItem(item);
                finish();
            }
        });
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
