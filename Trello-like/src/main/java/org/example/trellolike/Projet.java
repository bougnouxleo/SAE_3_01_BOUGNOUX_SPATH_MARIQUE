package org.example.trellolike;

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
     * Constructeur du projet
     *
     * @param nom le nom du projet
     */
    public Projet(String nom) {
        this.nom = nom;
        this.observateurs = new ArrayList<>();
        this.listeDeTaches = new ArrayList<>();
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
}

