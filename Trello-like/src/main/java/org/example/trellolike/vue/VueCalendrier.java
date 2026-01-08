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
    private Projet projet;
    private CalendarController controller;
    private YearMonth moisActuel;
    private GridPane grille;
    private Label labelMois;

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

        ScrollPane scroll = new ScrollPane(grille);
        scroll.setFitToWidth(true);
        this.setCenter(scroll);

        actualiser(projet);
    }

    private HBox creerHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #3498db;");

        Button btnPrec = new Button("◀");
        Button btnSuiv = new Button("▶");
        labelMois = new Label();
        labelMois.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        btnPrec.setOnAction(e -> { moisActuel = moisActuel.minusMonths(1); actualiser(projet); });
        btnSuiv.setOnAction(e -> { moisActuel = moisActuel.plusMonths(1); actualiser(projet); });

        header.getChildren().addAll(btnPrec, labelMois, btnSuiv);
        return header;
    }

    @Override
    public void actualiser(Sujet s) {
        grille.getChildren().clear();
        labelMois.setText(moisActuel.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE) + " " + moisActuel.getYear());

        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label lbl = new Label(jours[i]);
            lbl.setMinWidth(100);
            lbl.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            grille.add(lbl, i, 0);
        }

        List<Tache> taches = projet.obtenirToutesLesTaches();
        LocalDate premier = moisActuel.atDay(1);
        int decalage = premier.getDayOfWeek().getValue() - 1;

        for (int jour = 1; jour <= moisActuel.lengthOfMonth(); jour++) {
            LocalDate date = moisActuel.atDay(jour);
            VBox cellule = new VBox(3);
            cellule.setPadding(new Insets(5));
            cellule.setMinSize(100, 80);
            cellule.setStyle("-fx-border-color: #ccc; -fx-background-color: white;");

            Label num = new Label(String.valueOf(jour));
            num.setStyle("-fx-font-weight: bold;");
            cellule.getChildren().add(num);

            for (Tache t : taches) {
                try {
                    if (t.getDateFin() != null && LocalDate.parse(t.getDateFin()).equals(date)) {
                        Label lblT = new Label(t.getNom());
                        lblT.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 2; -fx-font-size: 10;");
                        cellule.getChildren().add(lblT);
                    }
                } catch (Exception ignored) {}
            }

            grille.add(cellule, (decalage + jour - 1) % 7, (decalage + jour - 1) / 7 + 1);
        }
    }
}