package com.stefi.licentamultibankingapp.ui.login.signUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.ui.login.SignInActivity;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment5 extends Fragment {

    private Button btnGoToLogin;
    private ProgressBar progressBarSignUp;
    private TextView tvEroareSignUp;
    private TextView tvWelcomeName;

    // Instanta Firebase Auth
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        btnGoToLogin = view.findViewById(R.id.btnGoToLogin);
        progressBarSignUp = view.findViewById(R.id.progressBarSignUp);
        tvEroareSignUp = view.findViewById(R.id.tvEroareSignUp);
        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();

        // Preluam datele colectate din fragmentele anterioare
        String firstName = "";
        String lastName = "";
        String email = "";
        String password = "";

        if (getArguments() != null) {
            firstName = getArguments().getString("firstName", "");
            lastName = getArguments().getString("lastName", "");
            email = getArguments().getString("email", "");
            password = getArguments().getString("password", "");
            tvWelcomeName.setText("Bine ai venit, " + firstName + " " + lastName + "!");
        }

        // Variabile finale pentru lambda
        String emailFinal = email;
        String passwordFinal = password;
        final String firstNameFinal = firstName;
        final String lastNameFinal = lastName;
        final String phoneFinal = getArguments() != null ? getArguments().getString("phone", "") : "";

        btnGoToLogin.setOnClickListener(v -> {
            // Ascundem eroarea anterioara daca exista
            tvEroareSignUp.setVisibility(View.GONE);

            // Verificare de siguranta — nu ar trebui sa fie goale
            if (emailFinal.isEmpty() || passwordFinal.isEmpty()) {
                tvEroareSignUp.setText("Date lipsă. Încearcă din nou de la început.");
                tvEroareSignUp.setVisibility(View.VISIBLE);
                return;
            }

            // Aratam loading, dezactivam butonul
            progressBarSignUp.setVisibility(View.VISIBLE);
            btnGoToLogin.setEnabled(false);


            // Inregistrare cu Firebas
            mAuth.createUserWithEmailAndPassword(emailFinal, passwordFinal)
                    .addOnSuccessListener(authResult -> {
                        // Salvam datele personale in Firestore
                        String userId = authResult.getUser().getUid();

                        // Salvam PIN si biometrie per userId
                        String pin = getArguments() != null ? getArguments().getString("pin", "") : "";
                        boolean biometricEnabled = getArguments() != null && getArguments().getBoolean("biometricEnabled", false);

                        if (!pin.isEmpty()) {
                            requireActivity()
                                    .getSharedPreferences("finmind_prefs", android.content.Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("user_pin_" + userId, pin)
                                    .putString("user_pin", pin)
                                    .putBoolean("biometric_enabled_" + userId, biometricEnabled)
                                    .putBoolean("biometric_enabled", biometricEnabled)
                                    .apply();
                        }

                        Map<String, Object> datePersonale = new HashMap<>();
                        datePersonale.put("firstName", firstNameFinal);
                        datePersonale.put("lastName", lastNameFinal);
                        datePersonale.put("phone", phoneFinal);
                        datePersonale.put("email", emailFinal);

                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .set(datePersonale, com.google.firebase.firestore.SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    // Navigam la SignIn doar dupa ce s-au salvat datele
                                    progressBarSignUp.setVisibility(View.GONE);
                                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    // Salvarea a esuat, dar contul e creat — mergem oricum
                                    progressBarSignUp.setVisibility(View.GONE);
                                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBarSignUp.setVisibility(View.GONE);
                        btnGoToLogin.setEnabled(true);
                        tvEroareSignUp.setText(mesajEroare(e.getMessage()));
                        tvEroareSignUp.setVisibility(View.VISIBLE);
                    });
        });
    }

    // Traducem mesajele de eroare Firebase in romana
    private String mesajEroare(String mesajFirebase) {
        if (mesajFirebase == null) return "Eroare necunoscută. Încearcă din nou.";

        if (mesajFirebase.contains("email address is already in use")) {
            return "Acest email este deja înregistrat.";
        } else if (mesajFirebase.contains("network error")) {
            return "Eroare de rețea. Verifică conexiunea.";
        } else if (mesajFirebase.contains("badly formatted")) {
            return "Adresa de email nu este validă.";
        } else {
            return "Eroare la înregistrare. Încearcă din nou.";
        }
    }
}