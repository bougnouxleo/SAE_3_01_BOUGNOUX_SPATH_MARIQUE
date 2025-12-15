package org.example.trellolike.vue;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class VueTableau extends HBox implements Observateur {
    /**
     * Le projet associé au tableau Kanban
     */
    private Projet projet;
    /**
     * Le contrôleur gérant la logique du Kanban
     */
    private KanbanController controller;

    /**
     * Constructeur de la vue du tableau Kanban
     * @param projet le projet associé
     * @param controller le contrôleur du Kanban
     */
    public VueTableau(Projet projet, KanbanController controller) {
        this.projet = projet;
        this.controller = controller;

        this.setSpacing(20);
        this.setStyle("-fx-padding: 20; -fx-background-color: #f4f4f4;");

        this.projet.enregistrerObservateur(this);

        this.actualiser(projet);
    }

    /**
     * Actualise la vue du tableau Kanban en fonction de l'état du projet
     * @param s le projet
     */
    @Override
    public void actualiser(Sujet s) {
        if (!(s instanceof Projet)) return;

        this.getChildren().clear();

        for (ListeDeTache liste : projet.getListeDeTaches()) {
            ColonneKanban colonneGraphique = new ColonneKanban(liste, this.controller);
            configurerEvenementsColonne(colonneGraphique, liste);

            for (Tache t : liste.getTaches()) {
                CarteTache carteGraphique = new CarteTache(t);

                configurerEvenementsCarte(carteGraphique, t);

                colonneGraphique.ajouterCarte(carteGraphique);
            }

            this.getChildren().add(colonneGraphique);
        }
    }

    /**
     * Configure les événements pour une carte de tâche
     * @param carte la carte de tâche
     * @param t la tâche associée
     */
    private void configurerEvenementsCarte(CarteTache carte, Tache t) {
        // Début du Drag
        carte.setOnDragDetected(e -> {
            if (controller.verifierDroitDeplacer(t)) {
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(t.getId()));
                db.setContent(content);
            }
            e.consume();
        });

        // Double Clic
        carte.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) controller.traiterOuvertureDetail(t);
        });
    }

    /**
     * Configure les événements pour une colonne Kanban
     * @param col la colonne Kanban
     * @param listeAssociee la liste de tâches associée
     */
    private void configurerEvenementsColonne(ColonneKanban col, ListeDeTache listeAssociee) {
        col.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        });

        col.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int id = Integer.parseInt(db.getString());
                controller.traiterDepotTache(id, listeAssociee);
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }
}