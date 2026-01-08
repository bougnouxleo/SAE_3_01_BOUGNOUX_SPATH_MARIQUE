package org.example.trellolike;

import org.example.trellolike.tache.GestionPersistance;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.tache.TacheComposite;
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
     * Liste des tâches archivées
     */
    private List<Tache> listeDesArchives;
    // Constructeur public (requis pour la sérialisation XML)
    public Projet() {
        this.observateurs = new ArrayList<>();
        this.listeDeTaches = new ArrayList<>();
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
     * Recherche robuste par ID
     * Trouve la liste contenant une tâche en comparant les IDs (pas les pointeurs mémoire).
     */
    public ListeDeTache trouverListeDeLaTache(Tache t) {
        if (t == null) return null;
        for (ListeDeTache liste : this.listeDeTaches) {
            for (Tache tDansListe : liste.getTaches()) {
                // On compare les ID : C'est la clé du succès !
                if (tDansListe.getId() == t.getId()) {
                    return liste;
                }
            }
        }
        return null;
    }

    // Getters & Setters (Requis pour sérialisation XML)
    public List<ListeDeTache> getListes() { return listeDeTaches; }

    public void setListes(List<ListeDeTache> listes) { this.listeDeTaches = listes; }

    public void setNom(String nom) { this.nom = nom; }

    // Méthodes obligatoires pour observateur
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
     * Point d'entrée pour déplacer une tâche.
     * Déclenche le déplacement en cascade pour les composites.
     */
    public void deplacerTache(Tache t, ListeDeTache source, ListeDeTache destination) {
        // 1. On lance la récursion (déplace t et tous ses enfants)
        deplacerTacheRecursif(t, destination);

        // 2. On sauvegarde et on notifie une seule fois à la fin
        this.sauvegarderGlobalement();
    }

    /**
     * Permet le déplacement récursif des taches composites
     * @param t
     * @param destination
     */
    private void deplacerTacheRecursif(Tache t, ListeDeTache destination) {
        // 1. On cherche où est la tâche actuellement (via ID)
        ListeDeTache sourceActuelle = trouverListeDeLaTache(t);

        // 2. Si elle est trouvée ailleurs que dans la destination
        if (sourceActuelle != null && !sourceActuelle.equals(destination)) {

            // --- ASTUCE CRITIQUE ---
            // On ne fait pas sourceActuelle.retirerTache(t) direct, car 't' vient peut-être
            // du Composite et n'est pas l'objet exact de la liste.
            // On cherche l'objet RÉEL dans la liste source pour le retirer.
            Tache instanceReelle = null;
            for (Tache candidat : sourceActuelle.getTaches()) {
                if (candidat.getId() == t.getId()) {
                    instanceReelle = candidat;
                    break;
                }
            }

            if (instanceReelle != null) {
                sourceActuelle.retirerTache(instanceReelle); // On retire l'ancien
                destination.ajouterTache(instanceReelle);    // On déplace le réel
            }
        }

        // 3. Récursion pour les enfants (Composite)
        if (t instanceof TacheComposite) {
            TacheComposite composite = (TacheComposite) t;
            // On vérifie que la liste n'est pas vide
            if (composite.getSousTaches() != null && !composite.getSousTaches().isEmpty()) {
                // On copie la liste pour itérer sans risque (ConcurrentModification)
                List<Tache> enfants = new ArrayList<>(composite.getSousTaches());

                for (Tache sousTache : enfants) {
                    // Appel récursif : L'enfant rejoint la MÊME destination que papa
                    deplacerTacheRecursif(sousTache, destination);
                }
            }
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

