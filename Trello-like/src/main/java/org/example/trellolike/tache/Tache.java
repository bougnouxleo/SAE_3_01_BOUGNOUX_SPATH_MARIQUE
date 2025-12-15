package org.example.trellolike.tache;

import org.example.trellolike.Etiquette;
import org.example.trellolike.Projet;
import org.example.trellolike.Utilisateur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Tache implements java.io.Serializable {
    private int id;
    private static int compteurId = 0; // Pour générer des ID uniques
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
    /**
     * Liste des ids des taches pour les dépendances
     */
    protected List<Integer> idsDependances = new ArrayList<>();

    /**
     * Indique si la tâche est archivée
     */
    private boolean estArchivee = false;

    public Tache() { } // Requis pour XML
    /**
     * Constructeur de la tâche
     * @param nom le nom de la tâche
     * @param description la description de la tâche
     */
    public Tache(String nom, String description, String dateDebut, String dateFin) {
        this.id = ++compteurId;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etiquettes = new ArrayList<>();
    }

    public static Tache findById(int id) {
        // On demande au Singleton Projet de chercher dans ses listes en mémoire
        return Projet.getInstance().trouverTacheParId(id);
    }

    public void save() {
        // En sérialisation, "sauvegarder une tâche" veut dire "réécrire tout le fichier XML"
        Projet.getInstance().sauvegarderGlobalement();
    }

    // Getters Setters classiques...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // Requis XML

    /**
     * Setter nom XML
     * @param nom
     */
    public void setNom(String nom) {
        this.nom = nom;
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
     * setter XML
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
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

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
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

    public void ajouterDependance(Tache t) {
        if (t.getId() != this.id) { // Empêche de dépendre de soi-même
            this.idsDependances.add(t.getId());
        }
    }

    public List<Integer> getIdsDependances() {
        return idsDependances;
    }

    /**
     * Méthode qui indique si la tâche est bloquée (si une dépendance n'est pas terminée)
     * @return true si la tâche est bloquée, false sinon
     */
    public boolean estBloquee() {
        if (idsDependances.isEmpty()) return false;

        for (Integer idDep : idsDependances) {
            Tache t = Tache.findById(idDep);
            if (t != null && !Projet.getInstance().estTacheTerminee(t)) {
                return true; // Bloqué car une dépendance n'est pas finie
            }
        }
        return false;
    }

    /**
     * Méthode qui archive ou désarchive la tâche
     * @param b true pour archiver, false pour désarchiver
     */
    public void setArchivee(boolean b) {
        this.estArchivee = b;
    }

    /**
     * Méthode qui indique si la tâche est archivée
     * @return true si la tâche est archivée, false sinon
     */
    public boolean estArchivee() {
        return estArchivee;
    }

    public int getDuree() {
        //TODO
        return 0;
    }
}
