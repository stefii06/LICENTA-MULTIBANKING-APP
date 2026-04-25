package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.stefi.licentamultibankingapp.R;

public class SignUpFragment2 extends Fragment {

    private TextInputEditText etPassword, etConfirmPassword;
    private TextInputLayout tilPassword, tilConfirmPassword;
    private ProgressBar progressPassword;
    private TextView tvReqLength, tvReqUpper, tvReqLower, tvReqNumber, tvReqSpecial;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);
        progressPassword = view.findViewById(R.id.progressPassword);
        tvReqLength = view.findViewById(R.id.tvReqLength);
        tvReqUpper = view.findViewById(R.id.tvReqUpper);
        tvReqLower = view.findViewById(R.id.tvReqLower);
        tvReqNumber = view.findViewById(R.id.tvReqNumber);
        tvReqSpecial = view.findViewById(R.id.tvReqSpecial);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordRequirements(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button btnNext = view.findViewById(R.id.btnNext);
        Button btnBack = view.findViewById(R.id.btnBack);

        btnNext.setOnClickListener(v -> {
            if (validatePassword()) {
                Bundle bundle = new Bundle();
                bundle.putString("password", etPassword.getText().toString());
                if (getActivity() instanceof SignUpNavigator) {
                    ((SignUpNavigator) getActivity()).nextStep(bundle);
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof SignUpNavigator) {
                ((SignUpNavigator) getActivity()).previousStep();
            }
        });
    }

    private void updatePasswordRequirements(String password) {
        boolean hasLength = password.length() >= 8;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*].*");

        updateRequirement(tvReqLength, hasLength, "Minim 8 caractere");
        updateRequirement(tvReqUpper, hasUpper, "Cel puțin o literă mare");
        updateRequirement(tvReqLower, hasLower, "Cel puțin o literă mică");
        updateRequirement(tvReqNumber, hasNumber, "Cel puțin o cifră");
        updateRequirement(tvReqSpecial, hasSpecial, "Cel puțin un caracter special (!@#$%)");

        int score = (hasLength ? 20 : 0) + (hasUpper ? 20 : 0) +
                (hasLower ? 20 : 0) + (hasNumber ? 20 : 0) +
                (hasSpecial ? 20 : 0);

        progressPassword.setProgress(score);

        if (score <= 40) {
            progressPassword.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFE53935));
        } else if (score <= 80) {
            progressPassword.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFFFA000));
        } else {
            progressPassword.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        }
    }

    private void updateRequirement(TextView tv, boolean met, String text) {
        if (met) {
            tv.setText("✓  " + text);
            tv.setTextColor(0xFF4CAF50);
        } else {
            tv.setText("✗  " + text);
            tv.setTextColor(0xFFE53935);
        }
    }

    private boolean validatePassword() {
        String password = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();

        if (password.length() < 8 || !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*") ||
                !password.matches(".*[!@#$%^&*].*")) {
            tilPassword.setError("Parola nu îndeplinește cerințele!");
            return false;
        }
        tilPassword.setError(null);

        if (!password.equals(confirm)) {
            tilConfirmPassword.setError("Parolele nu coincid!");
            return false;
        }
        tilConfirmPassword.setError(null);

        return true;
    }
}