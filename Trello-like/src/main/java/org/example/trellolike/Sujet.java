package org.example.trellolike;

public interface Sujet {
    /**
     * Méthode qui enregistre un nouvel Observateur
     * @param o l'observateur
     */
    public void enregistrerObservateur(Observateur o);

    /**
     * Méthode qui supprime un Observateur
     * @param o l'observateur
     */
    public void supprimerObservateur(Observateur o);

    /**
     * Méthode qui notifie les Observateurs
     */
    public void notifierObservateurs();
}
