package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class VueTableau extends ScrollPane implements Observateur {

    private Projet projet;
    private KanbanController controller;
    private Button btnAjouterListe;

    private HBox conteneurColonnes;

    public VueTableau(Projet projet, KanbanController controller) {
        this.projet = projet;
        this.controller = controller;

        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setPannable(true);
        this.setStyle("-fx-background-color: #f4f4f4;");

        this.conteneurColonnes = new HBox();
        this.conteneurColonnes.setSpacing(20);
        this.conteneurColonnes.setPadding(new Insets(20));
        this.conteneurColonnes.setAlignment(Pos.TOP_LEFT);
        this.conteneurColonnes.setStyle("-fx-background-color: #f4f4f4;");

        this.setContent(conteneurColonnes);

        this.btnAjouterListe = new Button("+ Ajouter une liste");
        this.btnAjouterListe.setMinWidth(200);
        this.btnAjouterListe.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-font-size: 14px; -fx-cursor: hand;");

        this.btnAjouterListe.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle Liste");
            dialog.setHeaderText(null);
            dialog.setContentText("Nom:");
            dialog.showAndWait().ifPresent(nom -> controller.traiterAjoutListe(nom.trim()));
        });

        this.projet.enregistrerObservateur(this);
        this.actualiser(projet);
    }

    @Override
    public void actualiser(Sujet s) {
        if (!(s instanceof Projet)) return;

        this.conteneurColonnes.getChildren().clear();

        for (ListeDeTache liste : projet.getListeDeTaches()) {
            ColonneKanban colonneGraphique = new ColonneKanban(liste, this.controller);

            HBox.setHgrow(colonneGraphique, Priority.ALWAYS);

            configurerEvenementsColonne(colonneGraphique, liste);

            for (Tache t : liste.getTaches()) {
                CarteTache carteGraphique = new CarteTache(t);
                configurerEvenementsCarte(carteGraphique, t);
                colonneGraphique.ajouterCarte(carteGraphique);
            }

            this.conteneurColonnes.getChildren().add(colonneGraphique);
        }

        this.conteneurColonnes.getChildren().add(btnAjouterListe);
    }

    private void configurerEvenementsCarte(CarteTache carte, Tache t) {
        carte.setOnDragDetected(e -> {
            if (controller.verifierDroitDeplacer(t)) {
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(t.getId()));
                db.setContent(content);
            }
            e.consume();
        });
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
                controller.traiterDepotTache(id, listeAssociee);
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }
}