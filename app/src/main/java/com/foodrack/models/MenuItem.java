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

    public final static String DRINK = "Drink";
    public final static String FOOD = "Food";
    public final static String DESSERT = "Dessert";

    public final static String SIZE_LARGE = "large";
    public final static String SIZE_MEDIUM = "medium";
    public final static String SIZE_SMALL = "small";
    public static final String COMBOWITHCOFFE = "ComboWithCoffee";
    public static final String COMBOWITHTTEA = "ComboWithHB";

    public String getName() {
        return getString(NAME);
    }

    public double getPrice() {
        return getDouble(PRICE);
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
