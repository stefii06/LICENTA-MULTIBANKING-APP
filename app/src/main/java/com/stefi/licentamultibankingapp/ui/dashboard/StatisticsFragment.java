package com.stefi.licentamultibankingapp.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.model.MockDataGenerator;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    // Grafice
    private PieChart donutChart, pieChartScor;
    private LineChart lineChart;

    // Selector luna
    private TextView btnLunaStanga, btnLunaDreapta;
    private TextView tvLunaSelectata, tvLunaSubtitlu;

    // Titluri carduri — se actualizeaza cu luna
    private TextView tvScorLuna, tvTitluRezumat, tvTitluDistributie, tvTitluDetalii;

    // Scor
    private TextView tvScorLabel, tvScorDescriptie;
    private TextView tvFactorEconomii, tvFactorStabilitate, tvFactorBuget, tvFactorTrend;

    // Rezumat lunar
    private TextView tvVenituri, tvCheltuieli, tvEconomii;
    private TextView tvPlanSubtitlu, tvPlanStatus;
    private ProgressBar progressVenituri, progressCheltuieli, progressEconomii;

    // Legenda donut
    private LinearLayout layoutLegendaCategorii;

    // Line chart
    private Spinner spinnerLineChart;
    private TextView tvBadgeLineChart;

    // Detalii categorii
    private LinearLayout layoutDetaliiCategorii;

    // Collapse — bodies
    private LinearLayout bodyRezumat, bodyDistributie, bodyDetalii;

    // Collapse — arrows
    private TextView tvArrowRezumat, tvArrowDistributie, tvArrowTrend, tvArrowDetalii;

    // Collapse — preview texts
    private TextView tvPreviewTrend, tvPreviewDetalii;

    // Stare collapse
    private boolean rezumatDeschis = true;
    private boolean distributieDeschisa = true;
    private boolean trendDeschis = false;
    private boolean detaliiDeschise = false;

    // Navigare luna: 0 = luna curenta, -1 = luna trecuta, etc.
    // lunaOffset merge de la -5 (cea mai veche) la 0 (curenta)
    private int lunaOffset = 0;

    // Numele lunilor pentru afisare
    private static final String[] NUME_LUNI = {
            "Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie",
            "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"
    };

    private String tipLineChart = "Venituri";

    private static final String[] CATEGORII = {
            "Mâncare", "Transport", "Shopping",
            "Divertisment", "Sănătate", "Utilități"
    };
    private static final int[] CULORI_CATEGORII = {
            Color.parseColor("#4A90D9"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#FFB74D"),
            Color.parseColor("#E57373"),
            Color.parseColor("#7A9CC0")
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupSelectorLuna();
        setupCollapse();
        setupSpinnerLineChart();
        setupBtnPredictii(view);
        setupBtnAI(view);
        actualizeazaEcran();
    }

    // ----------------------------------------------------------------
    // INIT
    // ----------------------------------------------------------------

    private void initViews(View view) {
        donutChart   = view.findViewById(R.id.donutChart);
        pieChartScor = view.findViewById(R.id.pieChartScor);
        lineChart    = view.findViewById(R.id.lineChart);

        btnLunaStanga  = view.findViewById(R.id.btnLunaStanga);
        btnLunaDreapta = view.findViewById(R.id.btnLunaDreapta);
        tvLunaSelectata = view.findViewById(R.id.tvLunaSelectata);
        tvLunaSubtitlu  = view.findViewById(R.id.tvLunaSubtitlu);

        tvScorLuna         = view.findViewById(R.id.tvScorLuna);
        tvTitluRezumat     = view.findViewById(R.id.tvTitluRezumat);
        tvTitluDistributie = view.findViewById(R.id.tvTitluDistributie);
        tvTitluDetalii     = view.findViewById(R.id.tvTitluDetalii);

        tvScorLabel        = view.findViewById(R.id.tvScorLabel);
        tvScorDescriptie   = view.findViewById(R.id.tvScorDescriptie);
        tvFactorEconomii   = view.findViewById(R.id.tvFactorEconomii);
        tvFactorStabilitate = view.findViewById(R.id.tvFactorStabilitate);
        tvFactorBuget      = view.findViewById(R.id.tvFactorBuget);
        tvFactorTrend      = view.findViewById(R.id.tvFactorTrend);

        tvVenituri     = view.findViewById(R.id.tvVenituri);
        tvCheltuieli   = view.findViewById(R.id.tvCheltuieli);
        tvEconomii     = view.findViewById(R.id.tvEconomii);
        tvPlanSubtitlu = view.findViewById(R.id.tvPlanSubtitlu);
        tvPlanStatus   = view.findViewById(R.id.tvPlanStatus);

        progressVenituri   = view.findViewById(R.id.progressVenituri);
        progressCheltuieli = view.findViewById(R.id.progressCheltuieli);
        progressEconomii   = view.findViewById(R.id.progressEconomii);

        layoutLegendaCategorii = view.findViewById(R.id.layoutLegendaCategorii);
        layoutDetaliiCategorii = view.findViewById(R.id.layoutDetaliiCategorii);

        spinnerLineChart = view.findViewById(R.id.spinnerLineChart);
        tvBadgeLineChart = view.findViewById(R.id.tvBadgeLineChart);

        bodyRezumat     = view.findViewById(R.id.bodyRezumat);
        bodyDistributie = view.findViewById(R.id.bodyDistributie);
        bodyDetalii     = view.findViewById(R.id.bodyDetalii);

        tvArrowRezumat     = view.findViewById(R.id.tvArrowRezumat);
        tvArrowDistributie = view.findViewById(R.id.tvArrowDistributie);
        tvArrowTrend       = view.findViewById(R.id.tvArrowTrend);
        tvArrowDetalii     = view.findViewById(R.id.tvArrowDetalii);

        tvPreviewTrend   = view.findViewById(R.id.tvPreviewTrend);
        tvPreviewDetalii = view.findViewById(R.id.tvPreviewDetalii);
    }

    // ----------------------------------------------------------------
    // SELECTOR LUNA
    // ----------------------------------------------------------------

    private void setupSelectorLuna() {
        btnLunaStanga.setOnClickListener(v -> {
            // Mergem mai departe in trecut (offset scade, min -5)
            if (lunaOffset > -5) {
                lunaOffset--;
                actualizeazaEcran();
            }
        });

        btnLunaDreapta.setOnClickListener(v -> {
            // Mergem spre luna curenta (offset creste, max 0)
            if (lunaOffset < 0) {
                lunaOffset++;
                actualizeazaEcran();
            }
        });
    }

    // Returneaza numele scurt al lunii pentru luna selectata
    private String getNumeLunaScurt() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, lunaOffset);
        return NUME_LUNI[cal.get(java.util.Calendar.MONTH)].substring(0, 3);
    }

    // Returneaza "Luna An" pentru afisare in selector
    private String getNumeLunaLung() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, lunaOffset);
        return NUME_LUNI[cal.get(java.util.Calendar.MONTH)]
                + " " + cal.get(java.util.Calendar.YEAR);
    }

    // Actualizeaza vizual selectorul si sagetile
    private void actualizeazaSelectorLuna() {
        tvLunaSelectata.setText(getNumeLunaLung());

        if (lunaOffset == 0) {
            tvLunaSubtitlu.setText("luna curentă");
            tvLunaSubtitlu.setTextColor(Color.parseColor("#4CAF50"));
            btnLunaDreapta.setTextColor(Color.parseColor("#2A3F60"));
        } else {
            tvLunaSubtitlu.setText("istoric");
            tvLunaSubtitlu.setTextColor(Color.parseColor("#7A9CC0"));
            btnLunaDreapta.setTextColor(Color.parseColor("#4A90D9"));
        }

        btnLunaStanga.setTextColor(lunaOffset > -5 ?
                Color.parseColor("#4A90D9") : Color.parseColor("#2A3F60"));
    }

    // Actualizeaza titlurile cardurilor cu luna selectata
    private void actualizeazaTitluriCarduri() {
        String lunaScurta = getNumeLunaScurt();
        tvScorLuna.setText("Scor de sănătate financiară — " + lunaScurta);
        tvTitluRezumat.setText("Rezumat — " + lunaScurta);
        tvTitluDistributie.setText("Distribuție — " + lunaScurta);
        tvTitluDetalii.setText("Detalii — " + lunaScurta);
    }

    // ----------------------------------------------------------------
    // COLLAPSE
    // ----------------------------------------------------------------

    private void setupCollapse() {
        aplicaStareCollapse(bodyRezumat, tvArrowRezumat, null, rezumatDeschis);
        aplicaStareCollapse(bodyDistributie, tvArrowDistributie, null, distributieDeschisa);
        aplicaStareCollapse(
                requireView().findViewById(R.id.bodyTrend),
                tvArrowTrend, tvPreviewTrend, trendDeschis);
        aplicaStareCollapse(bodyDetalii, tvArrowDetalii, tvPreviewDetalii, detaliiDeschise);

        requireView().findViewById(R.id.headerRezumat).setOnClickListener(v -> {
            rezumatDeschis = !rezumatDeschis;
            aplicaStareCollapse(bodyRezumat, tvArrowRezumat, null, rezumatDeschis);
        });

        requireView().findViewById(R.id.headerDistributie).setOnClickListener(v -> {
            distributieDeschisa = !distributieDeschisa;
            aplicaStareCollapse(bodyDistributie, tvArrowDistributie, null, distributieDeschisa);
        });

        requireView().findViewById(R.id.headerTrend).setOnClickListener(v -> {
            trendDeschis = !trendDeschis;
            View bodyTrend = requireView().findViewById(R.id.bodyTrend);
            aplicaStareCollapse(bodyTrend, tvArrowTrend, tvPreviewTrend, trendDeschis);
            if (trendDeschis) actualizeazaLineChart();
        });

        requireView().findViewById(R.id.headerDetalii).setOnClickListener(v -> {
            detaliiDeschise = !detaliiDeschise;
            aplicaStareCollapse(bodyDetalii, tvArrowDetalii, tvPreviewDetalii, detaliiDeschise);
            if (detaliiDeschise) actualizeazaDetaliiCategorii();
        });
    }

    private void aplicaStareCollapse(View body, TextView arrow,
                                     TextView preview, boolean deschis) {
        body.setVisibility(deschis ? View.VISIBLE : View.GONE);
        arrow.setText(deschis ? "▾" : "▸");
        if (preview != null) {
            preview.setVisibility(deschis ? View.GONE : View.VISIBLE);
        }
    }

    // ----------------------------------------------------------------
    // SPINNER LINE CHART
    // ----------------------------------------------------------------

    private void setupSpinnerLineChart() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Venituri", "Cheltuieli", "Economii"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLineChart.setAdapter(adapter);

        spinnerLineChart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                tipLineChart = parent.getItemAtPosition(position).toString();
                actualizeazaLineChart();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ----------------------------------------------------------------
    // BUTOANE
    // ----------------------------------------------------------------

    private void setupBtnPredictii(View view) {
        view.findViewById(R.id.btnPredictii).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PredictiiActivity.class)));
    }

    private void setupBtnAI(View view) {
        view.findViewById(R.id.btnCereSfatAI).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AsistentAIActivity.class)));
    }

    // ----------------------------------------------------------------
    // ACTUALIZARE COMPLETA — apelata la schimbarea lunii
    // ----------------------------------------------------------------

    private void actualizeazaEcran() {
        actualizeazaSelectorLuna();
        actualizeazaTitluriCarduri();
        actualizeazaScor();
        actualizeazaRezumat();
        actualizeazaDonut();
        // Daca detaliile sunt deschise le reincarcam cu noua luna
        if (detaliiDeschise) actualizeazaDetaliiCategorii();
        // Daca trendul e deschis il reincarcam
        if (trendDeschis) actualizeazaLineChart();
    }

    // ----------------------------------------------------------------
    // SCOR FINANCIAR — recalculat per luna selectata
    // ----------------------------------------------------------------

    private void actualizeazaScor() {
        float[] cheltuieli = MockDataGenerator.getTotalPerLuna();
        float[] venituri   = MockDataGenerator.getVenituriPerLuna();
        float[] economii   = MockDataGenerator.getEconomiiPerLuna();

        // Indexul in array corespunzator lunii selectate
        // lunaOffset 0 = index 5 (ultima), -1 = index 4, etc.
        int idx = 5 + lunaOffset;

        float v = venituri[idx];
        float c = cheltuieli[idx];
        float e = economii[idx];

        // Factor 1: rata economii (max 40)
        float rataEconomii = v > 0 ? e / v : 0;
        int scoreEconomii  = (int) Math.min(40, rataEconomii * 100);

        // Factor 2: stabilitate cheltuieli (max 25)
        float medie = 0;
        for (float ch : cheltuieli) medie += ch;
        medie /= cheltuieli.length;
        float deviatie = 0;
        for (float ch : cheltuieli) deviatie += Math.abs(ch - medie);
        deviatie /= cheltuieli.length;
        int scoreStabilitate = (int) Math.min(25, 25 * (1 - deviatie / medie));

        // Factor 3: buget (max 20)
        int scoreBuget = c < v ? 20 : c < v * 1.1f ? 10 : 0;

        // Factor 4: trend (max 15) — comparam cu luna anterioara daca exista
        int scoreTrend = 0;
        if (idx > 0) {
            scoreTrend = c < cheltuieli[idx - 1] ? 15 :
                    c == cheltuieli[idx - 1] ? 7 : 0;
        }

        int scorTotal = scoreEconomii + scoreStabilitate + scoreBuget + scoreTrend;

        tvFactorEconomii.setText(String.valueOf(scoreEconomii));
        tvFactorStabilitate.setText(String.valueOf(scoreStabilitate));
        tvFactorBuget.setText(String.valueOf(scoreBuget));
        tvFactorTrend.setText(String.valueOf(scoreTrend));

        String label;
        int culoareScor;
        String descriere;
        if (scorTotal >= 75) {
            label = "Excelent";
            culoareScor = Color.parseColor("#4CAF50");
            descriere = "Finanțele tale sunt în formă excelentă!";
        } else if (scorTotal >= 50) {
            label = "Bun";
            culoareScor = Color.parseColor("#4CAF50");
            descriere = "Economisești constant. Sub medie la cheltuieli.";
        } else if (scorTotal >= 25) {
            label = "OK";
            culoareScor = Color.parseColor("#FFB74D");
            descriere = "Ai putea economisi mai mult luna aceasta.";
        } else {
            label = "Atenție";
            culoareScor = Color.parseColor("#E57373");
            descriere = "Cheltuielile depășesc veniturile!";
        }

        tvScorLabel.setText(label);
        tvScorLabel.setTextColor(culoareScor);
        tvScorDescriptie.setText(descriere);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(scorTotal, "Scor"));
        entries.add(new PieEntry(100 - scorTotal, ""));

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(new int[]{culoareScor, Color.parseColor("#0D1829")});
        ds.setDrawValues(false);
        ds.setSliceSpace(0f);

        pieChartScor.setData(new PieData(ds));
        pieChartScor.setDrawHoleEnabled(true);
        pieChartScor.setHoleRadius(65f);
        pieChartScor.setHoleColor(Color.parseColor("#1A2F50"));
        pieChartScor.setDrawCenterText(true);
        pieChartScor.setCenterText(String.valueOf(scorTotal));
        pieChartScor.setCenterTextColor(Color.WHITE);
        pieChartScor.setCenterTextSize(16f);
        pieChartScor.getDescription().setEnabled(false);
        pieChartScor.getLegend().setEnabled(false);
        pieChartScor.setRotationEnabled(false);
        pieChartScor.animateY(600);
        pieChartScor.invalidate();
    }

    // ----------------------------------------------------------------
    // REZUMAT LUNAR — recalculat per luna selectata
    // ----------------------------------------------------------------

    private void actualizeazaRezumat() {
        float[] venituri   = MockDataGenerator.getVenituriPerLuna();
        float[] cheltuieli = MockDataGenerator.getTotalPerLuna();
        float[] economii   = MockDataGenerator.getEconomiiPerLuna();

        int idx = 5 + lunaOffset;
        float v = venituri[idx];
        float c = cheltuieli[idx];
        float e = economii[idx];

        tvVenituri.setText((int) v + " RON");
        tvCheltuieli.setText((int) c + " RON");
        tvEconomii.setText((int) e + " RON");

        int pctEconomii = v > 0 ? (int) ((e / v) * 100) : 0;
        tvPlanSubtitlu.setText("Ai economisit " + pctEconomii + "% din venituri");

        progressVenituri.setProgress(100);
        progressCheltuieli.setProgress(v > 0 ? (int) ((c / v) * 100) : 0);
        progressEconomii.setProgress(v > 0 ? (int) ((e / v) * 100) : 0);

        if (c < v) {
            tvPlanStatus.setText("✓ On track");
            tvPlanStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            tvPlanStatus.setText("⚠ Atenție");
            tvPlanStatus.setTextColor(Color.parseColor("#E57373"));
        }
    }

    // ----------------------------------------------------------------
    // DONUT CHART — recalculat per luna selectata
    // ----------------------------------------------------------------

    private void actualizeazaDonut() {
        float[] date = MockDataGenerator.getCheltuieliPerCategorie(lunaOffset);

        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < CATEGORII.length; i++) {
            if (date[i] > 0) {
                entries.add(new PieEntry(date[i], CATEGORII[i]));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(CULORI_CATEGORII);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(2f);

        donutChart.setData(new PieData(dataSet));
        donutChart.setDrawHoleEnabled(true);
        donutChart.setHoleRadius(52f);
        donutChart.setTransparentCircleRadius(55f);
        donutChart.setHoleColor(Color.parseColor("#1A2F50"));
        donutChart.setDrawCenterText(true);

        float total = 0;
        float maxVal = 0;
        int maxIdx = 0;
        for (int i = 0; i < date.length; i++) {
            total += date[i];
            if (date[i] > maxVal) { maxVal = date[i]; maxIdx = i; }
        }
        int procent = total > 0 ? (int) ((maxVal / total) * 100) : 0;
        donutChart.setCenterText(procent + "%\n" + CATEGORII[maxIdx]);
        donutChart.setCenterTextColor(Color.WHITE);
        donutChart.setCenterTextSize(11f);

        donutChart.getDescription().setEnabled(false);
        donutChart.getLegend().setEnabled(false);
        donutChart.setRotationEnabled(false);
        donutChart.animateY(600);
        donutChart.invalidate();

        actualizeazaLegendaCategorii(date, total);
    }

    private void actualizeazaLegendaCategorii(float[] date, float total) {
        layoutLegendaCategorii.removeAllViews();

        LinearLayout rand = null;
        for (int i = 0; i < CATEGORII.length; i++) {
            if (date[i] == 0) continue;

            if (i % 2 == 0) {
                rand = new LinearLayout(requireContext());
                rand.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                p.bottomMargin = 6;
                rand.setLayoutParams(p);
                layoutLegendaCategorii.addView(rand);
            }

            LinearLayout item = new LinearLayout(requireContext());
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(android.view.Gravity.CENTER_VERTICAL);
            item.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            View dot = new View(requireContext());
            LinearLayout.LayoutParams pDot = new LinearLayout.LayoutParams(14, 14);
            pDot.setMarginEnd(6);
            dot.setLayoutParams(pDot);
            dot.setBackgroundColor(CULORI_CATEGORII[i]);

            TextView tv = new TextView(requireContext());
            int proc = total > 0 ? (int) ((date[i] / total) * 100) : 0;
            tv.setText(CATEGORII[i] + " " + proc + "%");
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(10f);

            item.addView(dot);
            item.addView(tv);
            if (rand != null) rand.addView(item);
        }
    }

    // ----------------------------------------------------------------
    // LINE CHART — fix, nu se schimba cu luna
    // ----------------------------------------------------------------

    private void actualizeazaLineChart() {
        float[] date;
        int culoare;

        switch (tipLineChart) {
            case "Cheltuieli":
                date    = MockDataGenerator.getTotalPerLuna();
                culoare = Color.parseColor("#E57373");
                break;
            case "Economii":
                date    = MockDataGenerator.getEconomiiPerLuna();
                culoare = Color.parseColor("#FFB74D");
                break;
            default:
                date    = MockDataGenerator.getVenituriPerLuna();
                culoare = Color.parseColor("#4CAF50");
                break;
        }

        tvBadgeLineChart.setText("Mai • " + (int) date[5] + " RON");
        tvBadgeLineChart.setBackgroundColor(culoare);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < date.length; i++) {
            entries.add(new Entry(i, date[i]));
        }

        LineDataSet ds = new LineDataSet(entries, tipLineChart);
        ds.setColor(culoare);
        ds.setLineWidth(2f);
        ds.setCircleColor(culoare);
        ds.setCircleRadius(4f);
        ds.setDrawValues(false);
        ds.setDrawFilled(true);
        ds.setFillColor(culoare);
        ds.setFillAlpha(40);
        ds.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        lineChart.setData(new LineData(ds));
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setBackgroundColor(Color.parseColor("#1A2F50"));

        String[] luniScurte = {"Ian", "Feb", "Mar", "Apr", "Mai", "Iun"};
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(luniScurte));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#7A9CC0"));
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        lineChart.getAxisLeft().setTextColor(Color.parseColor("#7A9CC0"));
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.animateX(600);
        lineChart.invalidate();
    }

    // ----------------------------------------------------------------
    // DETALII PER CATEGORIE — recalculate per luna selectata
    // ----------------------------------------------------------------

    private void actualizeazaDetaliiCategorii() {
        layoutDetaliiCategorii.removeAllViews();

        // Luna selectata si luna anterioara pentru comparatie
        float[] dateLuna     = MockDataGenerator.getCheltuieliPerCategorie(lunaOffset);
        float[] dateLunaTre  = MockDataGenerator.getCheltuieliPerCategorie(lunaOffset - 1);

        float maxVal = 0;
        for (float d : dateLuna) if (d > maxVal) maxVal = d;

        for (int i = 0; i < CATEGORII.length; i++) {
            if (dateLuna[i] == 0) continue;

            LinearLayout rand = new LinearLayout(requireContext());
            rand.setOrientation(LinearLayout.HORIZONTAL);
            rand.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams pRand = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            pRand.bottomMargin = 10;
            rand.setLayoutParams(pRand);

            TextView tvNume = new TextView(requireContext());
            tvNume.setText(CATEGORII[i]);
            tvNume.setTextColor(Color.parseColor("#7A9CC0"));
            tvNume.setTextSize(10f);
            tvNume.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            ProgressBar bara = new ProgressBar(requireContext(),
                    null, android.R.attr.progressBarStyleHorizontal);
            bara.setProgress(maxVal > 0 ? (int) ((dateLuna[i] / maxVal) * 100) : 0);
            bara.setMax(100);
            bara.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(CULORI_CATEGORII[i]));
            LinearLayout.LayoutParams pBara = new LinearLayout.LayoutParams(0, 8, 1.5f);
            pBara.setMarginStart(8);
            pBara.setMarginEnd(8);
            bara.setLayoutParams(pBara);

            float diferenta = dateLuna[i] - dateLunaTre[i];
            String trendText = diferenta > 0 ?
                    "↑ " + (int) dateLuna[i] + " RON" :
                    "↓ " + (int) dateLuna[i] + " RON";
            int trendCuloare = diferenta > 0 ?
                    Color.parseColor("#E57373") : Color.parseColor("#4CAF50");

            TextView tvSuma = new TextView(requireContext());
            tvSuma.setText(trendText);
            tvSuma.setTextColor(trendCuloare);
            tvSuma.setTextSize(10f);

            rand.addView(tvNume);
            rand.addView(bara);
            rand.addView(tvSuma);
            layoutDetaliiCategorii.addView(rand);
        }
    }
}