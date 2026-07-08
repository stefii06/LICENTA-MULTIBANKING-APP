package com.stefi.licentamultibankingapp.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.stefi.licentamultibankingapp.R;

import java.util.concurrent.Executor;

public class SignInPinActivity extends AppCompatActivity {

    private StringBuilder pinInput = new StringBuilder();
    private View[] dots;
    private TextView tvUsePassword, tvUseBiometric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_pin);

        dots = new View[]{
                findViewById(R.id.dot1), findViewById(R.id.dot2),
                findViewById(R.id.dot3), findViewById(R.id.dot4),
                findViewById(R.id.dot5), findViewById(R.id.dot6)
        };

        tvUsePassword = findViewById(R.id.tvUsePassword);
        tvUseBiometric = findViewById(R.id.tvUseBiometric);

        int[] btnIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9};

        for (int id : btnIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                if (pinInput.length() < 6) {
                    pinInput.append(((Button) v).getText());
                    updateDots();
                    if (pinInput.length() == 6) {
                        verifyPin();
                    }
                }
            });
        }

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (pinInput.length() > 0) {
                pinInput.deleteCharAt(pinInput.length() - 1);
                updateDots();
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            pinInput.setLength(0);
            updateDots();
        });

        tvUsePassword.setOnClickListener(v -> finish());

        // FIX 1: biometrie reala in loc de Toast
        tvUseBiometric.setOnClickListener(v -> {
            BiometricManager bm = BiometricManager.from(this);
            int autentificatoriAcceptati = BiometricManager.Authenticators.BIOMETRIC_STRONG
                    | BiometricManager.Authenticators.BIOMETRIC_WEAK;
            int cod = bm.canAuthenticate(autentificatoriAcceptati);
            if (cod == BiometricManager.BIOMETRIC_SUCCESS) {
                showBiometricPrompt();
            } else {
                Toast.makeText(this, "Biometria nu este disponibilă. Cod: " + cod,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundColor(i < pinInput.length() ?
                    0xFF1A3C6E : 0xFFCCCCCC);
        }
    }

    // FIX 2: verifyPin simplu — direct Dashboard
    private void verifyPin() {
        android.content.SharedPreferences prefs = getSharedPreferences("finmind_prefs", MODE_PRIVATE);

        String lastUserId = prefs.getString("last_user_id", "");
        String pinSalvat = prefs.getString("user_pin_" + lastUserId, "");

        if (pinSalvat.isEmpty()) {
            Toast.makeText(this, "Nu ai setat un PIN. Folosește parola.", Toast.LENGTH_SHORT).show();
            pinInput.setLength(0);
            updateDots();
            return;
        }

        if (pinInput.toString().equals(pinSalvat)) {
            // Incarcam datele din Firestore inainte sa deschidem Dashboard-ul
            com.stefi.licentamultibankingapp.utils.FirestoreManager.reset();
            com.stefi.licentamultibankingapp.model.ContBancarRepository.reset();
            com.stefi.licentamultibankingapp.model.TranzactieRepository.reset();
            com.stefi.licentamultibankingapp.model.AbonamentRepository.reset();
            com.stefi.licentamultibankingapp.utils.FirestoreManager.getInstance();

            com.stefi.licentamultibankingapp.utils.DemoDataGenerator.verificaSiInitializeaza("", "", () -> {
                com.stefi.licentamultibankingapp.model.ContBancarRepository.getInstance().incarcaConturi(() -> {
                    com.stefi.licentamultibankingapp.model.TranzactieRepository.getInstance().incarcaTranzactii(() -> {
                        com.stefi.licentamultibankingapp.model.AbonamentRepository.getInstance().incarcaAbonamente(() -> {
                            com.stefi.licentamultibankingapp.model.MockDataGenerator.incarcaVenituri(new com.stefi.licentamultibankingapp.model.MockDataGenerator.OnDateIncarcate() {
                                @Override
                                public void onIncarcate() {
                                    android.content.Intent intent = new android.content.Intent(
                                            SignInPinActivity.this,
                                            com.stefi.licentamultibankingapp.ui.dashboard.DashboardActivity.class);
                                    intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                                            android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        });
                    });
                });
            });
        } else {
            Toast.makeText(this, "PIN incorect. Încearcă din nou.", Toast.LENGTH_SHORT).show();
            pinInput.setLength(0);
            updateDots();
        }
    }

    // FIX 3: biometrie reala
    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt prompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        // Incarcam datele din Firestore inainte sa deschidem Dashboard-ul,
                        // la fel ca la verificarea prin PIN
                        com.stefi.licentamultibankingapp.utils.FirestoreManager.reset();
                        com.stefi.licentamultibankingapp.model.ContBancarRepository.reset();
                        com.stefi.licentamultibankingapp.model.TranzactieRepository.reset();
                        com.stefi.licentamultibankingapp.model.AbonamentRepository.reset();
                        com.stefi.licentamultibankingapp.utils.FirestoreManager.getInstance();

                        com.stefi.licentamultibankingapp.utils.DemoDataGenerator.verificaSiInitializeaza("", "", () -> {
                            com.stefi.licentamultibankingapp.model.ContBancarRepository.getInstance().incarcaConturi(() -> {
                                com.stefi.licentamultibankingapp.model.TranzactieRepository.getInstance().incarcaTranzactii(() -> {
                                    com.stefi.licentamultibankingapp.model.AbonamentRepository.getInstance().incarcaAbonamente(() -> {
                                        com.stefi.licentamultibankingapp.model.MockDataGenerator.incarcaVenituri(new com.stefi.licentamultibankingapp.model.MockDataGenerator.OnDateIncarcate() {
                                            @Override
                                            public void onIncarcate() {
                                                android.content.Intent intent = new android.content.Intent(
                                                        SignInPinActivity.this,
                                                        com.stefi.licentamultibankingapp.ui.dashboard.DashboardActivity.class);
                                                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK |
                                                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
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
                        Toast.makeText(SignInPinActivity.this,
                                "Autentificare eșuată.", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autentificare biometrică")
                .setSubtitle("Folosește amprenta pentru a te conecta")
                .setNegativeButtonText("Anulează")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .build();

        prompt.authenticate(info);
    }
}