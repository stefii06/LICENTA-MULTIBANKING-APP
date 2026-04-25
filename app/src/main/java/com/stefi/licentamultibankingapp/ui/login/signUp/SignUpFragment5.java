package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.ui.login.SignInActivity;

public class SignUpFragment5 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        Button btnGoToLogin = view.findViewById(R.id.btnGoToLogin);

        // Preluam numele din bundle
        if (getArguments() != null) {
            String firstName = getArguments().getString("firstName", "");
            String lastName = getArguments().getString("lastName", "");
            tvWelcomeName.setText("Bine ai venit, " + firstName + " " + lastName + "!");
        }

        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}