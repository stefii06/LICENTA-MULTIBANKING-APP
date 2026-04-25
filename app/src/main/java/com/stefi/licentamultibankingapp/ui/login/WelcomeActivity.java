package com.stefi.licentamultibankingapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.ui.login.signUp.SignUpActivity;
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class)));

        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }
}