package org.example.trellolike.vue;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.example.trellolike.Sujet;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.ListeController;
import org.example.trellolike.tache.Tache;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// On hérite de ScrollPane car la liste peut être longue
public class VueListe extends ScrollPane implements Observateur {

    private Projet projet;
    private ListeController controller;
    private VBox conteneurPrincipal; // Contient toutes les sections jours

    public VueListe(Projet projet) {
        this.projet = projet;
        this.controller = new ListeController(projet); // Le contrôleur logique

        // Configuration du ScrollPane
        this.setFitToWidth(true);
        this.setStyle("-fx-background-color: white;");

        // Conteneur interne vertical
        this.conteneurPrincipal = new VBox();
        this.conteneurPrincipal.setPadding(new Insets(20));
        this.conteneurPrincipal.setSpacing(20); // Espace entre les blocs "Jours"
        this.setContent(conteneurPrincipal);

        // Abonnement
        this.projet.enregistrerObservateur(this);

        // Premier affichage
        this.actualiser(projet);
    }

    @Override
    public void actualiser(Sujet s) {
        if (!(s instanceof Projet)) return;

        Platform.runLater(() -> {
            conteneurPrincipal.getChildren().clear();

            // 1. Récupération des tâches groupées par jour via le Controller
            Map<LocalDate, List<Tache>> tachesParJour = controller.getTachesGroupeesParJour();

            // 2. Création de l'affichage pour chaque jour
            for (Map.Entry<LocalDate, List<Tache>> entry : tachesParJour.entrySet()) {
                LocalDate date = entry.getKey();
                  List<Tache> tachesDuJour = entry.getValue();

                // A. Titre du jour (ex: "Lundi")
                Label titreJour = new Label(controller.getNomJour(date));
                titreJour.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

                // B. Conteneur pour les tâches de ce jour
                VBox listeDuJour = new VBox();
                listeDuJour.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");

                // C. Création des lignes
                for (Tache t : tachesDuJour) {
                    LigneTache ligne = new LigneTache(t);

                    // Interaction : Double clic pour voir les détails
                    ligne.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) controller.traiterOuvertureDetail(t);
                    });

                    listeDuJour.getChildren().add(ligne);
                }

                // Ajout au conteneur principal
                conteneurPrincipal.getChildren().addAll(titreJour, listeDuJour);
            }

            // Gestion du cas vide
            if (tachesParJour.isEmpty()) {
                conteneurPrincipal.getChildren().add(new Label("Aucune tâche planifiée avec une date."));
            }
        });


    }
}