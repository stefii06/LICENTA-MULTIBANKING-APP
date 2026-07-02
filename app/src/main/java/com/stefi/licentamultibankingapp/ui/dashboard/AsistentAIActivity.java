package com.stefi.licentamultibankingapp.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.HorizontalScrollView;

import com.stefi.licentamultibankingapp.R;
import com.stefi.licentamultibankingapp.adapters.MesajAdapter;
import com.stefi.licentamultibankingapp.model.ContBancarRepository;
import com.stefi.licentamultibankingapp.model.MockDataGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.stefi.licentamultibankingapp.BuildConfig;

public class AsistentAIActivity extends AppCompatActivity {

    private RecyclerView rvMesaje;
    private EditText etMesaj;
    private TextView btnTrimite;
    private LinearLayout layoutSugestii;
    private HorizontalScrollView scrollSugestii;

    private MesajAdapter adapter;
    private List<MesajAdapter.Mesaj> mesaje = new ArrayList<>();
    private List<JSONObject> istoricConversatie = new ArrayList<>();


    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final MediaType JSON = MediaType.get("application/json");

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private static final String[] SUGESTII = {
            "Cum pot economisi mai mult?",
            "Analizează cheltuielile mele",
            "Sunt pe drumul cel bun?",
            "Ce pot îmbunătăți?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistent_ai);

        initViews();
        setupRecyclerView();
        setupSugestii();
        setupClickuri();
        trimiteMesajInitial();
    }

    private void initViews() {
        rvMesaje      = findViewById(R.id.rvMesaje);
        etMesaj       = findViewById(R.id.etMesaj);
        btnTrimite    = findViewById(R.id.btnTrimite);
        layoutSugestii = findViewById(R.id.layoutSugestii);
        scrollSugestii = findViewById(R.id.scrollSugestii);

        // Hint color setat din Java
        etMesaj.setHintTextColor(Color.parseColor("#7A9CC0"));

        findViewById(R.id.btnInapoi).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new MesajAdapter(mesaje);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMesaje.setLayoutManager(layoutManager);
        rvMesaje.setAdapter(adapter);
    }

    private void setupSugestii() {
        for (String sugestie : SUGESTII) {
            TextView chip = new TextView(this);
            chip.setText(sugestie);
            chip.setTextColor(Color.parseColor("#4A90D9"));
            chip.setTextSize(11f);
            chip.setBackground(getResources().getDrawable(android.R.drawable.btn_default));
            chip.setBackgroundColor(Color.parseColor("#1A2F50"));
            chip.setPadding(24, 12, 24, 12);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(8);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                etMesaj.setText(sugestie);
                trimiteMessaj();
                // Ascundem sugestiile dupa prima intrebare
                scrollSugestii.setVisibility(View.GONE);
            });

