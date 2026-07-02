package com.stefi.licentamultibankingapp.repository;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FlaskApiClient {

    private static final String URL_PREDICT = "https://flask-server-licenta-3.onrender.com/predict";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Callback — returnat pe thread-ul principal
    public interface OnRaspuns {
        void onSucces(float predictieTotal, float cheltuieliCurente, float diferenta, float procentTrend);
        void onEroare(String mesaj);
    }

    private final OkHttpClient client;

    public FlaskApiClient() {
        // Timeout mai mare pentru cold start Render (15 secunde)
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public void trimiteRequest(float[] cheltuieliPerLuna, int luniViitor,
                               android.os.Handler handler, OnRaspuns callback) {
        try {
            // Construim JSON-ul de trimis
            JSONArray array = new JSONArray();
            for (float val : cheltuieliPerLuna) {
                array.put(val);
            }

            JSONObject body = new JSONObject();
            body.put("cheltuieli_per_luna", array);
            body.put("luni_viitor", luniViitor);

            RequestBody requestBody = RequestBody.create(body.toString(), JSON);
            Request request = new Request.Builder()
                    .url(URL_PREDICT)
                    .post(requestBody)
                    .build();

            // OkHttp face requestul pe un thread separat automat
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    // Rulam pe main thread ca sa putem atinge UI-ul
                    handler.post(() -> callback.onEroare("Nu s-a putut conecta la server"));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        handler.post(() -> callback.onEroare("Eroare server: " + response.code()));
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);

                        float predictieTotal    = (float) json.getDouble("predictie_total");
                        float cheltuieliCurente = (float) json.getDouble("cheltuieli_curente");
                        float diferenta         = (float) json.getDouble("diferenta");
                        float procentTrend      = (float) json.getDouble("procent_trend");

                        handler.post(() -> callback.onSucces(
                                predictieTotal, cheltuieliCurente, diferenta, procentTrend));

                    } catch (Exception e) {
                        handler.post(() -> callback.onEroare("Eroare la parsarea răspunsului"));
                    }
                }
            });

        } catch (Exception e) {
            handler.post(() -> callback.onEroare("Eroare la construirea requestului"));
        }
    }
}