package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.stefi.licentamultibankingapp.R;

public class SignUpFragment4 extends Fragment {

    private EditText[] pinFields;
    private SwitchMaterial switchBiometric;
    private Button btnNext, btnSkip, btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pinFields = new EditText[]{
                view.findViewById(R.id.pin1),
                view.findViewById(R.id.pin2),
                view.findViewById(R.id.pin3),
                view.findViewById(R.id.pin4),
                view.findViewById(R.id.pin5),
                view.findViewById(R.id.pin6)
        };

        switchBiometric = view.findViewById(R.id.switchBiometric);
        btnNext = view.findViewById(R.id.btnNext);
        btnSkip = view.findViewById(R.id.btnSkip);
        btnBack = view.findViewById(R.id.btnBack);

        setupPinFields();

        btnNext.setOnClickListener(v -> {
            String pin = getPinValue();
            if (pin.length() < 6) {
                Toast.makeText(getContext(), "Introdu PIN-ul complet (6 cifre)!", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("pin", pin);
            bundle.putBoolean("biometricEnabled", switchBiometric.isChecked());
            if (getActivity() instanceof SignUpNavigator) {
                ((SignUpNavigator) getActivity()).nextStep(bundle);
            }
        });

        btnSkip.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpNavigator) {
                ((SignUpNavigator) getActivity()).nextStep(new Bundle());
            }
        });

        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpNavigator) {
                ((SignUpNavigator) getActivity()).previousStep();
            }
        });
    }

    private void setupPinFields() {
        for (int i = 0; i < pinFields.length; i++) {
            final int index = i;
            pinFields[i].addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < pinFields.length - 1) {
                        pinFields[index + 1].requestFocus();
                    }
                    if (s.length() == 0 && index > 0) {
                        pinFields[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
    }

    private String getPinValue() {
        StringBuilder pin = new StringBuilder();
        for (EditText field : pinFields) {
            pin.append(field.getText().toString());
        }
        return pin.toString();
    }
}