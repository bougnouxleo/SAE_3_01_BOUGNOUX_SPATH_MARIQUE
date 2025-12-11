package org.example.trellolike;

import java.util.Objects;

public class Etiquette {
    /**
     * Nom de l'étiquette
     */
    private String nom;

    /**
     * Couleur de l'étiquette
     */
    private String couleur;

    /**
     * Description de l'étiquette
     */
    private String description;

    /**
     * Constructeur de l'étiquette
     * @param nom nom de l'etiquette
     * @param couleur couleur de l'étiquette
     * @param description description de l'étiquette
     */
    public Etiquette(String nom, String couleur, String description) {
        this.nom = nom;
        this.couleur = couleur;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Etiquette etiquette)) return false;
        return Objects.equals(nom, etiquette.nom) && Objects.equals(couleur, etiquette.couleur) && Objects.equals(description, etiquette.description);
    }

    /**
     * Méthode qui retourne le nom de l'étiquette
     * @return le nom de l'étiquette
     */
    public String getNom() {
        return nom;
    }

    /**
     * Méthode qui retourne la couleur de l'étiquette
     * @return la couleur de l'étiquette
     */
    public String getCouleur() {
        return couleur;
    }

    /**
     * Méthode qui retourne la description de l'étiquette
     * @return la description de l'étiquette
     */
    public String getDescription() {
        return description;
    }
}
