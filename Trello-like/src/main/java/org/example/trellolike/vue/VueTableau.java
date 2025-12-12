package org.example.trellolike.vue;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class VueTableau extends HBox implements Observateur {

    private Projet projet;
    private KanbanController controller; // Référence vers la logique

    public VueTableau(Projet projet, KanbanController controller) {
        this.projet = projet;
        this.controller = controller;

        this.setSpacing(20);
        this.setStyle("-fx-padding: 20; -fx-background-color: #f4f4f4;");

        // Abonnement au modèle
        this.projet.enregistrerObservateur(this);

        // Premier rendu
        this.actualiser(projet);
    }

    @Override
    public void actualiser(Sujet s) {
        if (!(s instanceof Projet)) return;

        // 1. Nettoyage (La vue est passive, elle redessine tout)
        this.getChildren().clear();

        // 2. Reconstruction
        for (ListeDeTache liste : projet.getListeDeTaches()) {

            // Création du composant graphique COLONNE
            ColonneKanban colonneGraphique = new ColonneKanban(liste.getNom());

            // --- BINDING COLONNE (Vue -> Controller) ---
            // On configure la colonne pour qu'elle prévienne le contrôleur en cas de drop
            configurerEvenementsColonne(colonneGraphique, liste);

            // Remplissage des tâches dans la colonne
            for (Tache t : liste.getTaches()) {
                // Création du composant graphique CARTE
                CarteTache carteGraphique = new CarteTache(t);

                // --- BINDING CARTE (Vue -> Controller) ---
                configurerEvenementsCarte(carteGraphique, t);

                colonneGraphique.ajouterCarte(carteGraphique);
            }

            this.getChildren().add(colonneGraphique);
        }
    }

    // --- Méthodes privées pour attacher les événements ---

    private void configurerEvenementsCarte(CarteTache carte, Tache t) {
        // Début du Drag
        carte.setOnDragDetected(e -> {
            // La vue demande au contrôleur si c'est permis
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
                // La vue délègue l'action au contrôleur
                controller.traiterDepotTache(id, listeAssociee);
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }
}