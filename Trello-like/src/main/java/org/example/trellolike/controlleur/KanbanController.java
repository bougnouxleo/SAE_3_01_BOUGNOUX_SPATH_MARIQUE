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
import java.util.ArrayList;
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

        Label dates = new Label("Début : " + t.getDateDebut() + " | Fin : " + t.getDateFin());

        Label duree = new Label("Durée estimée : " + t.getDureeTotale() + "h");
        duree.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");

        Label lblDesc = new Label("Description :");
        TextArea description = new TextArea(t.getDescription());
        description.setEditable(true);
        description.setWrapText(true);
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

        if (t instanceof TacheComposite) {
            TacheComposite composite = (TacheComposite) t;

            Label lblSousTaches = new Label("Tâches dépendantes :");
            lblSousTaches.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

            ListView<Tache> listSelectionEnfants = new ListView<>();
            listSelectionEnfants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listSelectionEnfants.setMaxHeight(120);

            List<Tache> candidats = new ArrayList<>();
            for (ListeDeTache liste : projet.getListeDeTaches()) {
                candidats.addAll(liste.getTaches());
            }
            candidats.remove(t);

            listSelectionEnfants.getItems().addAll(candidats);

            for (Tache sousTache : composite.getSousTaches()) {
                listSelectionEnfants.getSelectionModel().select(sousTache);
            }

            Button btnValiderCompo = new Button("Mettre à jour les sous-tâches");
            btnValiderCompo.setOnAction(e -> {
                List<Tache> selection = listSelectionEnfants.getSelectionModel().getSelectedItems();
                composite.setSousTaches(new ArrayList<>(selection));

                duree.setText("Durée estimée : " + t.getDureeTotale() + "h");

                projet.sauvegarderGlobalement();
            });

            boxComposite.getChildren().addAll(lblSousTaches, listSelectionEnfants, btnValiderCompo);
        }

        Button btnSaveDesc = new Button("Sauvegarder Description");
        btnSaveDesc.setOnAction(e -> {
            t.setDescription(description.getText());
            projet.sauvegarderGlobalement();
        });

        Button btnArchiver = new Button("Archiver la tâche");
        btnArchiver.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
        btnArchiver.setOnAction(e -> {
            projet.archiverTache(t);
            detailStage.close();
        });

        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> detailStage.close());

        layout.getChildren().addAll(
                titre, dates, duree,
                boxDependances,
                boxComposite,
                lblDesc, description, btnSaveDesc,
                btnArchiver, btnFermer
        );

        Scene scene = new Scene(layout, 450, 750);
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