package com.stefi.licentamultibankingapp.model;

import com.stefi.licentamultibankingapp.utils.FirestoreManager;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AbonamentRepository {

    private static AbonamentRepository instance;
    private List<Abonament> abonamente = new ArrayList<>();

    private AbonamentRepository() {}

    public static AbonamentRepository getInstance() {
        if (instance == null) {
            instance = new AbonamentRepository();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public void incarcaAbonamente(OnIncarcat callback) {
        FirestoreManager.getInstance().abonamente().get()
                .addOnSuccessListener(snapshot -> {
                    abonamente.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Abonament a = documentToAbonament(doc);
                        abonamente.add(a);
                    }
                    if (callback != null) callback.onIncarcat();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onIncarcat();
                });
    }

    public List<Abonament> getAbonamente() {
        return abonamente;
    }

    public void adaugaAbonament(Abonament abonament) {
        java.util.Map<String, Object> map = abonamentToMap(abonament);
        FirestoreManager.getInstance().abonamente().add(map)
                .addOnSuccessListener(ref -> {
                    abonament.setId(ref.getId());
                    abonamente.add(abonament);
                });
    }

    public double getTotalLunar() {
        double total = 0;
        for (Abonament a : abonamente) total += a.getSuma();
        return total;
    }

    public Abonament getUrmatoareaPlata() {
        if (abonamente.isEmpty()) return null;
        Abonament urmatoarea = abonamente.get(0);
        for (Abonament a : abonamente) {
            if (a.getZiuaLunii() < urmatoarea.getZiuaLunii()) {
                urmatoarea = a;
            }
        }
        return urmatoarea;
    }

    private Abonament documentToAbonament(DocumentSnapshot doc) {
        Double suma = doc.getDouble("suma");
        Long ziua = doc.getLong("ziuaLunii");
        Boolean variabil = doc.getBoolean("variabil");
        Boolean dinRecurente = doc.getBoolean("dinRecurente");

        Abonament a = new Abonament(
                doc.getString("nume"),
                doc.getString("emoji"),
                suma != null ? suma : 0.0,
                ziua != null ? ziua.intValue() : 1,
                doc.getString("card"),
                doc.getString("categorie"),
                variabil != null && variabil,
                dinRecurente != null && dinRecurente
        );
        a.setId(doc.getId());
        return a;
    }

    private java.util.Map<String, Object> abonamentToMap(Abonament a) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("nume", a.getNume());
        map.put("emoji", a.getEmoji());
        map.put("suma", a.getSuma());
        map.put("ziuaLunii", a.getZiuaLunii());
        map.put("card", a.getCard());
        map.put("categorie", a.getCategorie());
        map.put("variabil", a.isVariabil());
        map.put("dinRecurente", a.isDinRecurente());
        return map;
    }

    public interface OnIncarcat {
        void onIncarcat();
    }
}