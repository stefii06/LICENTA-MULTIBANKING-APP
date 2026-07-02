package com.stefi.licentamultibankingapp.model;

import com.stefi.licentamultibankingapp.utils.FirestoreManager;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private static ContactRepository instance;
    private List<Contact> contacte = new ArrayList<>();

    private ContactRepository() {}

    public static ContactRepository getInstance() {
        if (instance == null) {
            instance = new ContactRepository();
        }
        return instance;
    }

    // Versiunea cu Context nu mai e necesara dar o pastram pt compatibilitate
    public static ContactRepository getInstance(android.content.Context context) {
        return getInstance();
    }

    public static void reset() {
        instance = null;
    }

    // Incarca contactele din Firestore
    public void incarcaContacte(OnIncarcat callback) {
        FirestoreManager.getInstance().contacte().get()
                .addOnSuccessListener(snapshot -> {
                    contacte.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Contact c = documentToContact(doc);
                        contacte.add(c);
                    }
                    if (callback != null) callback.onIncarcat();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onIncarcat();
                });
    }

    public List<Contact> getContacte() {
        return contacte;
    }

    public void adaugaContact(Contact contact) {
        java.util.Map<String, Object> map = contactToMap(contact);
        FirestoreManager.getInstance().contacte().add(map)
                .addOnSuccessListener(ref -> {
                    contact.setId(ref.getId());
                    contacte.add(contact);
                });
    }

    public void stergeContact(String id) {
        FirestoreManager.getInstance().contacte().document(id).delete()
                .addOnSuccessListener(v ->
                        contacte.removeIf(c -> id.equals(c.getId())));
    }

    private Contact documentToContact(DocumentSnapshot doc) {
        Contact c = new Contact(
                doc.getString("nume") != null ? doc.getString("nume") : "",
                doc.getString("prenume") != null ? doc.getString("prenume") : "",
                doc.getString("iban") != null ? doc.getString("iban") : "",
                doc.getString("nota") != null ? doc.getString("nota") : "",
                doc.getString("telefon") != null ? doc.getString("telefon") : ""
        );
        c.setId(doc.getId());
        return c;
    }

    private java.util.Map<String, Object> contactToMap(Contact c) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("nume", c.getNume());
        map.put("prenume", c.getPrenume());
        map.put("iban", c.getIban());
        map.put("nota", c.getNota());
        map.put("telefon", c.getTelefon());
        return map;
    }

    public interface OnIncarcat {
        void onIncarcat();
    }
}