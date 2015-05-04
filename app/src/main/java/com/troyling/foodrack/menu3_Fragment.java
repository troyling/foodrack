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

import com.foodrack.helpers.ErrorHelper;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class menu3_Fragment extends Fragment {
    View rootView;
    Button buttonAdmin;
    Button buttonLogOut;
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

        buttonLogOut = (Button) rootView.findViewById(R.id.buttonLogOut);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            ErrorHelper.getInstance().promptError(getActivity(), "Error", "Unable to log out");
                        }
                    }
                });
            }
        });

        return rootView;
    }

}
