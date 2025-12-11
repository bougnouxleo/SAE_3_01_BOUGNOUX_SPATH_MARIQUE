package org.example.trellolike.vue;

import org.example.trellolike.Sujet;

public interface Observateur {

    /**
     * Méthode qui actualise le Projet
     * @param s le projet
     */
    public void actualiser(Sujet s);
}
