package com.troyling.foodrack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class menu2_Fragment extends Fragment {
    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_your_orders_layout, container, false);
        return rootView;
    }

}
