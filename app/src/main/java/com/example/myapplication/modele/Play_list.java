package com.example.myapplication.modele;

public class Play_list {
    private String nom;
    private String user;

    public Play_list() {
        // Constructeur vide requis pour Firestore
    }

    public Play_list(String nom, String user) {
        this.nom = nom;
        this.user = user;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
