package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.foodrack.adapter.CartListAdapter;
import com.foodrack.helpers.ErrorHelper;
import com.foodrack.models.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChandlerWu on 4/17/15.
 */
public class CartActivity extends ActionBarActivity {
    private final int REQUEST_CODE = 100;
    Button payButton;
    String clientToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_layout);

        ListView list = (ListView) findViewById(R.id.listView);

        List<Item> listOfItems = new ArrayList<Item>();
        //!!!!!!!!!!!!!!!!!!!!
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        listOfItems.add(new Item());
        //!!!!!!!!!!!!!!!!!!!!!

        CartListAdapter adapter = new CartListAdapter(this, listOfItems);

        list.setAdapter(adapter);
        list.setDividerHeight(0);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CartActivity.this, ItemActivity.class);
                //intent.putExtra(FOOD_NAME_MESSAGE, childName);
                startActivity(intent);

            }
        });

        getTokenFromServer();

        payButton = (Button) this.findViewById(R.id.buttonPay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, BraintreePaymentActivity.class);
                intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, clientToken);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
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
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
