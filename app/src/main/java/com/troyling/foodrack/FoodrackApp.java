package com.troyling.foodrack;

import android.app.Application;

import com.firebase.client.Firebase;
import com.foodrack.helpers.DataHelper;
import com.foodrack.models.Item;
import com.foodrack.models.MenuItem;
import com.foodrack.models.Order;
import com.foodrack.models.Request;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by troyling on 4/13/15.
 */
public class FoodrackApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Firebase.setAndroidContext(this);

        // Register subclasses
        ParseObject.registerSubclass(MenuItem.class);
        ParseObject.registerSubclass(Order.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Request.class);
        Parse.initialize(this, "m0hPkVIzDsS8cAJMXymRwPfTW9ay1GWetzW8WZTy", "CBYYxxvuVV61B5UKc47TnFTVgJ6qrKi2Etyxgopn");

        // update data in cache
        DataHelper.getInstance().syncDataInBackground();

        // clear cached empty shopping cart
        DataHelper.getInstance().clearCachedOrderIfNecessary();

    }
}
