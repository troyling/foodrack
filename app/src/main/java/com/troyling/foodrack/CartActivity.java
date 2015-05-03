package com.troyling.foodrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.http.Header;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ChandlerWu on 4/17/15.
 */
public class CartActivity extends ActionBarActivity {
    private static final int PAYMENT_REQUEST_CODE = 100;
    public static final int LOCATION_REQUEST = 101;
    private final double TAX_RATE_MASS = 0.0625;
    private final double DELIVERY_RATE = 0.12;

    // view
    Button payButton;
    Button locationButton;
    String clientToken;
    ListView listView;
    List<Item> items;

    TextView taxTextView;
    TextView deliveryTextView;
    TextView totalTextView;

    int savedItemCount;
    CartListAdapter adapter;

    double payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_layout);

        listView = (ListView) findViewById(R.id.listView);
        taxTextView = (TextView) findViewById(R.id.textViewTax);
        deliveryTextView = (TextView) findViewById(R.id.textViewDelivery);
        totalTextView = (TextView) findViewById(R.id.textViewTotal);
        payButton = (Button) findViewById(R.id.buttonPay);
        locationButton = (Button) findViewById(R.id.buttonSetLocation);

        clientToken = DataHelper.getInstance().getClientToken();

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                startActivityForResult(intent, LOCATION_REQUEST);
            }
        });


        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, BraintreePaymentActivity.class);
                intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, clientToken);
                startActivityForResult(intent, PAYMENT_REQUEST_CODE);
            }
        });

        setPayButtonVisibleIfNecessary();

        loadShoppingCartToView();
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
                            adapter = new CartListAdapter(CartActivity.this, items);
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
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(CartActivity.this).setMessage("Are you sure you want to remove the select item?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Item item = (Item) adapter.getItem(position);
                        DataHelper.getInstance().removeItemFromShoppingCart(item);
                        loadShoppingCartToView();
                    }
                }).show();
            }
        });
    }

    private void setPayButtonVisibleIfNecessary() {
        if (DataHelper.getInstance().getShoppingCart().getDeliverLocation() != null) {
            payButton.setVisibility(View.VISIBLE);

            // Find address based on lat and lng
            try {
                double lng = DataHelper.getInstance().getShoppingCart().getDeliverLocation().getLongitude();
                double lat = DataHelper.getInstance().getShoppingCart().getDeliverLocation().getLatitude();
                // Set the button name with the address
                locationButton.setText(getGeoAddress(lat, lng));
            } catch (NullPointerException nullExp) {
                Log.d("Address", "Address has not been set");
            }
        } else {
            payButton.setVisibility(View.GONE);
        }
    }

    private void calculateFee() {
        double itemTotal = 0;
        double tax = 0;
        double deliveryFee = 0;
        payment = 0;

        for (Item i : items) {
            MenuItem menuItem = i.getMenuItem();
            double itemPrice = menuItem.getPrice();
            int quantity = i.getNumOfItems();
            itemTotal += itemPrice * quantity;
        }

        tax = round(TAX_RATE_MASS * itemTotal, 2);
        deliveryFee = round(DELIVERY_RATE * itemTotal, 2);
        payment = round(itemTotal + tax + deliveryFee, 2);

        // set view
        taxTextView.setText(Double.toString(tax));
        deliveryTextView.setText(Double.toString(deliveryFee));
        totalTextView.setText((Double.toString(payment)));
    }

    /**
     * Round a given value to the given decimal
     *
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                postNonceToServer(paymentMethodNonce);
            }
        } else if (resultCode == LOCATION_REQUEST) {
            setPayButtonVisibleIfNecessary();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    private void postNonceToServer(final String paymentMethodNonce) {
        savedItemCount = 0;

        // upload the order to backend
        final Order newOrder = DataHelper.getInstance().getShoppingCart();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            newOrder.setOwner(currentUser);
            newOrder.setStatus(Order.STATUS_RECEIVED);
            ParseRelation<Item> itemRelation = newOrder.getItems();
            if (itemRelation != null) {
                ParseQuery<Item> query = itemRelation.getQuery();
                query.fromLocalDatastore();
                query.findInBackground(new FindCallback<Item>() {
                    @Override
                    public void done(final List<Item> items, ParseException e) {
                        if (e == null) {
                            for (Item i : items) {
                                i.saveEventually(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        savedItemCount++;
                                        if (savedItemCount == items.size()) {
                                            // all items have been saved
                                            placeOrder(newOrder, paymentMethodNonce);
                                        }
                                    }
                                });
                            }
                        } else {
                            // TODO log Error
                        }
                    }
                });
            }
        } else {
            ErrorHelper.getInstance().promptError(this, "Error", "Please make sure you have signed in before placing an order.");
        }
    }

    private void placeOrder(Order newOrder, String paymentMethodNonce) {
        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        params.put("payment_method_nonce", paymentMethodNonce);
        params.put("amount", payment);

        newOrder.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // process payment
                    client.post("https://foodrackserver.herokuapp.com/transaction/purchase", params,
                            new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                    Toast.makeText(CartActivity.this, "Successfully process your payment. Thank you.", Toast.LENGTH_LONG);

                                    // empty shopping cart
                                    DataHelper.getInstance().emptyShoppingCart();

                                    //Todo jump to activity to show status view with status update
                                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                    ErrorHelper.getInstance().promptError(CartActivity.this, "Fail", throwable.getMessage());
                                }
                            }
                    );
                } else {
                    ErrorHelper.getInstance().promptError(CartActivity.this, "Error", e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_empty_cart) {
            // alert user
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Remove everything in your shopping cart?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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


    public String getGeoAddress(double lat, double lng) {
        String TAG = "getAddresssNameError";
        String errorMessage = "no addresss";
        String r = " ";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException ioException) {
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + lat +
                    ", Longitude = " +
                    lng, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no address";
                Log.e(TAG, errorMessage);
                r = "No address found";
            }
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = address.getMaxAddressLineIndex(); i > 0; i--) {
                addressFragments.add(address.getAddressLine(i));
                Log.d("address!!!!!!!", address.getAddressLine(i));
                r = address.getAddressLine(i) + " " + r;
            }
        }
        return r;
    }
}
