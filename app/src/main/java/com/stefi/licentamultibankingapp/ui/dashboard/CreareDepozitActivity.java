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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreareDepozitActivity extends AppCompatActivity {

    private String bancaSelectata = "";
    private int bancaIndex = 0;
    private int perioadaLuni = 12;
    private double dobandaSelectata = 6.0;

    private String[] banci = {"BCR", "ING", "BRD", "Raiffeisen"};
    private String[] culori = {"#E53935", "#FF6D00", "#1565C0", "#FFD600"};

    private int[] perioade = {3, 6, 12, 24};
    private double[] dobanzii = {3.5, 4.5, 6.0, 6.5};
    private int[] btnPerioadeIds = {R.id.btn3Luni, R.id.btn6Luni, R.id.btn1An, R.id.btn2Ani};

    private TextInputEditText etSuma;
    private TextView tvBancaSelectata, tvDataScadenta, tvSumaScadenta, tvCastig, tvDobanda;
    private TextView tvAvertisment;
    private SwitchMaterial switchReinnoire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creare_depozit);

        initViews();
        setupBanca();
        setupPerioade();
        setupCalculator();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDeschideDepozit).setOnClickListener(v -> deschideDepozit());
        findViewById(R.id.btnAnuleaza).setOnClickListener(v -> finish());
    }

    private void initViews() {
        etSuma = findViewById(R.id.etSumaDepozit);
        tvBancaSelectata = findViewById(R.id.tvBancaSelectataDepozit);
        tvDataScadenta = findViewById(R.id.tvDataScadenta);
        tvSumaScadenta = findViewById(R.id.tvSumaScadenta);
        tvCastig = findViewById(R.id.tvCastigDepozitNou);
        tvDobanda = findViewById(R.id.tvDobandaDepozit);
        tvAvertisment = findViewById(R.id.tvAvertismentDepozit);
        switchReinnoire = findViewById(R.id.switchReinnoireDepozit);
    }

    private void setupBanca() {
        bancaSelectata = banci[0];
        tvBancaSelectata.setText(bancaSelectata);
        tvBancaSelectata.setTextColor(0xFFFFFFFF);

        findViewById(R.id.spinnerBancaDepozit).setOnClickListener(v -> {
            bancaIndex = (bancaIndex + 1) % banci.length;
            bancaSelectata = banci[bancaIndex];
            tvBancaSelectata.setText(bancaSelectata);
            calculeaza();
        });
    }

    private void setupPerioade() {
        for (int i = 0; i < btnPerioadeIds.length; i++) {
            final int index = i;
            findViewById(btnPerioadeIds[i]).setOnClickListener(v -> {
                perioadaLuni = perioade[index];
                dobandaSelectata = dobanzii[index];
                updateSelectiePerioda(index);
                calculeaza();
            });
        }
        updateSelectiePerioda(2);
    }

    private void updateSelectiePerioda(int selectedIndex) {
        for (int i = 0; i < btnPerioadeIds.length; i++) {
            View btn = findViewById(btnPerioadeIds[i]);
            btn.setBackgroundColor(i == selectedIndex ? 0xFF0D47A1 : 0xFF1A2E4A);
        }
    }

    private void setupCalculator() {
        etSuma.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculeaza(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void calculeaza() {
        try {
            String sumaStr = etSuma.getText().toString();
            if (sumaStr.isEmpty()) return;

            double suma = Double.parseDouble(sumaStr);
            double castig = suma * dobandaSelectata / 100 * perioadaLuni / 12;
            double sumaFinala = suma + castig;

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, perioadaLuni);
            String dataScadenta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.getTime());

            tvDobanda.setText(String.format("%.1f%%", dobandaSelectata));
            tvDataScadenta.setText(dataScadenta);
            tvSumaScadenta.setText(String.format("%.2f RON", sumaFinala));
            tvCastig.setText(String.format("+%.2f RON castig", castig));
            tvAvertisment.setText("Banii vor fi blocati pana la " + dataScadenta +
                    ". Retragerea anticipata implica pierderea dobanzii acumulate.");

        } catch (Exception e) {
            tvSumaScadenta.setText("0.00 RON");
        }
    }

    private void deschideDepozit() {
        String sumaStr = etSuma.getText().toString().trim();

        if (bancaSelectata.isEmpty()) {
            Toast.makeText(this, "Selecteaza o banca!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sumaStr.isEmpty()) {
            etSuma.setError("Introdu suma depozitului!");
            return;
        }

        double suma = Double.parseDouble(sumaStr);
        double castig = suma * dobandaSelectata / 100 * perioadaLuni / 12;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, perioadaLuni);
        String dataScadenta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.getTime());

        String numeDepozit = bancaSelectata + " Depozit " + (perioadaLuni < 12 ?
                perioadaLuni + " luni" : (perioadaLuni / 12) + " an");

        ContBancar depozit = new ContBancar(
                bancaSelectata,
                "RO49DEP" + bancaSelectata.toUpperCase() + System.currentTimeMillis(),
                suma, "RON", "#0D47A1",
                "Ioana Stefania", "Depozit",
                ContBancar.TipCont.DEPOZIT,
                numeDepozit, "🏦",
                suma + castig, dobandaSelectata, dataScadenta, true
        );

        ContBancarRepository.getInstance().adaugaCont(depozit,null);
        Toast.makeText(this, "Depozit deschis cu succes!", Toast.LENGTH_SHORT).show();
        finish();
    }
}