package com.stefi.licentamultibankingapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreManager {

    private static FirestoreManager instance;
    private final FirebaseFirestore db;
    private final String userId;

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    // Resetam instanta la logout
    public static void reset() {
        instance = null;
    }

    // Colectii principale
    public com.google.firebase.firestore.CollectionReference conturi() {
        return db.collection("users").document(userId).collection("conturi");
    }

    public com.google.firebase.firestore.CollectionReference tranzactii() {
        return db.collection("users").document(userId).collection("tranzactii");
    }

    public com.google.firebase.firestore.CollectionReference abonamente() {
        return db.collection("users").document(userId).collection("abonamente");
    }

    public com.google.firebase.firestore.CollectionReference contacte() {
        return db.collection("users").document(userId).collection("contacte");
    }

    public com.google.firebase.firestore.DocumentReference userDoc() {
        return db.collection("users").document(userId);
    }
}