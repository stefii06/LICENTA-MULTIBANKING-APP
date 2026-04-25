package com.stefi.licentamultibankingapp.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stefi.licentamultibankingapp.R;

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

        tvUseBiometric.setOnClickListener(v -> {
            Toast.makeText(this, "Amprentă disponibilă după integrarea Firebase!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundColor(i < pinInput.length() ?
                    0xFF1A3C6E : 0xFFCCCCCC);
        }
    }

    private void verifyPin() {
        // Verificarea reala se face dupa Firebase
        Toast.makeText(this, "PIN verificat! (Firebase necesar)", Toast.LENGTH_SHORT).show();
        pinInput.setLength(0);
        updateDots();
    }
}