package com.stefi.licentamultibankingapp.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

public class FormularEconomiiActivity extends AppCompatActivity {

    private String iconitaSelectata = "✈";
    private String bancaSelectata = "";
    private boolean metodaManual = true;

    private TextInputEditText etNumeCont, etSumaInitiala, etObiectiv, etDataTinta, etSumaAutomata;
    private TextView tvDobanda, tvCastigEstimat, tvBancaSelectata;
    private SwitchMaterial switchRoundUp, switchSwearJar, switchNoSpend, switchSmartSave;
    private SwitchMaterial switchBlocare, switchReminder;

    private String[] banci = {"BCR", "ING", "BRD", "Raiffeisen"};
    private double[] dobanziBanci = {4.0, 3.5, 3.8, 4.2};
    private int bancaIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formular_economii);

        initViews();
        setupIconite();
        setupBanca();
        setupMetodaDepunere();
        setupDobandaCalculata();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnCreeazaCont).setOnClickListener(v -> creeazaCont());
        findViewById(R.id.btnAnuleaza).setOnClickListener(v -> finish());
    }

    private void initViews() {
        etNumeCont = findViewById(R.id.etNumeCont);
        etSumaInitiala = findViewById(R.id.etSumaInitiala);
        etObiectiv = findViewById(R.id.etObiectiv);
        etDataTinta = findViewById(R.id.etDataTinta);
        etSumaAutomata = findViewById(R.id.etSumaAutomata);
        tvDobanda = findViewById(R.id.tvDobanda);
        tvCastigEstimat = findViewById(R.id.tvCastigEstimat);
        tvBancaSelectata = findViewById(R.id.tvBancaSelectata);
        switchRoundUp = findViewById(R.id.switchRoundUp);
        switchSwearJar = findViewById(R.id.switchSwearJar);
        switchNoSpend = findViewById(R.id.switchNoSpend);
        switchSmartSave = findViewById(R.id.switchSmartSave);
        switchBlocare = findViewById(R.id.switchBlocare);
        switchReminder = findViewById(R.id.switchReminder);
    }

    private void setupIconite() {
        int[] ids = {R.id.iconita1, R.id.iconita2, R.id.iconita3, R.id.iconita4, R.id.iconita5};
        String[] iconite = {"✈", "🏠", "🚗", "💍", "📱"};

        for (int i = 0; i < ids.length; i++) {
            final String iconita = iconite[i];
            final int id = ids[i];
            findViewById(id).setOnClickListener(v -> {
                iconitaSelectata = iconita;
                updateSelectieIconita(ids, id);
            });
        }
        updateSelectieIconita(ids, ids[0]);
    }

    private void updateSelectieIconita(int[] ids, int selectedId) {
        for (int id : ids) {
            findViewById(id).setBackgroundColor(
                    id == selectedId ? 0xFF1B5E20 : 0xFF1A2E4A);
        }
    }

    private void setupBanca() {
        findViewById(R.id.spinnerBanca).setOnClickListener(v -> {
            bancaIndex = (bancaIndex + 1) % banci.length;
            bancaSelectata = banci[bancaIndex];
            tvBancaSelectata.setText(bancaSelectata);
            tvBancaSelectata.setTextColor(0xFFFFFFFF);
            tvDobanda.setText(String.format("%.1f%%", dobanziBanci[bancaIndex]));
            calculeazaCastig();
        });
    }

    private void setupMetodaDepunere() {
        LinearLayout optiuneManual = findViewById(R.id.optiuneManual);
        LinearLayout optiuneAutomat = findViewById(R.id.optiuneAutomat);
        View tilSumaAutomata = findViewById(R.id.tilSumaAutomata);

        optiuneManual.setOnClickListener(v -> {
            metodaManual = true;
            optiuneManual.setBackgroundColor(0xFF1A3C6E);
            optiuneAutomat.setBackgroundColor(0xFF1A2E4A);
            tilSumaAutomata.setVisibility(View.GONE);
        });

        optiuneAutomat.setOnClickListener(v -> {
            metodaManual = false;
            optiuneAutomat.setBackgroundColor(0xFF1A3C6E);
            optiuneManual.setBackgroundColor(0xFF1A2E4A);
            tilSumaAutomata.setVisibility(View.VISIBLE);
        });

        optiuneManual.setBackgroundColor(0xFF1A3C6E);
    }

    private void setupDobandaCalculata() {
        etSumaInitiala.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculeazaCastig(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void calculeazaCastig() {
        try {
            String sumaStr = etSumaInitiala.getText().toString();
            if (!sumaStr.isEmpty() && bancaIndex >= 0) {
                double suma = Double.parseDouble(sumaStr);
                double castig = suma * dobanziBanci[bancaIndex] / 100;
                tvCastigEstimat.setText(String.format("+%.2f RON", castig));
            }
        } catch (Exception e) {
            tvCastigEstimat.setText("+0.00 RON");
        }
    }

    private void creeazaCont() {
        String nume = etNumeCont.getText().toString().trim();
        String sumaStr = etSumaInitiala.getText().toString().trim();
        String obiectivStr = etObiectiv.getText().toString().trim();
        String dataTinta = etDataTinta.getText().toString().trim();

        if (nume.isEmpty()) {
            etNumeCont.setError("Introdu un nume pentru cont!");
            return;
        }
        if (sumaStr.isEmpty()) {
            etSumaInitiala.setError("Introdu suma initiala!");
            return;
        }
        if (bancaSelectata.isEmpty()) {
            Toast.makeText(this, "Selecteaza o banca!", Toast.LENGTH_SHORT).show();
            return;
        }

        double suma = Double.parseDouble(sumaStr);
        double obiectiv = obiectivStr.isEmpty() ? 0 : Double.parseDouble(obiectivStr);
        double dobanda = dobanziBanci[bancaIndex];

        ContBancar contNou = new ContBancar(
                bancaSelectata,
                "RO49" + bancaSelectata.toUpperCase() + System.currentTimeMillis(),
                suma, "RON", "#1B5E20",
                "Ioana Stefania", "Economii",
                ContBancar.TipCont.ECONOMII,
                nume, iconitaSelectata,
                obiectiv, dobanda, dataTinta
        );

        ContBancarRepository.getInstance().adaugaCont(contNou,null);
        Toast.makeText(this, "Cont " + nume + " creat!", Toast.LENGTH_SHORT).show();
        finish();
    }
}