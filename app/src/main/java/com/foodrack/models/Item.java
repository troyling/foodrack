package com.foodrack.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ChandlerWu on 4/18/15.
 * Model for Item as subclass of ParseObject
 *
 */

@ParseClassName("Item")
public class Item extends ParseObject {

    public Item() {
        super();
    }

    // Use put to modify field values
    public void setName(String name) {
        put("foodName", name);
    }

    public String getName() {
        return getString("foodName");
    }

    public void setPrice(double price) {
        put("price", price);
    }

    public double getPrice() {
        return getLong("price");
    }

    public void setNumOfItems(int num) {
        put("numOfItem", num);
    }

    public int getNumOfItems() {
        return getInt("numOfItem");
    }

}
