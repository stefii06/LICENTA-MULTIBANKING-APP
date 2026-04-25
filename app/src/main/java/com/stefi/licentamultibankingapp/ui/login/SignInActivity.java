package com.stefi.licentamultibankingapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.stefi.licentamultibankingapp.R;

import java.util.concurrent.Executor;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnSignIn, btnQuickAuth;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnQuickAuth = findViewById(R.id.btnQuickAuth);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email invalid!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Momentan navigam direct la dashboard (Firebase il adaugam mai tarziu)
            Toast.makeText(this, "Login reușit!", Toast.LENGTH_SHORT).show();
        });

        btnQuickAuth.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    == BiometricManager.BIOMETRIC_SUCCESS) {
                showBiometricPrompt();
            } else {
                startActivity(new Intent(this, SignInPinActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Funcționalitate disponibilă după integrarea Firebase!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(SignInActivity.this, "Autentificare reușită!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(SignInActivity.this, "Autentificare eșuată!", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autentificare biometrică")
                .setSubtitle("Folosește amprenta pentru a te conecta")
                .setNegativeButtonText("Anulează")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}