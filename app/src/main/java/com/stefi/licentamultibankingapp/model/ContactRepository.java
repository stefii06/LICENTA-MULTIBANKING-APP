package com.stefi.licentamultibankingapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private static final String PREFS_NAME = "contacte_prefs";
    private static final String KEY_CONTACTE = "contacte";

    private static ContactRepository instance;
    private List<Contact> contacte = new ArrayList<>();
    private SharedPreferences prefs;
    private Gson gson = new Gson();

    private ContactRepository(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        incarcaContacte();
    }

    public static ContactRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ContactRepository(context);
        }
        return instance;
    }

    public List<Contact> getContacte() {
        return contacte;
    }

    public void adaugaContact(Contact contact) {
        contacte.add(contact);
        salveazaContacte();
    }

    private void salveazaContacte() {
        String json = gson.toJson(contacte);
        prefs.edit().putString(KEY_CONTACTE, json).apply();
    }

    private void incarcaContacte() {
        String json = prefs.getString(KEY_CONTACTE, null);
        if (json != null) {
            Type type = new TypeToken<List<Contact>>(){}.getType();
            contacte = gson.fromJson(json, type);
        } else {
            // Contacte mock initiale
            contacte.add(new Contact("Ana", "Maria", "RO49AAAA1B31007593840001", ""));
            contacte.add(new Contact("Mihai", "Ionescu", "RO49AAAA1B31007593840002", ""));
            contacte.add(new Contact("Ioana", "Pop", "RO49AAAA1B31007593840003", ""));
            contacte.add(new Contact("Radu", "Dan", "RO49AAAA1B31007593840004", ""));
            salveazaContacte();
        }
    }
}