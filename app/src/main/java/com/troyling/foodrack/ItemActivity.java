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

import com.foodrack.helpers.DataHelper;
import com.foodrack.models.Item;
import com.foodrack.models.MenuItem;
import com.foodrack.models.Order;

/**
 * Created by ChandlerWu on 4/29/15.
 */
public class ItemActivity extends ActionBarActivity{
    EditText notes;
    NumberPicker np;
    MenuItem menuItem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_specify);

        // Get the name of food from previous activity
        Intent intent = getIntent();
        String nameOfFood = intent.getStringExtra(SelectFoodActivity.FOOD_NAME_MESSAGE);

        // Get the menuItem
        menuItem = (MenuItem) intent.getSerializableExtra(SelectFoodActivity.MENUITEM_MESSAGE);

        // Set the title of this layout
        TextView title = (TextView) findViewById(R.id.nameOfFood);
        title.setText(nameOfFood);

        // Number Picker Setup
        np = (NumberPicker) findViewById(R.id.numberPicker);
        String[] nums = new String[20];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        np.setMinValue(1);
        np.setMaxValue(20);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(1);

        // Notes
        notes = (EditText) findViewById(R.id.foodNotes);

//        // Query the order
//        ParseQuery<Order> orderParseQuery = ParseQuery.getQuery(Order.class);
//        orderParseQuery.fromLocalDatastore();
//        orderParseQuery.findInBackground(new FindCallback<Order>() {
//            @Override
//            public void done(List<Order> orders, ParseException e) {
//                if (e == null && orders.size() == 1) {
//                    order = orders.get(0);
//                }
//                else {
//                    ErrorHelper.getInstance().promptError(ItemActivity.this,"Order error",
//                            "NO order found or more than one order");
//                }
//            }
//        });

        // TODO check if there is item in cart already

        // Continue button
        Button button = (Button) findViewById(R.id.buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the item
                Item item = new Item();
                item.setNumOfItems(np.getValue());
                item.setNotes(notes.getText().toString());
                item.setMenuItem(menuItem);

                // Update Order
                Order cart = DataHelper.getInstance().getShoppingCart();
                cart.addItem(item);
                DataHelper.getInstance().pinShoppingCartInBackground();
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
