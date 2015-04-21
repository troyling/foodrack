package com.foodrack.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by ChandlerWu on 4/18/15.
 * Model for order as subclass of ParseObject
 *
 */

@ParseClassName("Order")
public class Order extends ParseObject {

    public Order() {
        super();
    }

    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    public void setOwner(ParseUser user) {
        put("owner", user);
    }

    // Use put to modify field values
    public void setName(String name) {
        put("orderName", name);
    }

    public String getName() {
        return getString("orderName");
    }

    public ParseRelation<Item> getItemRelation() {
        return getRelation("item");
    }

    public void addItem(Item item) {
        getItemRelation().add(item);
        //saveInBackground();
    }

    public void removeItem(Item item) {
        getItemRelation().remove(item);
        //saveInBackground();
    }

    public void setIsPaid(boolean bl) {
        put("isPaid", bl);
    }

    public boolean getIsPaid() {
        return getBoolean("isPaid");
    }

}
