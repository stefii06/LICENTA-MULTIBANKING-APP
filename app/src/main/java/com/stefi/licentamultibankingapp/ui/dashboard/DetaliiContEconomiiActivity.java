package com.stefi.licentamultibankingapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        TextView tvBadgeOrigine = findViewById(R.id.tvBadgeOrigine);
        TextView tvSold = findViewById(R.id.tvSoldCont);
        TextView tvObiectiv = findViewById(R.id.tvObiectivCont);
        TextView tvProcent = findViewById(R.id.tvProcent);
        ProgressBar progress = findViewById(R.id.progressObiectiv);
        TextView tvSoldStrans = findViewById(R.id.tvSoldStrans);
        TextView tvSoldRamas = findViewById(R.id.tvSoldRamas);
        TextView tvEstimare = findViewById(R.id.tvEstimareData);
        TextView tvDepusTotal = findViewById(R.id.tvDepusTotal);
        TextView tvDobanda = findViewById(R.id.tvDobandaCastigata);
        LinearLayout layoutSavingsHacks = findViewById(R.id.layoutSavingsHacks);

        tvTitlu.setText(cont.getNumeCont());
        tvIconita.setText(cont.getIconita());
        tvNume.setText(cont.getNumeCont());
        tvInfo.setText(cont.getNumeBanca() + " • " + cont.getDobanda() + "%");
        tvSold.setText(String.format("%.2f RON", cont.getSold()));

        if (cont.isCreatDeUtilizator()) {
            tvBadgeOrigine.setText("Obiectiv de economisire · nu e cont bancar real");
            layoutSavingsHacks.setVisibility(View.VISIBLE);
        } else {
            tvBadgeOrigine.setText("Cont importat de la " + cont.getNumeBanca());
            layoutSavingsHacks.setVisibility(View.GONE);
        }

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
    }

    private void setupButoane() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnEditeaza).setOnClickListener(v -> {
            if (cont.isCreatDeUtilizator()) {
                Intent intent = new Intent(this, FormularEconomiiActivity.class);
                intent.putExtra("iban", cont.getIban());
                intent.putExtra("modEditare", true);
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "In aplicatia reala, acest buton te-ar redirectiona catre aplicatia " + cont.getNumeBanca(),
                        Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btnStergeCont).setOnClickListener(v ->
                ContBancarRepository.getInstance().stergeContDinFirestore(cont.getId(), () -> {
                    Toast.makeText(this, "Cont sters", Toast.LENGTH_SHORT).show();
                    finish();
                }));
    }
}