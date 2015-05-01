package com.troyling.foodrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.foodrack.adapter.CartListAdapter;
import com.foodrack.helpers.DataHelper;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Item;
import com.foodrack.models.MenuItem;
import com.foodrack.models.Order;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import org.apache.http.Header;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by ChandlerWu on 4/17/15.
 */
public class CartActivity extends ActionBarActivity {
    private final int REQUEST_CODE = 100;
    private final double TAX_RATE_MASS = 0.0625;
    private final double DELIVERY_RATE = 0.12;

    Button payButton;
    String clientToken;
    ListView listView;
    List<Item> items;

    TextView taxTextView;
    TextView deliveryTextView;
    TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_layout);

        listView = (ListView) findViewById(R.id.listView);
        taxTextView = (TextView) findViewById(R.id.textViewTax);
        deliveryTextView = (TextView) findViewById(R.id.textViewDelivery);
        totalTextView = (TextView) findViewById(R.id.textViewTotal);
        payButton = (Button) this.findViewById(R.id.buttonPay);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, BraintreePaymentActivity.class);
                intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, clientToken);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        loadShoppingCartToView();

        // TODO token should be retrieved when the app is launched
        getTokenFromServer();
    }

    private void loadShoppingCartToView() {
        Order cart = DataHelper.getInstance().getShoppingCart();
        ParseRelation<Item> itemRelation = cart.getItems();

        if (itemRelation != null) {
            ParseQuery<Item> itemQuery = itemRelation.getQuery();
            itemQuery.include(Item.MENU_ITEM);
            itemQuery.fromLocalDatastore();
            itemQuery.findInBackground(new FindCallback<Item>() {
                @Override
                public void done(List<Item> itemList, ParseException e) {
                    if (e == null) {
                        if (itemList.size() > 0) {
                            items = itemList;
                            CartListAdapter adapter = new CartListAdapter(CartActivity.this, items);
                            listView.setAdapter(adapter);

                            calculateFee();

                        } else {
                            // cart is empty
                            // TODO show view when cart is empty
                        }
                    } else {
                        // error reading local datastore
                        ErrorHelper.getInstance().promptError(CartActivity.this, "CartActivity", e.getMessage());
                    }
                }
            });
        } else {
            // error??
            ErrorHelper.getInstance().promptError(CartActivity.this, "CartActivity", "error?");
        }

        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO add putextra here to pass essential info to the
                Intent intent = new Intent(CartActivity.this, ItemActivity.class);
                //intent.putExtra(FOOD_NAME_MESSAGE, childName);
                startActivity(intent);
            }
        });
    }

    private void calculateFee() {
        double itemTotal = 0;
        double tax = 0;
        double deliveryFee = 0;
        double total = 0;

        for (Item i : items) {
            MenuItem menuItem = i.getMenuItem();
            double itemPrice = menuItem.getPrice();
            int quantity = i.getNumOfItems();
            itemTotal += itemPrice * quantity;
        }

        tax = round(TAX_RATE_MASS * itemTotal, 2);
        deliveryFee = round(DELIVERY_RATE * itemTotal, 2);
        total = round(itemTotal + tax + deliveryFee, 2);

        // set view
        taxTextView.setText(Double.toString(tax));
        deliveryTextView.setText(Double.toString(deliveryFee));
        totalTextView.setText((Double.toString(total)));
    }

    /**
     * Round a given value to the given decimal
     * @param value
     * @param places
     * @return the rounded number
     */
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void getTokenFromServer() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://foodrackserver.herokuapp.com/transaction/client_token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("Error...", "Connecting server");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                clientToken = s;
                ErrorHelper.getInstance().promptError(CartActivity.this, "Get", clientToken);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                ErrorHelper.getInstance().promptError(this, "Nonce", paymentMethodNonce);
                postNonceToServer(paymentMethodNonce);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    private void postNonceToServer(String paymentMethodNonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", paymentMethodNonce);
        // TODO reflect the amount here
        params.put("amount", 3);
        client.post("https://foodrackserver.herokuapp.com/transaction/purchase", params,
            new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    ErrorHelper.getInstance().promptError(CartActivity.this, "Success", "Successfully processed credit card.");
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    ErrorHelper.getInstance().promptError(CartActivity.this, "Fail", throwable.getMessage());
                }
            }
        );
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_empty_cart) {
            // alert user
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Remove everything in your shopping cart?").setPositiveButton("Empty", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataHelper.getInstance().emptyShoppingCart();
                    loadShoppingCartToView();
                    Toast.makeText(CartActivity.this, "Items have been removed from your cart.", Toast.LENGTH_SHORT).show();

                    // TODO might be better way to reload the view
                    startActivity(getIntent());
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                }
            });
            builder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
