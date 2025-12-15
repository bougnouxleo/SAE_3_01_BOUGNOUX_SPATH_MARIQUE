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
import org.example.trellolike.tache.TacheSimple;
import java.time.LocalDate;
import java.util.List;

public class KanbanController {
    /**
     * Le projet associé au Kanban
     */
    private Projet projet;

    /**
     * Constructeur du KanbanController
     * @param projet le projet associé
     */
    public KanbanController(Projet projet) {
        this.projet = projet;
    }

    /**
     * Gère l'ajout d'une nouvelle tâche dans une liste donnée.
     * @param nom le nom de la tâche à ajouter
     * @param listeDest la liste de tâches destination
     */
    public void traiterAjoutTache(String nom, String description, LocalDate dateDebut, LocalDate dateFin, ListeDeTache listeDest, List<Tache> lesDependances, boolean estComposite, int dureeEstimee) {
        if (nom == null || nom.trim().isEmpty()) return;
        String strDebut = (dateDebut != null) ? dateDebut.toString() : "";
        String strFin = (dateFin != null) ? dateFin.toString() : "";

        Tache nouvelleTache;

        if (estComposite) {
            nouvelleTache = new TacheComposite(nom, description, strDebut, strFin);
        } else {
            nouvelleTache = new TacheSimple(nom, description, strDebut, strFin, dureeEstimee);
        }

        // Gestion commune des dépendances (grâce à la classe mère Tache)
        if (lesDependances != null) {
            for (Tache dep : lesDependances) {
                nouvelleTache.ajouterDependance(dep);
            }
        }

        listeDest.ajouterTache(nouvelleTache);
        projet.sauvegarderGlobalement();
    }

    /**
     * Vérifie si une tâche peut être déplacée (non bloquée).
     * @param t la tâche à vérifier
     * @return true si la tâche peut être déplacée, false sinon
     */
    public boolean verifierDroitDeplacer(Tache t) {
        if (t.estBloquee()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Action impossible");
            alert.setHeaderText("Tâche bloquée !");
            alert.setContentText("Vous ne pouvez pas déplacer '" + t.getNom() +
                    "' car elle dépend d'une tâche non terminée.");
            alert.show();
            return false;
        }
        return true;
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

        Label lblDesc = new Label("Description :");
        TextArea description = new TextArea(t.getDescription());
        description.setEditable(false);
        description.setWrapText(true);
        description.setMaxHeight(100);

        Label dates = new Label("Début : " + t.getDateDebut() + " | Fin : " + t.getDateFin());
        Label duree = new Label("Durée estimée : " + t.getDureeTotale() + "h");

        Label depInfo;
        if (t.estBloquee()) {
            depInfo = new Label("⚠️ BLOQUÉE par des dépendances non terminées.");
            depInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            depInfo = new Label("✅ Aucune dépendance bloquante.");
            depInfo.setStyle("-fx-text-fill: green;");
        }

        Button btnArchiver = new Button("Archiver la tâche");
        btnArchiver.setOnAction(e -> {
            t.setArchivee(true);
            projet.sauvegarderGlobalement();
            detailStage.close();
        });

        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> detailStage.close());

        layout.getChildren().addAll(titre, dates, duree, depInfo, lblDesc, description, btnArchiver, btnFermer);

        Scene scene = new Scene(layout, 400, 500);
        detailStage.setScene(scene);
        detailStage.show();
    }

    /**
     * Gère le dépôt d'une tâche dans une nouvelle liste.
     * @param idTache l'identifiant de la tâche à déplacer
     * @param listeDestination la liste de tâches destination
     */
    public void traiterDepotTache(int idTache, ListeDeTache listeDestination) {
        Tache tache = Tache.findById(idTache);
        if (tache == null) return;

        ListeDeTache listeSource = projet.trouverListeDeLaTache(tache);
        if (listeSource == null || listeSource == listeDestination) return;

        try {
            projet.deplacerTache(tache, listeSource, listeDestination);
            projet.sauvegarderGlobalement();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Déplacement impossible");
            alert.setHeaderText("Opération annulée");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}