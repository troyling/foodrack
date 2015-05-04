package com.foodrack.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ChandlerWu on 4/28/15.
 */
@ParseClassName("Request")
public class Request extends ParseObject{

    public Request() {
        super();
    }

    public void setNameOfPlace(String name) {
        put("nameOfNewPlace", name);
    }

    public void setReason(String reason) {
        put("reasonForNewPlace", reason);
    }

    public void setOwner(ParseUser user) {
        put("owner", user);
    }

}
