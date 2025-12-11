package org.example.trellolike;

public class TacheSimple extends Tache{
    /**
     * Duree de la tache
     */
    private final int duree;

    /**
     * Constructeur de la tache simple
     * @param nom nom de la tache
     * @param description description de la tache
     * @param dateDebut date de debut de la tache
     * @param dateFin date de fin de la tache
     * @param duree duree de la tache
     */
    public TacheSimple(String nom, String description, String dateDebut, String dateFin, int duree){
        super(nom, description, dateDebut, dateFin);
        this.duree = duree;
    }

    /**
     * Methode qui retourne la duree de la tache
     * @return la duree de la tache
     */
    public int getDureeTotale(){
        return duree;
    }

    public void afficherDetail(){
        System.out.println(this);
    }
}
