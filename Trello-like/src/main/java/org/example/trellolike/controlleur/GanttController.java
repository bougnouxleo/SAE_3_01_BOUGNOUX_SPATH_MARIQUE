package org.example.trellolike.controlleur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.trellolike.Projet;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.tache.TacheComposite;

import java.util.ArrayList;
import java.util.List;

public class GanttController {
    /**
     * Le projet associé au Gantt
     */
    private Projet projet;

    /**
     * Constructeur du GanttController
     * @param projet le projet associé
     */
    public GanttController(Projet projet) {

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

        VBox boxDependances = new VBox(5);
        List<Tache> tachesBloquantes = new ArrayList<>();

        for (Integer idDep : t.getIdsDependances()) {
            Tache dep = Tache.findById(idDep);
            if (dep != null && !projet.estTacheTerminee(dep)) {
                tachesBloquantes.add(dep);
            }
        }

        if (!tachesBloquantes.isEmpty()) {
            Label lblAlerte = new Label("⚠️ BLOQUÉE par " + tachesBloquantes.size() + " tâche(s) :");
            lblAlerte.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            boxDependances.getChildren().add(lblAlerte);

            for (Tache bloquant : tachesBloquantes) {
                Label lblNom = new Label(" • " + bloquant.getNom());
                lblNom.setStyle("-fx-text-fill: red; -fx-padding: 0 0 0 20;");

                boxDependances.getChildren().add(lblNom);
            }
        } else {
            Label lblOk = new Label("✅ Aucune dépendance bloquante.");
            lblOk.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            boxDependances.getChildren().add(lblOk);
        }

        VBox boxComposite = new VBox(5);


        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> detailStage.close());

        layout.getChildren().addAll(
                titre, dates, duree,
                boxDependances,
                boxComposite,
                lblDesc, description,
                btnFermer
        );

        Scene scene = new Scene(layout, 450, 750);
        detailStage.setScene(scene);
        detailStage.show();
    }
}