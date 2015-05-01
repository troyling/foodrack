package com.foodrack.helpers;

import android.util.Log;

import com.foodrack.models.Item;
import com.foodrack.models.MenuItem;
import com.foodrack.models.Order;
import com.foodrack.models.Restaurant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import org.apache.http.Header;

import java.util.List;

/**
 * This is an error class that follows singleton pattern
 */
public class DataHelper {
    private final static String TAG_DATA_HELPER_ERROR = "DataHelper";
    private final static String RESTAURANTS = "restaurants";
    private final static String MENU_ITEMS = "menuItems";
    private final static String ORDERS = "orders";

    private static DataHelper instance;
    private static Order shoppingCart;
    private static int numItemsInShoppingCart;
    private static String clientToken;          // token used for credit card precessing

    private DataHelper() {

    }

    public static DataHelper getInstance() {
        if (instance == null) {
            instance = new DataHelper();
            clientToken = null;
            getTokenFromServer();
        }

        return instance;
    }

    private static void getTokenFromServer() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://foodrackserver.herokuapp.com/transaction/client_token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("TAG_DATA_HELPER_ERROR", "Error connecting server...");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                clientToken = s;
            }
        });
    }

    public void syncDataInBackground() {
        // update restaurants and menu
        syncRestaurantInBackground();
        syncMenuItemInBackground();
    }

    /**
     * Clear cached orders in the cart in there is any being saved in cached
     */
    public void clearCachedOrderIfNecessary() {
        // query the data from localstore and clear all data in the background
        ParseQuery<Order> orderQuery = ParseQuery.getQuery(Order.class);
        orderQuery.orderByDescending("createdAt");
        orderQuery.fromLocalDatastore();
        orderQuery.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> orderList, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(ORDERS, orderList);

                    // use the nonempty order as user's shopping cart
                    for (final Order order : orderList) {
                        ParseRelation<Item> itemRelation = order.getItems();
                        if (itemRelation != null) {
                            ParseQuery<Item> itemQuery = order.getItems().getQuery();
                            itemQuery.fromLocalDatastore();
                            itemQuery.findInBackground(new FindCallback<Item>() {
                                @Override
                                public void done(List<Item> items, ParseException e) {
                                    if (items != null) {
                                        if (items.size() > 0) {
                                            shoppingCart = order;
                                            pinShoppingCartInBackground();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public int getNumItemsInShoppingCart() {
        return numItemsInShoppingCart;
    }

    public String getClientToken() {
        return clientToken;
    }

    /**
     * Get the shopping cart for current user
     *
     * @return
     */
    public Order getShoppingCart() {
        if (shoppingCart == null) {
            newShoppingCart();
        }
        return shoppingCart;
    }

    /*
     * Cache shopping cart locally
     */
    public void pinShoppingCartInBackground() {
        shoppingCart.pinInBackground(ORDERS);
        updateNumItemsInShoppingCartInBackground();
    }

    public void emptyShoppingCart() {
        shoppingCart.unpinInBackground(ORDERS);
        newShoppingCart();
    }

    private void newShoppingCart() {
        shoppingCart = new Order();
        numItemsInShoppingCart = 0;
    }

    private void updateNumItemsInShoppingCartInBackground() {
        numItemsInShoppingCart = 0;
        Order order = getShoppingCart();
        ParseRelation<Item> itemRelation = order.getItems();
        if (itemRelation != null) {
            ParseQuery<Item> itemQuery = itemRelation.getQuery();
            itemQuery.fromLocalDatastore();
            itemQuery.findInBackground(new FindCallback<Item>() {
                @Override
                public void done(List<Item> items, ParseException e) {
                    if (e == null) {
                        for (Item i : items) {
                            numItemsInShoppingCart += i.getNumOfItems();
                        }
                    } else {
                        Log.e("TAG_DATA_HELPER_ERROR", e.getMessage());
                    }
                }
            });
        }
    }

    private void syncMenuItemInBackground() {
        ParseQuery<MenuItem> menuItemQuery = ParseQuery.getQuery(MenuItem.class);
        menuItemQuery.include(MenuItem.RESTAURANT);
        menuItemQuery.orderByAscending(MenuItem.RESTAURANT); // FIXME THIS MIGHT BE ERROR PRONE
        menuItemQuery.findInBackground(new FindCallback<MenuItem>() {
            @Override
            public void done(final List menuItemList, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(MENU_ITEMS, menuItemList, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject.pinAllInBackground(MENU_ITEMS, menuItemList);
                                Log.i("TAG_DATA_HELPER_ERROR", "Save " + menuItemList.size() + " MENUS in localstore");
                            } else {
                                Log.e("TAG_DATA_HELPER_ERROR", "Unable to save MENU data in localstore.");
                            }
                        }
                    });
                } else {
                    Log.e("TAG_DATA_HELPER_ERROR", "Unable to get MENU from backend.");
                }
            }
        });
    }

    private void syncRestaurantInBackground() {
        ParseQuery<Restaurant> restaurantQuery = ParseQuery.getQuery(Restaurant.class);
        restaurantQuery.orderByAscending(Restaurant.NAME);
        restaurantQuery.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(final List restaurantList, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(RESTAURANTS, restaurantList, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject.pinAllInBackground(RESTAURANTS, restaurantList);
                                Log.i("TAG_DATA_HELPER_ERROR", "Save " + restaurantList.size() + " RESTAURANTS in localstore");
                            } else {
                                Log.e("TAG_DATA_HELPER_ERROR", "Unable to save RESTAURANT data in localstore.");
                            }
                        }
                    });
                } else {
                    Log.e("TAG_DATA_HELPER_ERROR", "Unable to get RESTAURANT from backend.");
                }
            }
        });
    }
}
