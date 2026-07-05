package com.stefi.licentamultibankingapp.model;

import java.util.Date;

public class Tranzactie {

    private String numeBanca;
    private String ultimeleCifre;
    private String descriere;   // numele comerciantului: Lidl, Netflix, Uber etc.
    private String categorie;
    private String emoji;
    private Date data;
    private float suma;

    private String id;
    private String contactId;

    // Constructor gol necesar pentru Firestore
    public Tranzactie() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public Tranzactie(String numeBanca, String ultimeleCifre, String categorie, String emoji) {
        this.numeBanca = numeBanca;
        this.ultimeleCifre = ultimeleCifre;
        this.categorie = categorie;
        this.emoji = emoji;
        this.data = new Date();
        this.suma = 0f;
    }

    // Constructor folosit de MockDataGenerator cu data si suma specifice
    public Tranzactie(String numeBanca, String ultimeleCifre, String categorie,
                      String emoji, Date data, float suma) {
        this.numeBanca = numeBanca;
        this.ultimeleCifre = ultimeleCifre;
        this.categorie = categorie;
        this.emoji = emoji;
        this.data = data;
        this.suma = suma;
    }

    // Constructor complet cu descriere
    public Tranzactie(String numeBanca, String ultimeleCifre, String descriere,
                      String categorie, String emoji, Date data, float suma) {
        this.numeBanca = numeBanca;
        this.ultimeleCifre = ultimeleCifre;
        this.descriere = descriere;
        this.categorie = categorie;
        this.emoji = emoji;
        this.data = data;
        this.suma = suma;
    }

    public String getNumeBanca() { return numeBanca; }
    public String getUltimeleCifre() { return ultimeleCifre; }
    public String getDescriere() { return descriere; }
    public String getCategorie() { return categorie; }
    public String getEmoji() { return emoji; }
    public Date getData() { return data; }
    public float getSuma() { return suma; }

    public void setNumeBanca(String numeBanca) { this.numeBanca = numeBanca; }
    public void setUltimeleCifre(String ultimeleCifre) { this.ultimeleCifre = ultimeleCifre; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public void setData(Date data) { this.data = data; }
    public void setSuma(float suma) { this.suma = suma; }
}