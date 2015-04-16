package com.foodrack.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by troyling on 4/16/15.
 * This is an error class that follows singleton pattern
 */
public class ErrorHelper {

    private static ErrorHelper instance;

    private ErrorHelper() {}

    public static ErrorHelper getInstance() {
        if (instance == null) {
            instance = new ErrorHelper();
        }
        return instance;
    }

    /**
     * Display error message
     * @param title Title for the Dialog
     * @param message Error message for the Dialog
     */
    public void promptError(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        }).show();
    }
}