            layoutSugestii.addView(chip);
        }
    }

    private void setupClickuri() {
        btnTrimite.setOnClickListener(v -> trimiteMessaj());
    }

    // Construieste contextul financiar pentru system prompt
    private String construiesteContext() {
        float[] venituri   = MockDataGenerator.getVenituriPerLuna();
        float[] cheltuieli = MockDataGenerator.getTotalPerLuna();
        float[] economii   = MockDataGenerator.getEconomiiPerLuna();
        float[] categorii  = MockDataGenerator.getCheltuieliPerCategorie(0);

        int nrConturi = ContBancarRepository.getInstance().getConturi().size();

        float rataEconomii = venituri[5] > 0 ? economii[5] / venituri[5] : 0;
        int scoreEco = (int) Math.min(40, rataEconomii * 100);
        int scoreBuget = cheltuieli[5] < venituri[5] ? 20 : 0;
        int scoreTrend = cheltuieli[5] < cheltuieli[4] ? 15 : 0;

        float medie = 0;
        for (float c : cheltuieli) medie += c;
        medie /= cheltuieli.length;
        float dev = 0;
        for (float c : cheltuieli) dev += Math.abs(c - medie);
        dev /= cheltuieli.length;
        int scoreStab = (int) Math.min(25, 25 * (1 - dev / medie));
        int scorTotal = scoreEco + scoreStab + scoreBuget + scoreTrend;

        return "Ești un asistent financiar personal integrat într-o aplicație de multibanking. " +
                "Răspunde ÎNTOTDEAUNA în română, într-un ton prietenos dar profesionist. " +
                "Fii concis și specific — maxim 3-4 propoziții per răspuns. " +
                "Folosește datele financiare ale utilizatorului în răspunsuri.\n\n" +
                "DATE FINANCIARE ALE UTILIZATORULUI:\n" +
                "- Venituri luna curentă: " + (int)venituri[5] + " RON\n" +
                "- Cheltuieli luna curentă: " + (int)cheltuieli[5] + " RON\n" +
                "- Economii luna curentă: " + (int)economii[5] + " RON\n" +
                "- Rata economii: " + (int)(rataEconomii * 100) + "%\n" +
                "- Cheltuieli Mâncare: " + (int)categorii[0] + " RON\n" +
                "- Cheltuieli Transport: " + (int)categorii[1] + " RON\n" +
                "- Cheltuieli Shopping: " + (int)categorii[2] + " RON\n" +
                "- Cheltuieli Divertisment: " + (int)categorii[3] + " RON\n" +
                "- Cheltuieli Sănătate: " + (int)categorii[4] + " RON\n" +
                "- Cheltuieli Utilități: " + (int)categorii[5] + " RON\n" +
                "- Scor financiar AI: " + scorTotal + "/100\n" +
                "- Număr conturi conectate: " + nrConturi + "\n" +
                "- Trend cheltuieli: " + (cheltuieli[5] < cheltuieli[4] ? "în scădere" : "în creștere") + "\n";
    }

    // Mesajul initial trimis automat la deschidere
    private void trimiteMesajInitial() {
        String mesajInitial = "Salut! Analizează pe scurt situația mea financiară și spune-mi " +
                "3 observații cheie — ce fac bine, ce pot îmbunătăți și o recomandare concretă.";
        trimiteCareClaude(mesajInitial, true);
    }

    private void trimiteMessaj() {
        String text = etMesaj.getText().toString().trim();
        if (text.isEmpty()) return;

        etMesaj.setText("");
        scrollSugestii.setVisibility(View.GONE);

        // Afisam mesajul utilizatorului
        adapter.adaugaMesaj(new MesajAdapter.Mesaj(text, false));
        scrollToBottom();

        trimiteCareClaude(text, false);
    }

    private void trimiteCareClaude(String mesaj, boolean esteInitial) {
        // Mesaj de loading
        MesajAdapter.Mesaj loading = new MesajAdapter.Mesaj("Se gândește...", true);
        runOnUiThread(() -> {
            adapter.adaugaMesaj(loading);
            scrollToBottom();
        });

        try {
            // Adaugam mesajul in istoricul conversatiei
            JSONObject mesajUser = new JSONObject();
            mesajUser.put("role", "user");
            mesajUser.put("content", mesaj);
            istoricConversatie.add(mesajUser);

            // Construim request-ul
            JSONObject body = new JSONObject();
            body.put("model", "claude-haiku-4-5-20251001");
            body.put("max_tokens", 500);
            body.put("system", construiesteContext());

            JSONArray messages = new JSONArray();
            for (JSONObject m : istoricConversatie) {
                messages.put(m);
            }
            body.put("messages", messages);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("x-api-key", BuildConfig.ANTHROPIC_API_KEY)
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        // Inlocuim loading cu eroare
                        int idx = mesaje.indexOf(loading);
                        if (idx >= 0) {
                            mesaje.set(idx, new MesajAdapter.Mesaj(
                                    "Eroare de conexiune. Verifică internetul.", true));
                            adapter.notifyItemChanged(idx);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    android.util.Log.d("CLAUDE_API", responseBody);

                    try {

                        JSONObject json = new JSONObject(responseBody);
                        String raspuns = json.getJSONArray("content")
                                .getJSONObject(0)
                                .getString("text");

                        // Adaugam raspunsul in istoric
                        JSONObject mesajAI = new JSONObject();
                        mesajAI.put("role", "assistant");
                        mesajAI.put("content", raspuns);
                        istoricConversatie.add(mesajAI);

                        runOnUiThread(() -> {
                            int idx = mesaje.indexOf(loading);
                            if (idx >= 0) {
                                mesaje.set(idx, new MesajAdapter.Mesaj(raspuns, true));
                                adapter.notifyItemChanged(idx);
                                scrollToBottom();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            int idx = mesaje.indexOf(loading);
                            if (idx >= 0) {
                                mesaje.set(idx, new MesajAdapter.Mesaj(
                                        "Eroare la procesarea răspunsului.", true));
                                adapter.notifyItemChanged(idx);
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Eroare: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollToBottom() {
        rvMesaje.scrollToPosition(mesaje.size() - 1);
    }
}