package com.troyling.foodrack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by ChandlerWu on 4/13/15.
 */
public class SignUpFragment extends Fragment {
    View rootView;

    CheckBox agreementCheckbox;
    EditText nameField;
    EditText emailField;
    EditText phoneField;
    EditText passwordField;
    Button confirmButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.sign_up_layout, container, false);

        agreementCheckbox = (CheckBox)rootView.findViewById(R.id.agreement_checkbox);
        nameField = (EditText)rootView.findViewById(R.id.name_field);
        emailField = (EditText)rootView.findViewById(R.id.email_field);
        phoneField = (EditText)rootView.findViewById(R.id.phone_field);
        passwordField = (EditText)rootView.findViewById(R.id.password_field);
        confirmButton = (Button)rootView.findViewById(R.id.sign_up_button);

//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username
//            }
//        });

        return rootView;
    }

    private void confirmSignUp() {

    }

}
