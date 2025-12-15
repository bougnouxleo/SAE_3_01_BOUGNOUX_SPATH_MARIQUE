package org.example.trellolike;

import org.example.trellolike.tache.GestionPersistance;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.vue.Observateur;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Projet implements Sujet, java.io.Serializable {
    /**
     * Nom du projet
     */
    private String nom;

    // Instance unique (Singleton) pour remplacer la BDD accessible partout
    private static Projet instance;

    /**
     * Liste des observateurs
     */
    private List<Observateur> observateurs;

    /**
     * Liste de ListeDeTache
     */
    private List<ListeDeTache> listeDeTaches;

    /**
     * Liste des membres du projet
     */
    private ArrayList<Utilisateur> members;


    // Constructeur public (requis pour la sérialisation XML)
    public Projet() {
        this.observateurs = new ArrayList<>();
        this.listeDeTaches = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    /**
     * Constructeur du projet
     *
     * @param nom le nom du projet
     */
    public Projet(String nom) {
        this();
        this.nom = nom;
    }


    // --- Gestion du Singleton & Chargement ---
    public static Projet getInstance() {
        if (instance == null) {
            instance = GestionPersistance.charger();
        }
        return instance;
    }

    public void sauvegarderGlobalement() {
        GestionPersistance.sauvegarder(this);
        this.notifierObservateurs(); // Sauvegarder déclenche souvent une mise à jour
    }

    // --- Méthodes de recherche (remplace le SQL SELECT) ---

    public Tache trouverTacheParId(int id) {
        // On parcourt tout en mémoire (in-memory database)
        for (ListeDeTache liste : this.listeDeTaches) {
            for (Tache t : liste.getTaches()) {
                if (t.getId() == id) return t;
            }
        }
        return null;
    }

    /**
     * Méthode qui trouve la liste de tâche par une tâche donnée
     * @param t la tâche dont on cherche la liste parente
     * @return la liste de tâche parente
     */
    public ListeDeTache trouverListeDeLaTache(Tache t) {
        for (ListeDeTache liste : this.listeDeTaches) {
            if (liste.getTaches().contains(t)) return liste;
        }
        return null; // Devrait lancer une exception
    }

    // Getters & Setters (Requis pour sérialisation XML)
    public List<ListeDeTache> getListes() { return listeDeTaches; }

    public void setListes(List<ListeDeTache> listes) { this.listeDeTaches = listes; }

    public void setNom(String nom) { this.nom = nom; }

    public void enregistrerObservateur(Observateur o) {
        observateurs.add(o);
    }

    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    public void notifierObservateurs() {
        for (Observateur o : observateurs) {
            o.actualiser(this);
        }
    }

    /**
     * deplacement d'une tache d'une liste à une autre
     *
     * @param t
     * @param l1
     * @param l2
     */
    public void deplacerTache(Tache t, ListeDeTache l1, ListeDeTache l2) {
        l1.retirerTache(t);
        l2.ajouterTache(t);
        notifierObservateurs();
    }

    /**
     * Méthode qui retourne le nom du Projet
     *
     * @return le nom du Projet
     */
    public String getNom() {
        return nom;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Projet projet)) return false;
        return Objects.equals(getNom(), projet.getNom());
    }

    /**
     * Méthode qui ajoute un membre au projet
     *
     * @param membre le membre à ajouter
     */
    public void addMembers(Utilisateur membre) {
        this.members.add(membre);

    }

    /**
     * Méthode qui supprime un membre du projet
     *
     * @param membre le membre à supprimer
     */
    public void removeMembers(Utilisateur membre) {
        this.members.remove(membre);
    }

    public List<ListeDeTache> getListeDeTaches() {
        return listeDeTaches;
    }

    public void ajouterListe(ListeDeTache liste) {
        listeDeTaches.add(liste);
    }

    /**
     * Méthode qui vérifie si une tâche est terminée
     * @param t la tâche à vérifier
     * @return true si la tâche est terminée, false sinon
     */
    public boolean estTacheTerminee(Tache t) {
        ListeDeTache listeParent = trouverListeDeLaTache(t);
        if (listeParent == null) return false;
        return listeParent.getNom().equalsIgnoreCase("Terminé");
    }


}

