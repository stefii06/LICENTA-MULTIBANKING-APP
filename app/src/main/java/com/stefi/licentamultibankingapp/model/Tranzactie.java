package com.stefi.licentamultibankingapp.model;

import java.util.Date;

public class Tranzactie {

    private String numeBanca;
    private String ultimeleCifre;
    private String categorie;
    private String emoji;
    private Date data;

    public Tranzactie(String numeBanca, String ultimeleCifre, String categorie, String emoji) {
        this.numeBanca = numeBanca;
        this.ultimeleCifre = ultimeleCifre;
        this.categorie = categorie;
        this.emoji = emoji;
        this.data = new Date();
    }

    public String getNumeBanca() { return numeBanca; }
    public String getUltimeleCifre() { return ultimeleCifre; }
    public String getCategorie() { return categorie; }
    public String getEmoji() { return emoji; }
    public Date getData() { return data; }
}