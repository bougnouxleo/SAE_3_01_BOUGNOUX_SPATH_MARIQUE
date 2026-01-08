package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.CalendarController;
import org.example.trellolike.tache.Tache;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class VueCalendrier extends BorderPane implements Observateur {
    /**
     * Projet associé à la vue
     */
    private Projet projet;
    /**
     * Controller associé à la vue
     */
    private CalendarController controller;
    /**
     * Mois actuellement affiché
     */
    private YearMonth moisActuel;
    /**
     * Grille du calendrier
     */
    private GridPane grille;
    /**
     * Label affichant le mois et l'année
     */
    private Label labelMois;

    /**
     * Constructeur de la vue Calendrier
     * @param projet
     * @param controller
     */
    public VueCalendrier(Projet projet, CalendarController controller) {
        this.projet = projet;
        this.controller = controller;
        this.moisActuel = YearMonth.now();
        this.projet.enregistrerObservateur(this);

        this.setTop(creerHeader());

        this.grille = new GridPane();
        grille.setHgap(2);
        grille.setVgap(2);
        grille.setPadding(new Insets(10));
        grille.setAlignment(Pos.CENTER);

        // Pour centrer la grille dans le ScrollPane
        StackPane conteneurCentrage = new StackPane(grille);
        conteneurCentrage.setAlignment(Pos.CENTER);
        conteneurCentrage.setStyle("-fx-background-color: white;");

        // ScrollPane pour permettre le défilement si nécessaire
        ScrollPane scroll = new ScrollPane(grille);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        this.setCenter(scroll);

        actualiser(projet);
    }

    /**
     * Crée l'en-tête du calendrier avec les boutons de navigation
     * @return HBox contenant l'en-tête
     */
    private HBox creerHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #3498db;");

        // Boutons de navigation
        Button btnPrec = new Button("◀");
        Button btnSuiv = new Button("▶");
        labelMois = new Label();
        labelMois.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Actions des boutons
        btnPrec.setOnAction(e -> { moisActuel = moisActuel.minusMonths(1); actualiser(projet); });
        btnSuiv.setOnAction(e -> { moisActuel = moisActuel.plusMonths(1); actualiser(projet); });

        header.getChildren().addAll(btnPrec, labelMois, btnSuiv);
        return header;
    }

    /**
     * Actualise l'affichage du calendrier
     * @param s le sujet observé
     */
    @Override
    public void actualiser(Sujet s) {
        grille.getChildren().clear();
        labelMois.setText(moisActuel.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE) + " " + moisActuel.getYear());

        // Ajout des jours de la semaine
        List<Tache> taches = projet.obtenirToutesLesTaches();
        LocalDate premier = moisActuel.atDay(1);
        int decalage = premier.getDayOfWeek().getValue() - 1;

        for (int jour = 1; jour <= moisActuel.lengthOfMonth(); jour++) {
            LocalDate date = moisActuel.atDay(jour);

            // La cellule principale reste une VBox
            VBox cellule = new VBox(5);
            cellule.setPadding(new Insets(5));
            cellule.setMinSize(120, 100); // Taille minimum stable
            cellule.setMaxWidth(Double.MAX_VALUE);
            cellule.setStyle("-fx-border-color: #ccc; -fx-background-color: white;");

            Label num = new Label(String.valueOf(jour));
            num.setStyle("-fx-font-weight: bold;");
            cellule.getChildren().add(num);

            // Création d'un conteneur spécifique pour les tâches
            VBox tasksBox = new VBox(3);
            tasksBox.setFillWidth(true);

            // Ajout des tâches terminées à cette date
            for (Tache t : taches) {
                try {
                    if (t.getDateFin() != null && LocalDate.parse(t.getDateFin()).equals(date)) {
                        Label lblT = new Label(t.getNom());
                        lblT.setMaxWidth(Double.MAX_VALUE); // Prend toute la largeur
                        lblT.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                                "-fx-padding: 2; -fx-font-size: 10; -fx-background-radius: 2;");
                        lblT.setOnMouseClicked(e -> controller.traiterOuvertureDetail(t));
                        tasksBox.getChildren().add(lblT);
                    }
                } catch (Exception ignored) {}
            }

            // Encapsulation dans un ScrollPane interne pour éviter le débordement
            ScrollPane cellScroll = new ScrollPane(tasksBox);
            cellScroll.setFitToWidth(true);
            cellScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Pas de scroll horizontal
            cellScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Scroll vertical si besoin

            // Supprimer le fond et la bordure du ScrollPane interne pour la discrétion
            cellScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
            cellScroll.setPrefHeight(80); // Hauteur suggérée pour la zone de tâches

            cellule.getChildren().add(cellScroll);
            VBox.setVgrow(cellScroll, Priority.ALWAYS); // Le scroll prend toute la place restante

            grille.add(cellule, (decalage + jour - 1) % 7, (decalage + jour - 1) / 7 + 1);
        }
    }
}