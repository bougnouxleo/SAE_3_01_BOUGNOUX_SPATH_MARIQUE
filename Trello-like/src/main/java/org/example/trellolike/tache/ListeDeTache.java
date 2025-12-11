package org.example.trellolike.tache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListeDeTache {
    /**
     * Nom de la ListeDeTache
     */
    private String nom;

    /**
     * Liste des Taches
     */
    private List<Tache> taches;

    /**
     * Constructeur de la ListeDeTache
     * @param nom le nom de la ListeDeTache
     */
    public ListeDeTache(String nom){
        this.nom = nom;
        this.taches = new ArrayList<>();
    }

    /**
     * Methode qui ajoute une tache a la liste
     * @param t la tache
     */
    public void ajouterTache(Tache t){
        taches.add(t);
    }

    /**
     * Methode qui retire une tache à la liste
     * @param t la tache
     */
    public void retirerTache(Tache t){
        taches.remove(t);
    }

    /**
     * Methode qui retourne le nom de la ListeDeTache
     * @return le nom de la ListeDeTache
     */
    public String getNom() {
        return nom;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ListeDeTache that)) return false;
        return Objects.equals(getNom(), that.getNom());
    }

    /**
     * Methode qui retourne la liste des taches
     * @return la liste des taches
     */
    public List<Tache> getTaches() {
        return taches;
    }

}
