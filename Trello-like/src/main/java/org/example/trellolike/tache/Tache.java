package org.example.trellolike.tache;

import org.example.trellolike.Etiquette;
import org.example.trellolike.Utilisateur;

import java.util.ArrayList;
import java.util.List;

public abstract class Tache {
    /**
     * Nom de la tâche
     */
    protected String nom;

    /**
     * Description de la tâche
     */
    protected String description;

    /**
     * Date de début de la tâche
     */
    protected String dateDebut;

    /**
     * Date de fin de la tâche
     */
    protected String dateFin;

    /**
     * Liste des Etiquettes (peut etre null)
     */
    protected List<Etiquette> etiquettes;

    protected Utilisateur utilisateur;


    /**
     * Constructeur de la tâche
     * @param nom le nom de la tâche
     * @param description la description de la tâche
     */
    public Tache(String nom, String description, String dateDebut, String dateFin) {
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etiquettes = new ArrayList<>();
    }

    public static Tache findById(int idTache) {
        //TODO
        return null;
    }

    /**
     * Méthode qui retourne le nom de la tâche
     * @return le nom de la tâche
     */
    public String getNom() {
        return nom;
    }

    /**
     * Méthode qui retourne la description de la tâche
     * @return la description de la tâche
     */
    public String getDescription() {
        return description;
    }

    /**
     * Méthode qui ajoute une Etiquette à la Tache
     * @param e l'Etiquette
     */
    public void ajouterEtiquette(Etiquette e){
        etiquettes.add(e);
    }

    /**
     * Méthode qui retourne la durée totale de la tâche
     * @return la durée totale de la tâche
     */
    public abstract int getDureeTotale();

    /**
     * Méthode qui affiche les détails de la tâche
     */
    public abstract void afficherDetail();

    /**
     * Méthode qui retourne la date de début de la tâche
     * @return la date de début de la tâche
     */
    public String getDateDebut() {
        return dateDebut;
    }

    /**
     * Méthode qui retourne la date de fin de la tâche
     * @return la date de fin de la tâche
     */
    public String getDateFin() {
        return dateFin;
    }

    /**
     * Méthode qui retourne la liste des Etiquettes
     * @return la liste des Etiquettes
     */
    public String toString(){
        return "Nom : " + nom + "\nDescription : " + description + "\nDate de début : " + dateDebut + "\nDate de fin : " + dateFin + "\n";
    }

    public List<Etiquette> getEtiquettes() {
        return etiquettes;
    }

    public Utilisateur getUtilisateurAssigne() {
        return utilisateur;
    }

    public static int getId() {
        //TODO
        return 0;
    }
}
