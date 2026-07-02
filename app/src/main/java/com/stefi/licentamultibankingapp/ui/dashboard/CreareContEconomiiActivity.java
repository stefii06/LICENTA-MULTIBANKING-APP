package com.stefi.licentamultibankingapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stefi.licentamultibankingapp.R;

public class CreareContEconomiiActivity extends AppCompatActivity {

    private boolean singurSelectat = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creare_cont_economii);

        LinearLayout optiuneSingur = findViewById(R.id.optiuneSingur);
        LinearLayout optiunePartener = findViewById(R.id.optiunePartener);
        Button btnContinua = findViewById(R.id.btnContinuaTipCont);

        updateSelectie(optiuneSingur, optiunePartener);

        optiuneSingur.setOnClickListener(v -> {
            singurSelectat = true;
            updateSelectie(optiuneSingur, optiunePartener);
        });

        optiunePartener.setOnClickListener(v ->
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show());

        btnContinua.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormularEconomiiActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void updateSelectie(LinearLayout singur, LinearLayout partener) {
        singur.setBackgroundColor(singurSelectat ? 0xFF1A3C6E : 0xFF1A2E4A);
        partener.setBackgroundColor(0xFF1A2E4A);
    }
}