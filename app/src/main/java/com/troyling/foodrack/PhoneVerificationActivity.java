package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foodrack.helpers.ErrorHelper;
import com.foodrack.helpers.TextVerificationHelper;


public class PhoneVerificationActivity extends ActionBarActivity {
    public static String PHONE_NUMBER = "phoneNumber";
    private static String TEXT_PROMPT = "A 6 digit verification code has been sent to ";
    private EditText verificationCodeField;
    private Button changeNumberButton;
    private Button sendAgainButton;
    private TextView phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        verificationCodeField = (EditText) this.findViewById(R.id.verification_code_field);
        changeNumberButton = (Button) this.findViewById(R.id.change_number_button);
        sendAgainButton = (Button) this.findViewById(R.id.send_again_button);
        phoneNumberTextView = (TextView) this.findViewById(R.id.phone_number_textView);

        // get data phone number from previous activity
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String phoneNumber = "";
        if (b != null) {
            phoneNumber = (String) b.get(PHONE_NUMBER);
            phoneNumberTextView.setText(TEXT_PROMPT + phoneNumber);
        }

        // send notification
        if (!phoneNumber.isEmpty()) {
            TextVerificationHelper.getInstance().sendVerificationCode(phoneNumber);
        } else {
            ErrorHelper.getInstance().promptError(this, "Error sending code", "Unable to send verification code now. Please try again later");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_verification, menu);
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
