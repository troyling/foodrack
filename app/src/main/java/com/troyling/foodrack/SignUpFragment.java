package com.troyling.foodrack;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class SignUpFragment extends Fragment {
    private static String DEBUG_ERROR_FLAG = "Sign Up Error: ";

    private static final String ACCOUNT_SID = "ACdbf3ce43b755440dd62b66402b06b947";
    private static final String AUTH_TOKEN = "17d6f02d5ef262956b941c21c50ffba9";

    View rootView;

    CheckBox agreementCheckbox;
    EditText nameField;
    EditText emailField;
    EditText phoneField;
    EditText passwordField;
    Button confirmButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.sign_up_layout, container, false);

        agreementCheckbox = (CheckBox) rootView.findViewById(R.id.agreement_checkbox);
        nameField = (EditText) rootView.findViewById(R.id.name_field);
        emailField = (EditText) rootView.findViewById(R.id.email_field);
        phoneField = (EditText) rootView.findViewById(R.id.phone_field);
        passwordField = (EditText) rootView.findViewById(R.id.password_field);
        confirmButton = (Button) rootView.findViewById(R.id.sign_up_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = nameField.getText().toString();
                String email = emailField.getText().toString();
                String phoneNumber = phoneField.getText().toString();
                String password = passwordField.getText().toString();
                boolean isAgreed = agreementCheckbox.isChecked();

                if (!isDataValid()) {
                    return;
                }

                int verificationCode = generateVerificationCode();

                // send verification message
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                DefaultHttpClient httpClient = new DefaultHttpClient();
                httpClient.getCredentialsProvider().setCredentials(new AuthScope("api.twilio.com", AuthScope.ANY_PORT), new UsernamePasswordCredentials(ACCOUNT_SID, AUTH_TOKEN));
                HttpPost httpPost = new HttpPost("https://api.twilio.com/2010-04-01/Accounts/ACdbf3ce43b755440dd62b66402b06b947/Messages.json");

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("To", "5082411700"));
                params.add(new BasicNameValuePair("From", "+15085934034"));
                params.add(new BasicNameValuePair("Body", "You Foodrack verification code is " + verificationCode));

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    Log.d("Http Post Response:", response.toString());
                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }

                // Create an account
                ParseUser newUser = new ParseUser();
                newUser.setUsername(phoneNumber);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.put("name", username);
                newUser.put("isVerified", false); // new user required text code verification

                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // user is done signing up
                            
                        } else {
                            // TODO we need to display the error here...
                            Log.e(DEBUG_ERROR_FLAG, e.getMessage());
                        }
                    }
                });
            }
        });

        return rootView;
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

    /**
     * @return 6 digit verification code
     */
    private int generateVerificationCode() {
        Random rnd = new Random();
        return 100000 + rnd.nextInt(900000);
    }
}
