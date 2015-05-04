package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.foodrack.helpers.ErrorHelper;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class SignUpActivity extends ActionBarActivity {
    private static String DEBUG_ERROR_FLAG = "Sign Up Error: ";

    CheckBox agreementCheckbox;
    EditText nameField;
    EditText emailField;
    EditText phoneField;
    EditText passwordField;
    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_layout);

        agreementCheckbox = (CheckBox) this.findViewById(R.id.agreement_checkbox);
        nameField = (EditText) this.findViewById(R.id.name_field);
        emailField = (EditText) this.findViewById(R.id.email_field);
        phoneField = (EditText) this.findViewById(R.id.phone_field);
        passwordField = (EditText) this.findViewById(R.id.password_field);
        confirmButton = (Button) this.findViewById(R.id.sign_up_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = nameField.getText().toString();
                String email = emailField.getText().toString();
                final String phoneNumber = phoneField.getText().toString();
                String password = passwordField.getText().toString();
                boolean isAgreed = agreementCheckbox.isChecked();

                if (!isDataValid()) {
                    return;
                }

                // Create an account
                ParseUser newUser = new ParseUser();
                newUser.setUsername(email);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.put("name", username);
                newUser.put("phone", phoneNumber);
                newUser.put("isVerified", false); // new user required text code verification
                newUser.put("isPaymentAvailable", false); // false since user has yet provide credit card information
                newUser.put("isAdmin", false);

                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // TODO show a new view
                            Intent intent = new Intent(SignUpActivity.this, PhoneVerificationActivity.class);
                            intent.putExtra(PhoneVerificationActivity.PHONE_NUMBER, phoneNumber); // pass phone number to the new activity
                            startActivity(intent);
                            finish();
                        } else {
                            // TODO we need to display the error here...
                            ErrorHelper.getInstance().promptError(SignUpActivity.this, "Error signing up...", e.getMessage());
                            Log.e(DEBUG_ERROR_FLAG, e.getMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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

    /**
     * @return True when user's information is valid
     */
    private boolean isDataValid() {
        String username = nameField.getText().toString();
        String email = emailField.getText().toString();
        String phoneNumber = phoneField.getText().toString();
        String password = passwordField.getText().toString();
        boolean isAgreed = agreementCheckbox.isChecked();

        // form validation
        if (!isAgreed) {
            Log.e(DEBUG_ERROR_FLAG, "Please review and agree with our Terms and Services before proceeding.");
            return false;
        }

        if (username.isEmpty()) {
            // TODO show error message
            Log.e(DEBUG_ERROR_FLAG, "Username is empty");
            return false;
        }

        if (email.isEmpty()) {
            Log.e(DEBUG_ERROR_FLAG, "Email is empty");
            return false;
        }

        if (phoneNumber.isEmpty()) {
            Log.e(DEBUG_ERROR_FLAG, "Phone is empty");
            return false;
        }

        if (password.isEmpty()) {
            Log.e(DEBUG_ERROR_FLAG, "Password is empty");
            return false;
        }

        // TODO discuss the necessity of this...
        if (!email.contains("@wpi.edu")) {
            Log.e(DEBUG_ERROR_FLAG, "Sorry, we are currently only available for WPI community.");
            return false;
            // begin registration
        }

        return true;
    }




}
