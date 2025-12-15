package org.example.trellolike.tache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListeDeTache implements Serializable {

    /**
     * Nom de la ListeDeTache
     */
    private String nom;

    /**
     * Liste des Taches
     */
    private List<Tache> taches;

    /**
     * CONSTRUCTEUR VIDE (OBLIGATOIRE POUR XML)
     * XMLEncoder l'utilise pour créer l'instance avant de remplir les champs.
     */
    public ListeDeTache() {
        this.taches = new ArrayList<>();
    }

    /**
     * Constructeur utilitaire (pour créer une liste manuellement dans le code)
     * @param nom le nom de la ListeDeTache
     */
    public ListeDeTache(String nom){
        this(); // Appelle le constructeur vide pour initialiser la liste
        this.nom = nom;
    }

    /**
     * SETTER NOM (OBLIGATOIRE POUR XML)
     * Permet au XMLDecoder de restaurer le nom.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * SETTER TACHES (OBLIGATOIRE POUR XML)
     * Permet au XMLDecoder de restaurer la liste des tâches.
     */
    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }

    public String getNom() {
        return nom;
    }

    public List<Tache> getTaches() {
        return taches;
    }

    // --- Méthodes Métier (Inchangées) ---

    public void ajouterTache(Tache t){
        taches.add(t);
    }

    public void retirerTache(Tache t){
        taches.remove(t);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ListeDeTache that)) return false;
        return Objects.equals(getNom(), that.getNom());
    }
}