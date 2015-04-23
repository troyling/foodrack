package com.foodrack.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by troyling on 4/23/15.
 */
@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {
    public static final String NAME = "name";

    public String getName() {
        return getString(NAME);
    }
}
