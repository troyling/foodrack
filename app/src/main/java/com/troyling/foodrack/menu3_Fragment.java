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

import com.parse.ParseUser;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class menu3_Fragment extends Fragment {
    View rootView;
    Button buttonAdmin;
    TextView name;
    TextView phone;
    TextView email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        name = (TextView) rootView.findViewById(R.id.textNameOfAccountHolder);
        phone = (TextView) rootView.findViewById(R.id.textPhoneNumber);
        email = (TextView) rootView.findViewById(R.id.textEmailAddress);

        ParseUser user = ParseUser.getCurrentUser();
        name.setText("Name: " + user.get("name").toString());
        phone.setText("Phone: " + user.get("phone").toString());
        email.setText("Email: " + user.getEmail());

        buttonAdmin = (Button) rootView.findViewById(R.id.buttonAdmin);
        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminListActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

}
