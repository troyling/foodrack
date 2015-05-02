package com.foodrack.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
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
    public final static String OWNER = "owner";
    public final static String STATUS_RECEIVED = "Received";
    public final static String STATUS_CONFIRMED = "Confirmed";
    public final static String STATUS_IN_TRANSIT = "In transit";
    public final static String STATUS_DELIVERED = "Delivered";
    public final static String DELIVER_LOCATION = "DeliverLocation";

    public Order() {
        super();
    }

    public ParseUser getOwner() {
        return getParseUser(OWNER);
    }

    public void setOwner(ParseUser user) {
        put(OWNER, user);
    }

    // Use put to modify field values
    public void setStatus(String status) {
        put("status", status);
    }

    public String getStatus() {
        return getString("status");
    }

    public ParseRelation<Item> getItems() {
        return getRelation("item");
    }

    public void addItem(Item item) {
        getItems().add(item);
        //saveInBackground();
    }

    public void removeItem(Item item) {
        getItems().remove(item);
        //saveInBackground();
    }

    public void setIsPaid(boolean bl) {
        put("isPaid", bl);
    }

    public boolean getIsPaid() {
        return getBoolean("isPaid");
    }

    public void setDeliverLocation(double latitude, double longitude) {
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put(DELIVER_LOCATION, point);
    }

    public ParseGeoPoint getDeliverLocation() {
        return getParseGeoPoint(DELIVER_LOCATION);
    }

    public void setNotes(String notes) {
        put("notes", notes);
    }

    public String getNotes() {
        return getString("notes");
    }


}
