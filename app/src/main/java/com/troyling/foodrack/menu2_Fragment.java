package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class menu2_Fragment extends Fragment {
    View rootView;
    Button statusButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_your_orders_layout, container, false);

        statusButton = (Button) rootView.findViewById(R.id.button2);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderStatusActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
