package com.foodrack.helpers;

import android.os.StrictMode;
import android.util.Log;

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
 * This is a helper class that follows singleton pattern for sending text verification.
 */
public class TextVerificationHelper {
    private static final String ACCOUNT_SID = "ACdbf3ce43b755440dd62b66402b06b947";
    private static final String AUTH_TOKEN = "17d6f02d5ef262956b941c21c50ffba9";

    private static TextVerificationHelper instance;
    private static int verificationCode;

    private TextVerificationHelper() {}

    public static TextVerificationHelper getInstance() {
        if (instance == null) {
            instance = new TextVerificationHelper();
        }
        return instance;
    }

    /**
     * Create a new 6 digit verification code
     */
    private void generateVerificationCode() {
        Random rnd = new Random();
        verificationCode = 100000 + rnd.nextInt(900000);
    }

    /**
     * send verification message with POST method to Twilio API
     */
    public void sendVerificationCode(String phoneNumber) {
        generateVerificationCode();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials(new AuthScope("api.twilio.com", AuthScope.ANY_PORT), new UsernamePasswordCredentials(ACCOUNT_SID, AUTH_TOKEN));
        HttpPost httpPost = new HttpPost("https://api.twilio.com/2010-04-01/Accounts/ACdbf3ce43b755440dd62b66402b06b947/Messages.json");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("To", phoneNumber));
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
    }


    /**
     * @return True if the given verification is correct. False otherwise
     */
    public boolean isCodeValid(int code) {
        return code == verificationCode;
    }
}
