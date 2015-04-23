package com.foodrack.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Menu Item is the model used to capture the business model
 */

@ParseClassName("MenuItem")
public class MenuItem extends ParseObject {
    public final static String NAME = "name";
    public final static String PRICE = "price";
    public final static String CATEGORY = "category";
    public final static String SERVING = "serving";
    public final static String RESTAURANT = "restaurant";

    public final static String DRINK = "drink";
    public final static String FOOD = "food";

    public final static String SIZE_LARGE = "large";
    public final static String SIZE_MEDIUM = "medium";
    public final static String SIZE_SMALL = "small";

    public String getName() {
        return getString(NAME);
    }

    public double getPrice() {
        return getLong(PRICE);
    }

    public String getCategory() {
        return getString(CATEGORY);
    }

    public String getServing() {
        return getString(SERVING);
    }

    public Restaurant getRestaurant() {
        return (Restaurant) get(RESTAURANT);
    }

}
