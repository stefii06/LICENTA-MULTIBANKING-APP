package com.stefi.licentamultibankingapp.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.MockDataGenerator;
import com.stefi.licentamultibankingapp.repository.FlaskApiClient;

import java.util.ArrayList;
import java.util.List;

public class PredictiiActivity extends AppCompatActivity {

    private TextView tab1Luna, tab3Luni, tab1An;
    private TextView tvLunaPredictie, tvTotalPredictie, tvTrendPredictie, tvSubtitluPredictie;
    private TextView tvPredMancare, tvPredTransport, tvPredShopping, tvPredDivertisment;
    private ProgressBar progressPredMancare, progressPredTransport,
            progressPredShopping, progressPredDivertisment;
    private TextView tvInsightRecomandare, tvInsightAtentie, tvInsightProgres;
    private LineChart lineChartPredictii;
    private ProgressBar progressBarLoading;
    private TextView tvEroare;

    private int tabSelectat = 1;
    private FlaskApiClient flaskApiClient;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictii);

        flaskApiClient = new FlaskApiClient();
        handler = new Handler(Looper.getMainLooper());

        initViews();
        setupTabs();
        setupBtnInapoi();
        actualizeazaPredictii(1);
    }

    private void initViews() {
        tab1Luna  = findViewById(R.id.tab1Luna);
        tab3Luni  = findViewById(R.id.tab3Luni);
        tab1An    = findViewById(R.id.tab1An);

        tvLunaPredictie     = findViewById(R.id.tvLunaPredictie);
        tvTotalPredictie    = findViewById(R.id.tvTotalPredictie);
        tvTrendPredictie    = findViewById(R.id.tvTrendPredictie);
        tvSubtitluPredictie = findViewById(R.id.tvSubtitluPredictie);

        tvPredMancare      = findViewById(R.id.tvPredMancare);
        tvPredTransport    = findViewById(R.id.tvPredTransport);
        tvPredShopping     = findViewById(R.id.tvPredShopping);
        tvPredDivertisment = findViewById(R.id.tvPredDivertisment);

        progressPredMancare      = findViewById(R.id.progressPredMancare);
        progressPredTransport    = findViewById(R.id.progressPredTransport);
        progressPredShopping     = findViewById(R.id.progressPredShopping);
        progressPredDivertisment = findViewById(R.id.progressPredDivertisment);

        tvInsightRecomandare = findViewById(R.id.tvInsightRecomandare);
        tvInsightAtentie     = findViewById(R.id.tvInsightAtentie);
        tvInsightProgres     = findViewById(R.id.tvInsightProgres);

        lineChartPredictii = findViewById(R.id.lineChartPredictii);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        tvEroare           = findViewById(R.id.tvEroare);
    }

    private void setupBtnInapoi() {
        findViewById(R.id.btnInapoi).setOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tab1Luna.setOnClickListener(v -> {
            tabSelectat = 1;
            actualizeazaStyleTab();
            actualizeazaPredictii(1);
        });
        tab3Luni.setOnClickListener(v -> {
            tabSelectat = 3;
            actualizeazaStyleTab();
            actualizeazaPredictii(3);
        });
        tab1An.setOnClickListener(v -> {
            tabSelectat = 12;
            actualizeazaStyleTab();
            actualizeazaPredictii(12);
        });
    }

    private void actualizeazaStyleTab() {
        tab1Luna.setBackgroundColor(tabSelectat == 1 ?
                Color.parseColor("#4A90D9") : Color.parseColor("#1A2F50"));
        tab1Luna.setTextColor(tabSelectat == 1 ?
                Color.WHITE : Color.parseColor("#7A9CC0"));

        tab3Luni.setBackgroundColor(tabSelectat == 3 ?
                Color.parseColor("#4A90D9") : Color.parseColor("#1A2F50"));
        tab3Luni.setTextColor(tabSelectat == 3 ?
                Color.WHITE : Color.parseColor("#7A9CC0"));

        tab1An.setBackgroundColor(tabSelectat == 12 ?
                Color.parseColor("#4A90D9") : Color.parseColor("#1A2F50"));
        tab1An.setTextColor(tabSelectat == 12 ?
                Color.WHITE : Color.parseColor("#7A9CC0"));
    }

    private void actualizeazaPredictii(int luni) {
        float[] cheltuieli = MockDataGenerator.getTotalPerLuna();
        float[] categorii  = MockDataGenerator.getCheltuieliPerCategorie(0);

        // Setam textul perioadei inainte de request
        String perioadaText = luni == 1 ? "luna viitoare" :
                luni == 3 ? "3 luni" : "anul viitor";
        tvLunaPredictie.setText("Estimare — " + perioadaText);

        aratLoading();

        flaskApiClient.trimiteRequest(cheltuieli, luni, handler, new FlaskApiClient.OnRaspuns() {

            @Override
            public void onSucces(float predictieTotal, float cheltuieliCurente,
                                 float diferenta, float procentTrend) {
                ascundeLoading();
                actualizeazaUI(cheltuieli, categorii, predictieTotal,
                        cheltuieliCurente, diferenta, procentTrend);
            }

            @Override
            public void onEroare(String mesaj) {
                ascundeLoading();
                aratEroare(mesaj);
            }
        });
    }

    // Actualizeaza tot UI-ul cu datele primite de la server
    private void actualizeazaUI(float[] cheltuieli, float[] categorii,
                                float predictieTotal, float cheltuieliCurente,
                                float diferenta, float procentTrend) {
        // Header
        tvTotalPredictie.setText("~" + (int) predictieTotal + " RON");

        if (diferenta > 0) {
            tvTrendPredictie.setText("↑ +" + (int) procentTrend + "%");
            tvTrendPredictie.setTextColor(Color.parseColor("#E57373"));
        } else {
            tvTrendPredictie.setText("↓ " + (int) procentTrend + "%");
            tvTrendPredictie.setTextColor(Color.parseColor("#4CAF50"));
        }
        tvSubtitluPredictie.setText("față de luna curentă");

        // Categorii — aplicam acelasi factor de trend per categorie
        float factorTrend = cheltuieliCurente > 0 ? predictieTotal / cheltuieliCurente : 1f;

        int predMancare      = (int)(categorii[0] * factorTrend);
        int predTransport    = (int)(categorii[1] * factorTrend);
        int predShopping     = (int)(categorii[2] * factorTrend);
        int predDivertisment = (int)(categorii[3] * factorTrend);
        int maxCategorie     = Math.max(Math.max(predMancare, predTransport),
                Math.max(predShopping, predDivertisment));

        tvPredMancare.setText("~" + predMancare + " RON");
        tvPredTransport.setText("~" + predTransport + " RON");
        tvPredShopping.setText("~" + predShopping + " RON");
        tvPredDivertisment.setText("~" + predDivertisment + " RON");

        if (maxCategorie > 0) {
            progressPredMancare.setProgress((int)((predMancare / (float) maxCategorie) * 100));
            progressPredTransport.setProgress((int)((predTransport / (float) maxCategorie) * 100));
            progressPredShopping.setProgress((int)((predShopping / (float) maxCategorie) * 100));
            progressPredDivertisment.setProgress((int)((predDivertisment / (float) maxCategorie) * 100));
        }

        actualizeazaGraficTrend(cheltuieli, predictieTotal);
        genereazaInsights(cheltuieli, categorii, predictieTotal);
    }

    private void aratLoading() {
        progressBarLoading.setVisibility(View.VISIBLE);
        tvEroare.setVisibility(View.GONE);
    }

    private void ascundeLoading() {
        progressBarLoading.setVisibility(View.GONE);
    }

    private void aratEroare(String mesaj) {
        tvEroare.setText(mesaj);
        tvEroare.setVisibility(View.VISIBLE);
    }

    // Neatins — exact ca inainte
    private void actualizeazaGraficTrend(float[] istorice, float predictie) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < istorice.length; i++) {
            entries.add(new Entry(i, istorice[i]));
        }
        entries.add(new Entry(istorice.length, predictie));

        LineDataSet dsIstorice = new LineDataSet(
                entries.subList(0, istorice.length), "Istorice");
        dsIstorice.setColor(Color.parseColor("#4A90D9"));
        dsIstorice.setLineWidth(2f);
        dsIstorice.setCircleColor(Color.parseColor("#4A90D9"));
        dsIstorice.setCircleRadius(4f);
        dsIstorice.setDrawValues(false);
        dsIstorice.setDrawFilled(true);
        dsIstorice.setFillColor(Color.parseColor("#4A90D9"));
        dsIstorice.setFillAlpha(30);
        dsIstorice.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        List<Entry> predEntries = new ArrayList<>();
        predEntries.add(new Entry(istorice.length - 1, istorice[istorice.length - 1]));
        predEntries.add(new Entry(istorice.length, predictie));

        LineDataSet dsPredictie = new LineDataSet(predEntries, "Predicție");
        dsPredictie.setColor(Color.parseColor("#4A90D9"));
        dsPredictie.setLineWidth(2f);
        dsPredictie.enableDashedLine(10f, 5f, 0f);
        dsPredictie.setCircleColor(Color.parseColor("#4A90D9"));
        dsPredictie.setCircleRadius(5f);
        dsPredictie.setDrawValues(false);
        dsPredictie.setDrawFilled(false);

        lineChartPredictii.setData(new LineData(dsIstorice, dsPredictie));
        lineChartPredictii.getDescription().setEnabled(false);
        lineChartPredictii.getLegend().setEnabled(false);
        lineChartPredictii.setTouchEnabled(false);
        lineChartPredictii.setBackgroundColor(Color.parseColor("#1A2F50"));

        String[] etichete = {"Ian", "Feb", "Mar", "Apr", "Mai", "Iun", "→"};
        XAxis xAxis = lineChartPredictii.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etichete));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#7A9CC0"));
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        lineChartPredictii.getAxisLeft().setTextColor(Color.parseColor("#7A9CC0"));
        lineChartPredictii.getAxisLeft().setDrawGridLines(false);
        lineChartPredictii.getAxisRight().setEnabled(false);

        lineChartPredictii.animateX(600);
        lineChartPredictii.invalidate();
    }

    // Neatins — exact ca inainte
    private void genereazaInsights(float[] cheltuieli, float[] categorii, float predictie) {
        float maxCat = 0;
        int maxIdx = 0;
        String[] numeCategorii = {"Mâncare", "Transport", "Shopping", "Divertisment"};
        for (int i = 0; i < 4; i++) {
            if (categorii[i] > maxCat) { maxCat = categorii[i]; maxIdx = i; }
        }
        int economiePos = (int)(maxCat * 0.20f);
        tvInsightRecomandare.setText("Reducând " + numeCategorii[maxIdx] +
                " cu 20% economisești ~" + economiePos + " RON/lună");

        float trend = cheltuieli[5] - cheltuieli[4];
        if (trend > 0) {
            tvInsightAtentie.setText("Cheltuielile au crescut cu " +
                    (int) trend + " RON față de luna trecută");
        } else {
            tvInsightAtentie.setText("Cheltuielile sunt în scădere față de luna trecută — bine!");
        }

        float[] economii = MockDataGenerator.getEconomiiPerLuna();
        float trendEconomii = economii[5] - economii[4];
        if (trendEconomii > 0) {
            tvInsightProgres.setText("Economiile tale au crescut cu " +
                    (int) trendEconomii + " RON față de luna trecută — continuă!");
        } else {
            tvInsightProgres.setText("Încearcă să economisești mai mult luna viitoare!");
        }
    }
}