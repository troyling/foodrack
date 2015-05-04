package com.foodrack.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by ChandlerWu on 4/18/15.
 * Model for Item as subclass of ParseObject
 *
 */

@ParseClassName("Item")
public class Item extends ParseObject {
    public static final String MENU_ITEM = "menuItem";

    public Item() {
        super();
    }

    public void setMenuItem(MenuItem menuItem) {
        put(MENU_ITEM, menuItem);
    }

    public MenuItem getMenuItem() {
        return (MenuItem)get(MENU_ITEM);
    }

    public void setNumOfItems(int num) {
        put("numOfItem", num);
    }

    public void setNotes(String notes) {
        put("notes", notes);
    }

    public int getNumOfItems() {
        return getInt("numOfItem");
    }

}
