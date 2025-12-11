package org.example.trellolike;

import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.vue.Observateur;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Projet implements Sujet {
    /**
     * Nom du projet
     */
    private final String nom;

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


    /**
     * Constructeur du projet
     *
     * @param nom le nom du projet
     */
    public Projet(String nom) {
        this.nom = nom;
        this.observateurs = new ArrayList<>();
        this.listeDeTaches = new ArrayList<>();
        this.members = new ArrayList<>();
    }


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

    public ListeDeTache trouverListeDeLaTache(Tache tache) {
        ListeDeTache lres = null;
        for (ListeDeTache liste : listeDeTaches) {
            if (liste.getTaches().contains(tache)) {
                lres = liste;
            }
        }
        return lres;
    }
}

