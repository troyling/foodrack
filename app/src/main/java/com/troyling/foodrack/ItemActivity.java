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
import android.widget.Toast;

import com.foodrack.helpers.DataHelper;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Item;
import com.foodrack.models.MenuItem;
import com.foodrack.models.Order;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.math.BigDecimal;

/**
 * Created by ChandlerWu on 4/29/15.
 */
public class ItemActivity extends ActionBarActivity{
    private EditText notes;
    private NumberPicker np;
    private MenuItem menuItem;
    private Double price;
    private TextView textPrice;
    private TextView title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_specify);

        textPrice = (TextView) findViewById(R.id.textPrice);

        // Get the name of food from previous activity
        Intent intent = getIntent();
        String nameOfFood = intent.getStringExtra(SelectFoodActivity.FOOD_NAME_MESSAGE);

        // Get the menuItem
        String objectId = (String) intent.getSerializableExtra(SelectFoodActivity.MENUITEM_OBJECTID);

        // load the menu item
        ParseQuery<MenuItem> menuItemQuery = ParseQuery.getQuery(MenuItem.class);
        menuItemQuery.fromLocalDatastore();
        menuItemQuery.getInBackground(objectId, new GetCallback<MenuItem>() {
            @Override
            public void done(MenuItem mi, ParseException e) {
                if (e == null) {
                    menuItem = mi;
                    price = mi.getPrice();
                    textPrice.setText("Combined price： " + Double.toString(price));
                } else {
                    ErrorHelper.getInstance().promptError(ItemActivity.this, "Error", e.getMessage());
                }
            }
        });

        // Set the title of this layout
        title = (TextView) findViewById(R.id.nameOfFood);
        title.setText(nameOfFood);

        // Number Picker Setup
        np = (NumberPicker) findViewById(R.id.numberPicker);
        String[] nums = new String[20];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i + 1);

        np.setMinValue(1);
        np.setMaxValue(20);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(1);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int num = np.getValue();
                double totalPrice = price * num;
                Double truncatedDouble=new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                textPrice.setText("Combined price： " + Double.toString(truncatedDouble));
            }
        });

        // Notes
        notes = (EditText) findViewById(R.id.foodNotes);

        // Continue button
        Button button = (Button) findViewById(R.id.buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the item
                int numOfItems = np.getValue();
                if (numOfItems > 0) {
                    final Item item = new Item();
                    item.setNumOfItems(numOfItems);
                    item.setNotes(notes.getText().toString());
                    item.setMenuItem(menuItem);
                    item.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            // Update Order
                            Order cart = DataHelper.getInstance().getShoppingCart();
                            cart.addItem(item);
                            DataHelper.getInstance().pinShoppingCartInBackground();
                            Toast.makeText(ItemActivity.this, "Item has been added to your cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_food, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
