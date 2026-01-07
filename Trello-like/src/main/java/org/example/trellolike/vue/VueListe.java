package org.example.trellolike.vue;

import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.example.trellolike.Sujet;

import javafx.application.Platform;
import javafx.geometry.Insets;
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

        // 1. On vide l'affichage précédent
        conteneurPrincipal.getChildren().clear();

        // 2. Récupération des données via le contrôleur
        Map<LocalDate, List<Tache>> tachesParJour = controller.getTachesGroupeesParJour();

        // 3. Reconstruction de l'interface
        for (Map.Entry<LocalDate, List<Tache>> entry : tachesParJour.entrySet()) {
            LocalDate date = entry.getKey();
            List<Tache> tachesDuJour = entry.getValue();

            // Titre du jour
            Label titreJour = new Label(controller.getNomJour(date) + " " + date);
            titreJour.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");

            // Liste verticale pour ce jour (Conteneur des tâches)
            VBox listeDuJour = new VBox();
            listeDuJour.setSpacing(5); // Un peu d'espace entre les tâches
            listeDuJour.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

            // Ajout des lignes de tâches
            for (Tache t : tachesDuJour) {
                LigneTache ligne = new LigneTache(t);

                // 1. Événements existants
                configurerEvenementsLigne(ligne, t);

                // 2. Événement suppression d'étiquette
                ligne.setOnEtiquetteSupprimee(etiquetteASupprimer -> {
                    controller.traiterSuppressionEtiquette(t, etiquetteASupprimer);
                });

                // CORRECTION 1 : Utiliser la bonne variable (listeDuJour et non groupeJour)
                listeDuJour.getChildren().add(ligne);
            }

            // CORRECTION 2 : Ajouter le tout au conteneur principal
            // Sinon, rien ne s'affiche à l'écran !
            conteneurPrincipal.getChildren().addAll(titreJour, listeDuJour);
        }

        // Message si vide
        if (tachesParJour.isEmpty()) {
            Label vide = new Label("Aucune tâche planifiée.");
            vide.setStyle("-fx-text-fill: grey; -fx-padding: 20;");
            conteneurPrincipal.getChildren().add(vide);
        }
    }
    /**
     * Gère les clics (Gauche pour détails, Droit pour menu)
     */
    private void configurerEvenementsLigne(LigneTache ligne, Tache t) {
        // 1. Double clic -> Détails
        ligne.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) controller.traiterOuvertureDetail(t);
        });

        // 2. Clic Droit -> Menu Contextuel (Ajouter Etiquette)
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemAjoutTag = new MenuItem("Ajouter une étiquette...");

        itemAjoutTag.setOnAction(e -> afficherDialogCreationEtiquette(t)); // Appelle la boite de dialogue

        contextMenu.getItems().add(itemAjoutTag);

        // Attache le menu à la ligne
        ligne.setOnContextMenuRequested(e ->
                contextMenu.show(ligne, e.getScreenX(), e.getScreenY())
        );
    }

    /**
     * Affiche la boite de dialogue (Code identique à VueTableau)
     */
    private void afficherDialogCreationEtiquette(Tache tacheCible) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Étiquette");
        dialog.setHeaderText("Étiquette pour : " + tacheCible.getNom());

        ButtonType btnValider = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtNom = new TextField();
        txtNom.setPromptText("Nom (ex: Urgent)");
        ColorPicker colorPicker = new ColorPicker(Color.ORANGE);

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Couleur :"), 0, 1);
        grid.add(colorPicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                Color c = colorPicker.getValue();
                // Conversion Hex
                String hex = String.format("#%02X%02X%02X",
                        (int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255));

                // Appel au contrôleur
                controller.traiterAjoutEtiquette(tacheCible, txtNom.getText(), hex);
            }
            return null;
        });

        dialog.showAndWait();
    }
}