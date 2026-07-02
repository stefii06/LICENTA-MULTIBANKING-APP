package com.stefi.licentamultibankingapp.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.AbonamentRepository;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.MockDataGenerator;
import com.stefi.licentamultibankingapp.model.TranzactieRepository;
import com.stefi.licentamultibankingapp.ui.dashboard.DashboardActivity;
import com.stefi.licentamultibankingapp.ui.login.onboarding.OnboardingActivity;
import com.stefi.licentamultibankingapp.utils.DemoDataGenerator;
import com.stefi.licentamultibankingapp.utils.FirestoreManager;

import java.util.concurrent.Executor;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnSignIn, btnQuickAuth;
    private TextView tvForgotPassword, tvEroareSignIn;
    private ProgressBar progressBarSignIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnQuickAuth = findViewById(R.id.btnQuickAuth);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBarSignIn = findViewById(R.id.progressBarSignIn);
        tvEroareSignIn = findViewById(R.id.tvEroareSignIn);



        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                tvEroareSignIn.setText("Completează toate câmpurile!");
                tvEroareSignIn.setVisibility(View.VISIBLE);
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvEroareSignIn.setText("Adresa de email nu este validă!");
                tvEroareSignIn.setVisibility(View.VISIBLE);
                return;
            }

            tvEroareSignIn.setVisibility(View.GONE);
            progressBarSignIn.setVisibility(View.VISIBLE);
            btnSignIn.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // Resetam toate repository-urile
                        FirestoreManager.reset();
                        ContBancarRepository.reset();
                        TranzactieRepository.reset();
                        AbonamentRepository.reset();

                        // Initializam FirestoreManager cu userul nou logat
                        FirestoreManager.getInstance();

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Salvam last_user_id pentru biometrie
                        getSharedPreferences("finmind_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("last_user_id", userId)
                                .apply();

                        // Citim datele personale din Firestore si le salvam local
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(doc -> {
                                    SharedPreferences prefs = getSharedPreferences("user_data_" + userId, MODE_PRIVATE);
                                    if (doc.exists()) {
                                        String fn = doc.getString("firstName") != null ? doc.getString("firstName") : "";
                                        String ln = doc.getString("lastName") != null ? doc.getString("lastName") : "";
                                        String ph = doc.getString("phone") != null ? doc.getString("phone") : "";
                                        prefs.edit()
                                                .putString("firstName", fn)
                                                .putString("lastName", ln)
                                                .putString("phone", ph)
                                                .apply();
                                    }

                                    // Acum incarcam restul datelor
                                    DemoDataGenerator.verificaSiInitializeaza("", "", () -> {
                                        ContBancarRepository.getInstance().incarcaConturi(() -> {
                                            TranzactieRepository.getInstance().incarcaTranzactii(() -> {
                                                AbonamentRepository.getInstance().incarcaAbonamente(() -> {
                                                    MockDataGenerator.incarcaVenituri(new MockDataGenerator.OnDateIncarcate() {
                                                        @Override
                                                        public void onIncarcate() {
                                                            progressBarSignIn.setVisibility(View.GONE);

                                                            boolean onboardingCompletat = getSharedPreferences("finmind_prefs", MODE_PRIVATE)
                                                                    .getBoolean("onboarding_completat_" + userId, false);

                                                            if (onboardingCompletat) {
                                                                startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                                                            } else {
                                                                startActivity(new Intent(SignInActivity.this, OnboardingActivity.class));
                                                            }
                                                            finish();
                                                        }
                                                    });
                                                });
                                            });
                                        });
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    progressBarSignIn.setVisibility(View.GONE);
                                    boolean onboardingCompletat = getSharedPreferences("finmind_prefs", MODE_PRIVATE)
                                            .getBoolean("onboarding_completat_" + userId, false);
                                    if (onboardingCompletat) {
                                        startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                                    } else {
                                        startActivity(new Intent(SignInActivity.this, OnboardingActivity.class));
                                    }
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBarSignIn.setVisibility(View.GONE);
                        btnSignIn.setEnabled(true);
                        tvEroareSignIn.setText(mesajEroare(e.getMessage()));
                        tvEroareSignIn.setVisibility(View.VISIBLE);
                    });
        });

        btnQuickAuth.setOnClickListener(v -> {
            String lastUserId = getSharedPreferences("finmind_prefs", MODE_PRIVATE)
                    .getString("last_user_id", "");
            boolean biometricEnabled = getSharedPreferences("finmind_prefs", MODE_PRIVATE)
                    .getBoolean("biometric_enabled_" + lastUserId, false);

            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricEnabled &&
                    biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                            == BiometricManager.BIOMETRIC_SUCCESS) {
                showBiometricPrompt();
            } else {
                startActivity(new Intent(this, SignInPinActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Funcționalitate disponibilă în curând!", Toast.LENGTH_SHORT).show());
    }

    private String mesajEroare(String firebaseMessage) {
        if (firebaseMessage == null) return "A apărut o eroare. Încearcă din nou.";
        if (firebaseMessage.contains("no user record") ||
                firebaseMessage.contains("user-not-found")) {
            return "Nu există un cont cu acest email.";
        } else if (firebaseMessage.contains("wrong-password") ||
                firebaseMessage.contains("invalid-credential")) {
            return "Email sau parolă incorectă.";
        } else if (firebaseMessage.contains("network")) {
            return "Eroare de rețea. Verifică conexiunea.";
        } else if (firebaseMessage.contains("too-many-requests")) {
            return "Prea multe încercări. Încearcă mai târziu.";
        } else {
            return "A apărut o eroare. Încearcă din nou.";
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        String lastUserId = getSharedPreferences("finmind_prefs", MODE_PRIVATE)
                                .getString("last_user_id", "");

                        if (lastUserId.isEmpty()) {
                            Toast.makeText(SignInActivity.this,
                                    "Autentifică-te mai întâi cu email și parolă.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        progressBarSignIn.setVisibility(View.VISIBLE);
                        btnSignIn.setEnabled(false);

                        FirestoreManager.reset();
                        ContBancarRepository.reset();
                        TranzactieRepository.reset();
                        AbonamentRepository.reset();
                        FirestoreManager.getInstance();

                        DemoDataGenerator.verificaSiInitializeaza("", "", () -> {
                            ContBancarRepository.getInstance().incarcaConturi(() -> {
                                TranzactieRepository.getInstance().incarcaTranzactii(() -> {
                                    AbonamentRepository.getInstance().incarcaAbonamente(() -> {
                                        MockDataGenerator.incarcaVenituri(new MockDataGenerator.OnDateIncarcate() {
                                            @Override
                                            public void onIncarcate() {
                                                progressBarSignIn.setVisibility(View.GONE);
                                                boolean onboardingCompletat = getSharedPreferences("finmind_prefs", MODE_PRIVATE)
                                                        .getBoolean("onboarding_completat_" + lastUserId, false);
                                                if (onboardingCompletat) {
                                                    startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                                                } else {
                                                    startActivity(new Intent(SignInActivity.this, OnboardingActivity.class));
                                                }
                                                finish();
                                            }
                                        });
                                    });
                                });
                            });
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(SignInActivity.this,
                                "Autentificare eșuată. Încearcă din nou.",
                                Toast.LENGTH_SHORT).show();
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