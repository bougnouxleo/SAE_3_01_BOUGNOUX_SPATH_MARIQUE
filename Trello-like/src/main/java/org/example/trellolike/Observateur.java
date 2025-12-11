package org.example.trellolike;

public interface Observateur {

    /**
     * Méthode qui actualise le Projet
     * @param s le projet
     */
    public void actualiser(Sujet s);
}
