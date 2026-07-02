package com.stefi.licentamultibankingapp.model;

import com.stefi.licentamultibankingapp.utils.FirestoreManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TranzactieRepository {

    private static TranzactieRepository instance;
    private List<Tranzactie> tranzactii = new ArrayList<>();

    private TranzactieRepository() {}

    public static TranzactieRepository getInstance() {
        if (instance == null) {
            instance = new TranzactieRepository();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    // Incarca tranzactiile din Firestore
    public void incarcaTranzactii(OnIncarcat callback) {
        FirestoreManager.getInstance().tranzactii()
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    tranzactii.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Tranzactie t = documentToTranzactie(doc);
                        tranzactii.add(t);
                    }
                    if (callback != null) callback.onIncarcat();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onIncarcat();
                });
    }

    public List<Tranzactie> getTranzactii() {
        return tranzactii;
    }

    public void adaugaTranzactie(Tranzactie tranzactie) {
        java.util.Map<String, Object> map = tranzactieToMap(tranzactie);
        FirestoreManager.getInstance().tranzactii().add(map)
                .addOnSuccessListener(ref -> {
                    tranzactie.setId(ref.getId());
                    tranzactii.add(0, tranzactie);
                });
    }

    private Tranzactie documentToTranzactie(DocumentSnapshot doc) {
        Date data = doc.getDate("data");
        Double sumaDouble = doc.getDouble("suma");
        float suma = sumaDouble != null ? sumaDouble.floatValue() : 0f;

        // Citim descrierea — poate fi null pentru tranzactii vechi, folosim categoria ca fallback
        String descriere = doc.getString("descriere");
        String categorie = doc.getString("categorie");
        if (descriere == null || descriere.isEmpty()) {
            descriere = categorie;
        }

        Tranzactie t = new Tranzactie(
                doc.getString("numeBanca"),
                doc.getString("ultimeleCifre"),
                descriere,
                categorie,
                doc.getString("emoji"),
                data != null ? data : new Date(),
                suma
        );
        t.setId(doc.getId());
        return t;
    }

    private java.util.Map<String, Object> tranzactieToMap(Tranzactie t) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("numeBanca", t.getNumeBanca());
        map.put("ultimeleCifre", t.getUltimeleCifre());
        map.put("descriere", t.getDescriere());
        map.put("categorie", t.getCategorie());
        map.put("emoji", t.getEmoji());
        map.put("suma", t.getSuma());
        map.put("data", t.getData());
        return map;
    }

    public interface OnIncarcat {
        void onIncarcat();
    }
}