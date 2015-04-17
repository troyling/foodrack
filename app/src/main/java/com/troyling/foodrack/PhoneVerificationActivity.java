package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foodrack.helpers.ErrorHelper;
import com.foodrack.helpers.TextVerificationHelper;
import com.parse.ParseUser;


public class PhoneVerificationActivity extends ActionBarActivity {
    public final static String PHONE_NUMBER = "phoneNumber";
    private final static String TEXT_PROMPT = "A 6 digit verification code has been sent to ";
    private String phoneNumber;
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

        if (b != null) {
            phoneNumber = (String) b.get(PHONE_NUMBER);
            phoneNumberTextView.setText(TEXT_PROMPT + phoneNumber);
        }

        sendVerificationCode();

        // add listeners verifying code
        verificationCodeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = verificationCodeField.getText().toString();
                if (input.length() != 6) return;

                if (TextVerificationHelper.getInstance().isCodeValid(Integer.parseInt(input))) {
                    // change the verification status of the current user in backend
                    final ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("isVerified", true);
                    currentUser.saveEventually();

                    // move to main view
                    Toast.makeText(PhoneVerificationActivity.this, "Phone number is successfully verified.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PhoneVerificationActivity.this, MainActivity.class));
                    finish();
                } else {
                    ErrorHelper.getInstance().promptError(PhoneVerificationActivity.this, "Error..", "The verification code you entered is not the same as the one sent to you.");
                }
            }
        });

        // add listener to send again button
        sendAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        // add listener to change number
        changeNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneVerificationActivity.this, ChangeNumberActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendVerificationCode() {
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
