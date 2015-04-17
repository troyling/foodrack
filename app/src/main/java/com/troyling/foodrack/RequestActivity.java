package com.troyling.foodrack;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by ChandlerWu on 4/17/15.
 * An activity that handles requests for new restaurants from users
 *
 */
public class RequestActivity extends ActionBarActivity {

    Button sendButton;
    EditText nameText;
    EditText reasonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_new_place);

        nameText = (EditText)this.findViewById(R.id.editTextName);  // To be Implemented;
        reasonText = (EditText)this.findViewById(R.id.editTextReason); // To be Implemented;
        sendButton = (Button)this.findViewById(R.id.buttonRequest);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Request has been sent!",
                        Toast.LENGTH_SHORT).show();
                // To be Implemented;
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

