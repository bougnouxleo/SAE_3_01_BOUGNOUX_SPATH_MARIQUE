package org.example.trellolike;

import org.example.trellolike.tache.GestionPersistance;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.vue.Observateur;

import java.time.LocalDate;
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

    /**
     * Liste des tâches archivées
     */
    private List<Tache> listeDesArchives;
    // Constructeur public (requis pour la sérialisation XML)
    public Projet() {
        this.observateurs = new ArrayList<>();
        this.listeDeTaches = new ArrayList<>();
        this.members = new ArrayList<>();
        this.listeDesArchives = new ArrayList<>();
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
     * Change l'ordre des colonnes dans le projet.
     * @param indexSource L'index actuel de la liste (avant déplacement)
     * @param indexCible L'index où on veut la déposer
     */
    public void deplacerListe(int indexSource, int indexCible) {
        // 1. Sécurités
        if (indexSource < 0 || indexSource >= listeDeTaches.size()) return;
        if (indexCible < 0 || indexCible >= listeDeTaches.size()) return;
        if (indexSource == indexCible) return;

        // 2. Déplacement dans la liste Java (On retire et on réinsère)
        ListeDeTache listeADeplacer = listeDeTaches.remove(indexSource);
        listeDeTaches.add(indexCible, listeADeplacer);

        // 3. Sauvegarde et Notification
        this.sauvegarderGlobalement();
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
        if (t.getDateFin() == null || t.getDateFin().isEmpty()) {
            return false;
        }
        try {
            LocalDate dateFinTache = LocalDate.parse(t.getDateFin());
            LocalDate aujourdhui = LocalDate.now();
            return aujourdhui.isAfter(dateFinTache);

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Méthode qui archive une tâche
     * @param t la tâche à archiver
     */
    public void archiverTache(Tache t) {
        t.setArchivee(true);

        ListeDeTache listeCourante = trouverListeDeLaTache(t);

        if (listeCourante != null) {
            listeCourante.retirerTache(t);
            this.listeDesArchives.add(t);
        }

        sauvegarderGlobalement();
    }

    /**
     * Méthode qui désarchive une tâche
     * @param liste la liste de tâche où remettre la tâche désarchivée
     */
    public void archiverListeDeTaches(ListeDeTache liste) {
        for (Tache t : new ArrayList<>(liste.getTaches())) {
            archiverTache(t);
        }
        listeDeTaches.remove(liste);
        sauvegarderGlobalement();
    }

    /**
     * Méthode qui retourne la liste des tâches archivées
     * @return la liste des tâches archivées
     */
    public List<Tache> getListeDesArchives() {
        return listeDesArchives;
    }

    /**
     * Méthode qui définit la liste des tâches archivées
     * @param listeDesArchives la liste des tâches archivées
     */
    public void setListeDesArchives(List<Tache> listeDesArchives) {
        this.listeDesArchives = listeDesArchives;
    }

    /**
     * Méthode qui retourne toutes les tâches du projet
     * @return la liste de toutes les tâches
     */
    public List<Tache> obtenirToutesLesTaches() {
        List<Tache> listeFinale = new ArrayList<>();
        for (ListeDeTache l : this.getListes()) {
            listeFinale.addAll(l.getTaches());
        }
        return listeFinale;
    }

    /**
     * Méthode qui supprime une tâche archivée
     * @param t la tâche à supprimer
     */
    public void supprimerTacheArchive(Tache t) {
        if(this.listeDesArchives.contains(t)){
            this.listeDesArchives.remove(t);
        }
        sauvegarderGlobalement();
    }


}

