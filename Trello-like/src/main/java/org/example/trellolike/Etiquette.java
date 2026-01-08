package org.example.trellolike;

import java.io.Serializable;

public class Etiquette implements Serializable {

    private String nom;
    private String codeCouleur; // Stocké en Hex (ex: "#e74c3c")

    // Constructeur vide (OBLIGATOIRE XML)
    public Etiquette() {}

    /**
     * Constructeur
     * @param nom
     * @param codeCouleur
     */
    public Etiquette(String nom, String codeCouleur) {
        this.nom = nom;
        this.codeCouleur = codeCouleur;
    }

    // Getters & Setters

    /**
     * Getter nom
     * @return
     */
    public String getNom() { return nom; }
    /**
     * Setter nom
     * @param nom
     */
    public void setNom(String nom) { this.nom = nom; }

    /**
     * Getter codeCouleur
     * @return
     */
    public String getCodeCouleur() { return codeCouleur; }
    /**
     * Setter codeCouleur
     * @param codeCouleur
     */
    public void setCodeCouleur(String codeCouleur) { this.codeCouleur = codeCouleur; }

    @Override
    public String toString() { return nom; }
}