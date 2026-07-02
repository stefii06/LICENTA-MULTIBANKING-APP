package com.stefi.licentamultibankingapp.model;

public class Abonament {

    private String nume;
    private String emoji;
    private double suma;
    private int ziuaLunii;
    private String card;
    private String categorie;
    private boolean variabil;
    private boolean dinRecurente;



    private String id;

    // Constructor gol necesar pentru Firestore
    public Abonament() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }



    public Abonament(String nume, String emoji, double suma, int ziuaLunii,
                     String card, String categorie, boolean variabil, boolean dinRecurente) {
        this.nume = nume;
        this.emoji = emoji;
        this.suma = suma;
        this.ziuaLunii = ziuaLunii;
        this.card = card;
        this.categorie = categorie;
        this.variabil = variabil;
        this.dinRecurente = dinRecurente;
    }

    public String getNume() { return nume; }
    public String getEmoji() { return emoji; }
    public double getSuma() { return suma; }
    public int getZiuaLunii() { return ziuaLunii; }
    public String getCard() { return card; }
    public String getCategorie() { return categorie; }
    public boolean isVariabil() { return variabil; }
    public boolean isDinRecurente() { return dinRecurente; }
    public void setSuma(double suma) { this.suma = suma; }
}