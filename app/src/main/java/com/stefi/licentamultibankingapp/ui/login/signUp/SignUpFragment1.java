package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.stefi.licentamultibankingapp.R;

public class SignUpFragment1 extends Fragment {

    private TextInputEditText etFirstName, etLastName, etEmail, etPhone;
    private TextInputLayout tilFirstName, tilLastName, tilEmail, tilPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilFirstName = view.findViewById(R.id.tilFirstName);
        tilLastName = view.findViewById(R.id.tilLastName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPhone = view.findViewById(R.id.tilPhone);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);

        Button btnNext = view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> {
            if (validateFields()) {
                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                Bundle bundle = new Bundle();
                bundle.putString("firstName", firstName);
                bundle.putString("lastName", lastName);
                bundle.putString("email", email);
                bundle.putString("phone", phone);

                if (getActivity() instanceof SignUpNavigator) {
                    ((SignUpNavigator) getActivity()).nextStep(bundle);
                }
            }
        });
    }

    private boolean validateFields() {
        boolean valid = true;

        if (etFirstName.getText().toString().trim().isEmpty()) {
            tilFirstName.setError("Numele este obligatoriu");
            valid = false;
        } else {
            tilFirstName.setError(null);
        }

        if (etLastName.getText().toString().trim().isEmpty()) {
            tilLastName.setError("Prenumele este obligatoriu");
            valid = false;
        } else {
            tilLastName.setError(null);
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            tilEmail.setError("Email-ul este obligatoriu");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            tilEmail.setError("Email invalid");
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            tilPhone.setError("Numărul de telefon este obligatoriu");
            valid = false;
        } else {
            tilPhone.setError(null);
        }

        return valid;
    }
}