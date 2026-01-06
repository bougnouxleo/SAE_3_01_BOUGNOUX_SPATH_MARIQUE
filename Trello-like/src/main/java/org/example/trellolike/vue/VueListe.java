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
        // Vérification de sécurité
        if (!(s instanceof Projet)) return;


        // 1. On vide l'affichage
        conteneurPrincipal.getChildren().clear();

        // 2. Récupération des données via le contrôleur
        Map<LocalDate, List<Tache>> tachesParJour = controller.getTachesGroupeesParJour();

        // 3. Reconstruction de l'interface
        for (Map.Entry<LocalDate, List<Tache>> entry : tachesParJour.entrySet()) {
            LocalDate date = entry.getKey();
            List<Tache> tachesDuJour = entry.getValue();

            // Titre
            Label titreJour = new Label(controller.getNomJour(date));
            titreJour.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Liste verticale pour ce jour
            VBox listeDuJour = new VBox();
            listeDuJour.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

            // Ajout des lignes
            for (Tache t : tachesDuJour) {
                LigneTache ligne = new LigneTache(t);

                ligne.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) controller.traiterOuvertureDetail(t);
                });

                listeDuJour.getChildren().add(ligne);
            }

            conteneurPrincipal.getChildren().addAll(titreJour, listeDuJour);
        }

        // Message si vide
        if (tachesParJour.isEmpty()) {
            Label vide = new Label("Aucune tâche planifiée.");
            vide.setStyle("-fx-text-fill: grey; -fx-padding: 20;");
            conteneurPrincipal.getChildren().add(vide);
        }
    }
}