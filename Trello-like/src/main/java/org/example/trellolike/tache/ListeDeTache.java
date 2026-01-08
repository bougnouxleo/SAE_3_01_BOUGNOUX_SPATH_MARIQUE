package org.example.trellolike.tache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente une liste de tâches (ex: "A faire", "En cours", "Terminé").
 * <p>
 * Cette classe contient un nom et une collection de tâches associées.
 * Elle implémente {@link Serializable} pour permettre la persistance.
 * </p>
 */
public class ListeDeTache implements Serializable {

    /**
     * Nom de la ListeDeTache.
     */
    private String nom;

    /**
     * Liste des Taches contenues dans cette liste.
     */
    private List<Tache> taches;

    /**
     * Constructeur vide (Obligatoire pour la sérialisation XML).
     * <p>
     * XMLEncoder l'utilise pour créer l'instance avant de remplir les champs.
     * Initialise la liste interne des tâches pour éviter les NullPointerException.
     * </p>
     */
    public ListeDeTache() {
        this.taches = new ArrayList<>();
    }

    /**
     * Constructeur utilitaire pour créer une liste manuellement.
     *
     * @param nom le nom de la ListeDeTache
     */
    public ListeDeTache(String nom){
        this(); // Appelle le constructeur vide pour initialiser la liste
        this.nom = nom;
    }

    /**
     * Définit le nom de la liste.
     * <p>
     * Méthode requise par XMLDecoder pour restaurer l'état de l'objet.
     * </p>
     *
     * @param nom Le nouveau nom de la liste.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Définit la liste des tâches.
     * <p>
     * Méthode requise par XMLDecoder pour restaurer l'état de l'objet.
     * </p>
     *
     * @param taches La liste des tâches à associer.
     */
    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }

    /**
     * Récupère le nom de la liste de tâches.
     *
     * @return Le nom de la liste.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Récupère la liste des tâches contenues dans cette liste.
     *
     * @return Une liste d'objets {@link Tache}.
     */
    public List<Tache> getTaches() {
        return taches;
    }

    // --- Méthodes Métier (Inchangées) ---

    /**
     * Ajoute une tâche à la liste.
     *
     * @param t La tâche à ajouter.
     */
    public void ajouterTache(Tache t){
        taches.add(t);
    }

    /**
     * Retire une tâche de la liste.
     *
     * @param t La tâche à retirer.
     */
    public void retirerTache(Tache t){
        taches.remove(t);
    }

    /**
     * Vérifie l'égalité entre cette liste et un autre objet.
     * <p>
     * L'égalité est basée uniquement sur le nom de la liste de tâches.
     * </p>
     *
     * @param o L'objet à comparer.
     * @return true si les objets sont égaux, false sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ListeDeTache that)) return false;
        return Objects.equals(getNom(), that.getNom());
    }

}