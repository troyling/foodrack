package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodrack.helpers.ErrorHelper;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class SignInActivity extends ActionBarActivity {
    EditText signInTokenField;
    EditText passwordField;
    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_layout);

        signInTokenField = (EditText)this.findViewById(R.id.activity_sign_in_token);
        passwordField = (EditText)this.findViewById(R.id.activity_sign_in_password);
        confirmButton = (Button)this.findViewById(R.id.activity_sign_in_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String token = signInTokenField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                Log.d("Login: ", "token: " + token);
                Log.d("Login: ", "Pwd: " + password);

                if (token.isEmpty()) {
                    ErrorHelper.getInstance().promptError(SignInActivity.this, "Error...", "Please enter email or phone number.");
                    return;
                }

                if (password.isEmpty()) {
                    ErrorHelper.getInstance().promptError(SignInActivity.this, "Error...", "Please enter password");
                    return;
                }

                if (isTokenEmail(token)) {
                    ParseUser.logInInBackground(token, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                startMainActivity();
                            } else {
                                ErrorHelper.getInstance().promptError(SignInActivity.this, "Error...", "Email and password provided do not match");
                            }
                        }
                    });
                } else {
                    // query phone number first
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("phone", token);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> parseUsers, ParseException e) {
                            if (e == null) {
                                if (parseUsers.size() != 0) {
                                    ParseUser user = parseUsers.get(0);
                                    // try to login with the given email and password
                                    ParseUser.logInInBackground(user.getUsername(), password, new LogInCallback() {
                                        @Override
                                        public void done(ParseUser parseUser, ParseException e) {
                                            if (e == null) {
                                                startMainActivity();
                                            } else {
                                                ErrorHelper.getInstance().promptError(SignInActivity.this, "Error...", "Phone number and password provided do not match");
                                            }
                                        }
                                    });
                                }
                            } else {
                                ErrorHelper.getInstance().promptError(SignInActivity.this, "Error...", "Phone number and password provided do not match");
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Jump to main activity after successfully logged in
     */
    private void startMainActivity() {
        Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * @param token
     * @return true if the given token is in form of email. false otherwise.
     */
    private boolean isTokenEmail(String token) {
        // TODO need a better regex
        return token.contains("@");
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
}
