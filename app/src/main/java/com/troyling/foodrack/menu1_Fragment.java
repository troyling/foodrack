package com.troyling.foodrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class menu1_Fragment extends Fragment {
    TextView ddText;
    TextView lcText;
    Button requestButton;
    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_select_from_layout, container, false);

        ddText = (TextView) rootView.findViewById(R.id.textDD);
        lcText = (TextView) rootView.findViewById(R.id.textLibCafe);
        requestButton = (Button) rootView.findViewById(R.id.requestButton);


        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RequestActivity.class);
                startActivity(intent);
            }
        });

        ddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SelectFoodActivity.class);
                intent.putExtra(SelectFoodActivity.RESTAURANT_NAME, "Dunkin' Donuts");
                startActivity(intent);
            }
        });

        lcText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SelectFoodActivity.class);
                intent.putExtra(SelectFoodActivity.RESTAURANT_NAME, "Gordon Library Café");
                startActivity(intent);
            }
        });

        return rootView;
    }

}
