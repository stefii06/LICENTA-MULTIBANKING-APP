package com.stefi.licentamultibankingapp.ui.dashboard;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetaliiDepozitActivity extends AppCompatActivity {

    private ContBancar cont;
    private boolean reinnoireActiva = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_depozit);

        String iban = getIntent().getStringExtra("iban");
        for (ContBancar c : ContBancarRepository.getInstance().getConturi()) {
            if (c.getIban().equals(iban)) {
                cont = c;
                break;
            }
        }

        if (cont == null) {
            finish();
            return;
        }

        setupUI();
        setupButoane();
    }

    private void setupUI() {
        TextView tvTitlu = findViewById(R.id.tvTitluDepozit);
        TextView tvIconita = findViewById(R.id.tvIconitaDepozit);
        TextView tvNume = findViewById(R.id.tvNumeDepozit);
        TextView tvInfo = findViewById(R.id.tvInfoDepozit);
        TextView tvSuma = findViewById(R.id.tvSumaDepozit);
        TextView tvDataScadenta = findViewById(R.id.tvDataScadentaDetalii);
        TextView tvDataDeschidere = findViewById(R.id.tvDataDeschidere);
        TextView tvDataDeschidereIstoric = findViewById(R.id.tvDataDeschidereIsoric);
        TextView tvZile = findViewById(R.id.tvZileDepozit);
        TextView tvSumaScadenta = findViewById(R.id.tvSumaScadentaDetalii);
        TextView tvSumaBlockata = findViewById(R.id.tvSumaBlockata);
        TextView tvCastig = findViewById(R.id.tvCastigTotal);
        TextView tvZileRamase = findViewById(R.id.tvZileRamaseDepozit);
        TextView tvSumaIstoric = findViewById(R.id.tvSumaIstoric);
        TextView tvDobandaIstoric = findViewById(R.id.tvDobandaIstoric);
        ProgressBar progress = findViewById(R.id.progressDepozitDetalii);

        tvTitlu.setText(cont.getNumeCont());
        tvIconita.setText(cont.getIconita());
        tvNume.setText(cont.getNumeCont());
        tvInfo.setText(cont.getNumeBanca() + " • " + cont.getDobanda() + "% dobanda anuala");
        tvSuma.setText(String.format("%.2f RON", cont.getSold()));

        String dataAzi = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDataDeschidere.setText(dataAzi);
        tvDataDeschidereIstoric.setText(dataAzi);
        tvDataScadenta.setText(cont.getDataTinta() != null ? cont.getDataTinta() : "-");

        double castig = cont.getSold() * cont.getDobanda() / 100;
        double sumaScadenta = cont.getSold() + castig;

        tvSumaScadenta.setText(String.format("%.2f RON", sumaScadenta));
        tvSumaBlockata.setText(String.format("%.0f RON", cont.getSold()));
        tvCastig.setText(String.format("+%.0f RON", castig));
        tvZile.setText("2 / 365 zile");
        tvZileRamase.setText("363");
        tvSumaIstoric.setText(String.format("%.0f RON", cont.getSold()));
        tvDobandaIstoric.setText(String.format("+%.0f RON", castig));
        progress.setProgress(2);
    }

    private void setupButoane() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView tvReinnoireStatus = findViewById(R.id.tvReinnoireStatus);
        TextView btnToggle = findViewById(R.id.btnToggleReinnoire);

        btnToggle.setOnClickListener(v -> {
            reinnoireActiva = !reinnoireActiva;
            tvReinnoireStatus.setText("Reinnoire automata: " +
                    (reinnoireActiva ? "Activata" : "Dezactivata"));
            tvReinnoireStatus.setTextColor(reinnoireActiva ? 0xFF4CAF50 : 0xFF7A9CC0);
            btnToggle.setText(reinnoireActiva ? "Dezactiveaza" : "Activeaza");
            Toast.makeText(this, reinnoireActiva ?
                            "Reinnoire automata activata!" : "Reinnoire automata dezactivata!",
                    Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnLichideaza).setOnClickListener(v -> {
            ContBancarRepository.getInstance().getConturi().remove(cont);
            Toast.makeText(this, "Depozit lichidat! Suma returnata in cont.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}