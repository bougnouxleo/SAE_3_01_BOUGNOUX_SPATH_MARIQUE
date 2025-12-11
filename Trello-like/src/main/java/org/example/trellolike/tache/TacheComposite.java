package org.example.trellolike.tache;

import java.util.ArrayList;
import java.util.List;

public class TacheComposite extends Tache {
    /**
     * Liste des sous taches nécessaires à la réalisation de la tâche
     */
    private List<Tache> sousTaches;

    /**
     * Constructeur de la tache composite
     * @param nom nom de la tache
     * @param description description de la tache
     * @param dateDebut date de debut de la tache
     * @param dateFin date de fin de la tache
     */
    public TacheComposite(String nom, String description, String dateDebut, String dateFin) {
        super(nom, description, dateDebut, dateFin);
        this.sousTaches = new ArrayList<>();
    }
    /**
     * Methode qui retourne la duree de la tache
     * @return la duree de la tache
     */
    public int getDureeTotale(){
        int dureeTotale = 0;
        for (Tache sousTache : sousTaches) {
            dureeTotale += sousTache.getDureeTotale();
        }
        return dureeTotale;
    }

    /**
     * Affiche en détail la tache
     */
    public void afficherDetail(){
        System.out.println(this);
    }

    /**
     * Ajoute une sous tache dans la liste
     * @param sousTache la sous tache
     */
    public void ajouterSousTache(Tache sousTache) {
        sousTaches.add(sousTache);
    }

}
