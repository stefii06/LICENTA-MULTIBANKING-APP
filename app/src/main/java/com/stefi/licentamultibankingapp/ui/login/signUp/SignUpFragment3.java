package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;

public class SignUpFragment3 extends Fragment {

    private EditText[] otpFields;
    private Button btnVerify, btnBack;
    private TextView tvResendCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        otpFields = new EditText[]{
                view.findViewById(R.id.otp1),
                view.findViewById(R.id.otp2),
                view.findViewById(R.id.otp3),
                view.findViewById(R.id.otp4),
                view.findViewById(R.id.otp5),
                view.findViewById(R.id.otp6)
        };

        btnVerify = view.findViewById(R.id.btnVerify);
        btnBack = view.findViewById(R.id.btnBack);
        tvResendCode = view.findViewById(R.id.tvResendCode);

        setupOtpFields();

        btnVerify.setOnClickListener(v -> {
            String otp = getOtpValue();
            if (otp.length() < 6) {
                Toast.makeText(getContext(), "Introdu codul complet!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (getActivity() instanceof SignUpNavigator) {
                Bundle bundle = new Bundle();
                bundle.putString("otp", otp);
                ((SignUpNavigator) getActivity()).nextStep(bundle);
            }
        });

        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpNavigator) {
                ((SignUpNavigator) getActivity()).previousStep();
            }
        });

        tvResendCode.setOnClickListener(v ->
                Toast.makeText(getContext(), "Cod retrimis! (Firebase necesar)", Toast.LENGTH_SHORT).show());
    }

    private void setupOtpFields() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                    if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private String getOtpValue() {
        StringBuilder otp = new StringBuilder();
        for (EditText field : otpFields) {
            otp.append(field.getText().toString());
        }
        return otp.toString();
    }
}