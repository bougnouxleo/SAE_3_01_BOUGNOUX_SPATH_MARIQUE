package org.example.trellolike.controlleur;

import org.example.trellolike.Projet;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class KanbanController {

    private Projet projet;

    public KanbanController(Projet projet) {
        this.projet = projet;
    }

    // --- Actions déclenchées par la Vue ---

    /**
     * Appelé quand une tâche commence à être glissée.
     * @return true si le drag est autorisé (tache non bloquée), false sinon.
     */
    public boolean verifierDroitDeplacer(Tache t) {
        if (t.estBloquee()) {
            return false;
        }
        return true;
    }

    /**
     * Appelé quand on lâche une tâche dans une colonne.
     */
    public void traiterDepotTache(int idTache, ListeDeTache listeDestination) {
        Tache tache = Tache.findById(idTache);
        // On suppose que le projet sait retrouver la liste source, ou on la passe en paramètre
        ListeDeTache listeSource = projet.trouverListeDeLaTache(tache);

        try {
            // Modification du Modèle
            projet.deplacerTache(tache, listeSource, listeDestination);
        } catch (Exception e) {
            // En cas d'erreur métier, on peut afficher une alerte ici ou lancer un callback
            System.err.println("Erreur déplacement : " + e.getMessage());
        }
    }

    public void traiterOuvertureDetail(Tache t) {
        t.afficherDetail();
    }
}