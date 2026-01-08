package org.example.trellolike.controlleur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.trellolike.Journal;
import org.example.trellolike.Projet;
import org.example.trellolike.tache.Tache;

import java.util.ArrayList;
import java.util.List;

public class ArchiveController {
    /**
     * Le projet associé au contrôleur
     */
    private Projet projet;

    /**
     * Constructeur de l'ArchiveController
     * @param projet le projet associé
     */
    public ArchiveController(Projet projet) {
        this.projet = projet;
    }

    /**
     * Gère l'ouverture de la fenêtre de détails d'une tâche.
     * @param t la tâche dont on veut afficher les détails
     */
    public void traiterOuvertureDetail(Tache t) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("Détails : " + t.getNom());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label titre = new Label(t.getNom());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label dates = new Label("Début : " + t.getDateDebut() + " | Fin : " + t.getDateFin());

        Label duree = new Label("Durée estimée : " + t.getDureeTotale() + "J");
        duree.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");

        Label lblDesc = new Label("Description :");
        TextArea description = new TextArea(t.getDescription());
        description.setEditable(false);
        description.setMaxHeight(100);

        Button supprimerBtn = new Button("Supprimer définitivement");
        supprimerBtn.setOnAction(e -> {
            projet.supprimerTacheArchive(t);
            Journal.log("Suppression définitive de la tâche archivée : " + t.getNom());
            detailStage.close();
        });

        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> detailStage.close());

        layout.getChildren().addAll(
                titre, dates, duree,
                lblDesc, description,
                supprimerBtn,
                btnFermer
        );

        Scene scene = new Scene(layout, 450, 750);
        detailStage.setScene(scene);
        detailStage.show();
    }


}
