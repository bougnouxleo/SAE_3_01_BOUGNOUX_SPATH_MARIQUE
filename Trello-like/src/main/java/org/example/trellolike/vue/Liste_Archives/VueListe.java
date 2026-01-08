package org.example.trellolike.vue.Liste_Archives;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.example.trellolike.modele.Sujet;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.example.trellolike.modele.Projet;
import org.example.trellolike.controlleur.ListeController;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.vue.Observateur;

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

        //Barre Recherche + Filtres
        HBox barreOutils = new HBox(10);
        barreOutils.setPadding(new Insets(10));
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une tâche...");
        searchField.setPrefWidth(300);

        ComboBox<String> comboPriorite = new ComboBox<>();
        comboPriorite.getItems().addAll("Toutes les priorités", "Urgente", "Haute", "Moyenne", "Basse");
        comboPriorite.setValue("Toutes les priorités");

        // Écouteurs pour mettre à jour le filtrage en temps réel
        searchField.textProperty().addListener((obs, old, nouveau) ->
                controller.mettreAJourFiltres(nouveau, comboPriorite.getValue()));

        comboPriorite.valueProperty().addListener((obs, old, nouveau) ->
                controller.mettreAJourFiltres(searchField.getText(), nouveau));

        // Bouton de tri par durée
        Button btnTriDuree = new Button("Tri Durée : ↕");
        btnTriDuree.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd;");

        btnTriDuree.setOnAction(e -> {
            controller.mettreAJourTriDuree();

            // Mise à jour visuelle du bouton
            int etat = controller.getEtatTriDuree();
            if (etat == 0) btnTriDuree.setText("Tri Durée : ↕");
            else if (etat == 1) btnTriDuree.setText("Tri Durée : ↑ (Croissant)");
            else btnTriDuree.setText("Tri Durée : ↓ (Décroissant)");
        });

        barreOutils.getChildren().addAll(new Label("🔍"), searchField,new Label("Priorité :") ,comboPriorite, btnTriDuree);

        // Configuration du ScrollPane
        this.setFitToWidth(true);
        this.setStyle("-fx-background-color: white;");

        // Conteneur interne vertical
        this.conteneurPrincipal = new VBox();
        this.conteneurPrincipal.setPadding(new Insets(20));
        this.conteneurPrincipal.setSpacing(20); // Espace entre les blocs "Jours"
        this.setContent(conteneurPrincipal);

        //Layout principal avec la barre d'outils en haut
        VBox layoutPrincipal = new VBox();
        layoutPrincipal.getChildren().addAll(barreOutils, conteneurPrincipal);
        this.setContent(layoutPrincipal);

        // Abonnement
        this.projet.enregistrerObservateur(this);

        // Premier affichage
        this.actualiser(projet);
    }

    /**
     * Mise à jour de l'affichage
     * @param s le projet
     */
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