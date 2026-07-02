package com.stefi.licentamultibankingapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.ContBancar;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;

public class DetaliiContEconomiiActivity extends AppCompatActivity {

    private ContBancar cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_cont_economii);

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
        TextView tvTitlu = findViewById(R.id.tvTitluCont);
        TextView tvIconita = findViewById(R.id.tvIconitaCont);
        TextView tvNume = findViewById(R.id.tvNumeCont);
        TextView tvInfo = findViewById(R.id.tvInfoCont);
        TextView tvSold = findViewById(R.id.tvSoldCont);
        TextView tvObiectiv = findViewById(R.id.tvObiectivCont);
        TextView tvProcent = findViewById(R.id.tvProcent);
        ProgressBar progress = findViewById(R.id.progressObiectiv);
        TextView tvSoldStrans = findViewById(R.id.tvSoldStrans);
        TextView tvSoldRamas = findViewById(R.id.tvSoldRamas);
        TextView tvEstimare = findViewById(R.id.tvEstimareData);
        TextView tvDepusTotal = findViewById(R.id.tvDepusTotal);
        TextView tvDobanda = findViewById(R.id.tvDobandaCastigata);
        TextView tvZile = findViewById(R.id.tvZileRamase);
        TextView tvDepunereSuma = findViewById(R.id.tvDepunereSuma);
        TextView tvMetoda = findViewById(R.id.tvMetodaActiva);

        tvTitlu.setText(cont.getNumeCont());
        tvIconita.setText(cont.getIconita());
        tvNume.setText(cont.getNumeCont());
        tvInfo.setText(cont.getNumeBanca() + " • " + cont.getDobanda() + "% • Manual");
        tvSold.setText(String.format("%.2f RON", cont.getSold()));
        tvDepunereSuma.setText(String.format("+%.2f RON", cont.getSold()));
        tvMetoda.setText("Metoda: Manual");

        if (cont.getObiectiv() > 0) {
            tvObiectiv.setText("din " + String.format("%.2f", cont.getObiectiv()) + " RON obiectiv");
            int procent = (int) (cont.getSold() / cont.getObiectiv() * 100);
            tvProcent.setText(procent + "%");
            progress.setProgress(procent);
            tvSoldStrans.setText(String.format("%.0f RON stransi", cont.getSold()));
            tvSoldRamas.setText(String.format("%.0f RON ramasi", cont.getObiectiv() - cont.getSold()));
        }

        tvEstimare.setText(cont.getDataTinta() != null ? cont.getDataTinta() : "-");
        tvDepusTotal.setText(String.format("%.0f RON", cont.getSold()));

        double dobandaCastigata = cont.getSold() * cont.getDobanda() / 100 / 12;
        tvDobanda.setText(String.format("+%.2f RON", dobandaCastigata));
        tvZile.setText("42");
    }

    private void setupButoane() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAdaugaBani).setOnClickListener(v ->
                Toast.makeText(this, "Adauga bani - coming soon!", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnRetrage).setOnClickListener(v ->
                Toast.makeText(this, "Retrage - coming soon!", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnEditeaza).setOnClickListener(v -> {
            Intent intent = new Intent(this, FormularEconomiiActivity.class);
            intent.putExtra("iban", cont.getIban());
            intent.putExtra("modEditare", true);
            startActivity(intent);
        });

        findViewById(R.id.btnSchimbaMetoda).setOnClickListener(v ->
                Toast.makeText(this, "Schimba metoda - coming soon!", Toast.LENGTH_SHORT).show());
    }
}