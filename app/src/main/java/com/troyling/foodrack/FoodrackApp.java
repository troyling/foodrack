package com.troyling.foodrack;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by troyling on 4/13/15.
 */
public class FoodrackApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "m0hPkVIzDsS8cAJMXymRwPfTW9ay1GWetzW8WZTy", "CBYYxxvuVV61B5UKc47TnFTVgJ6qrKi2Etyxgopn");
    }
}
