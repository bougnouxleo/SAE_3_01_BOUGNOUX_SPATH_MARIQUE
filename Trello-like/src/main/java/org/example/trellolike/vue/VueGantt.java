package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.GanttController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class VueGantt extends ScrollPane implements Observateur {

    private Projet projet;
    private GanttController controller;
    private GridPane grid;

    private final int LARGEUR_JOUR = 40;
    private final int HAUTEUR_BARRE = 30;

    public VueGantt(Projet projet, GanttController controller) {
        this.projet = projet;
        this.controller = controller;
        this.projet.enregistrerObservateur(this);

        this.grid = new GridPane();
        this.grid.setHgap(0);
        this.grid.setVgap(10);
        this.grid.setPadding(new Insets(20));
        this.grid.setStyle("-fx-background-color: white;");

        this.setContent(grid);
        this.setFitToWidth(true);
        this.setPannable(true);

        actualiser(projet);
    }

    /**
     * Méthode pour actualiser la vue Gantt
     * @param s le projet
     */
    @Override
    public void actualiser(Sujet s) {
        grid.getChildren().clear();

        List<Tache> toutesLesTaches = obtenirToutesLesTaches();
        if (toutesLesTaches.isEmpty()) {
            grid.add(new Label("Aucune tâche à afficher."), 0, 0);
            return;
        }

        LocalDate dateDebutProjet = calculerDateMin(toutesLesTaches);
        creerEnTete(dateDebutProjet);

        int indexLigne = 1;
        for (Tache t : toutesLesTaches) {
            if (t.getDateDebut() == null || t.getDateFin() == null || t.getDateDebut().isEmpty()) {
                continue;
            }

            try {
                LocalDate debut = LocalDate.parse(t.getDateDebut());
                LocalDate fin = LocalDate.parse(t.getDateFin());

                long decalageJours = ChronoUnit.DAYS.between(dateDebutProjet, debut);
                long dureeJours = ChronoUnit.DAYS.between(debut, fin) + 1;

                // 1. Création de la barre (Fond)
                Rectangle barre = new Rectangle(dureeJours * LARGEUR_JOUR, HAUTEUR_BARRE);
                barre.setArcHeight(10);
                barre.setArcWidth(10);
                //barre.setFill(determinerCouleur(t)); // Utilisation de la méthode de couleur

                // 2. Création du Label (Texte sur la barre)
                Label lblNom = new Label(t.getNom());
                lblNom.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
                lblNom.setMaxWidth(dureeJours * LARGEUR_JOUR - 10); // Empêche le texte de déborder
                lblNom.setEllipsisString("..."); // Ajoute des points si le nom est trop long

                // 3. Superposition dans un StackPane
                StackPane stack = new StackPane();
                stack.getChildren().addAll(barre, lblNom);
                stack.setAlignment(Pos.CENTER); // Centre le texte sur la barre

                // Effet de survol
                stack.setOnMouseEntered(e -> stack.setOpacity(0.8));
                stack.setOnMouseExited(e -> stack.setOpacity(1.0));

                // 4. Positionnement avec le décalage temporel
                HBox ligneTemps = new HBox();
                Region spacer = new Region();
                spacer.setMinWidth(decalageJours * LARGEUR_JOUR);
                ligneTemps.getChildren().addAll(spacer, stack);

                // On ajoute tout dans la colonne 0 car on n'a plus besoin de la colonne de noms
                grid.add(ligneTemps, 0, indexLigne);

                indexLigne++;
            } catch (Exception e) {
                // Ignore les erreurs de format de date
            }
        }
    }

    private void creerEnTete(LocalDate dateMin) {
        HBox bandeauDates = new HBox();
        for (int i = 0; i < 50; i++) {
            LocalDate jourActuel = dateMin.plusDays(i);
            Label lblJour = new Label(jourActuel.getDayOfMonth() + "/" + jourActuel.getMonthValue());
            lblJour.setMinWidth(LARGEUR_JOUR);
            lblJour.setPrefWidth(LARGEUR_JOUR);
            lblJour.setStyle("-fx-border-color: #ecf0f1; -fx-alignment: center; -fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
            bandeauDates.getChildren().add(lblJour);
        }
        grid.add(bandeauDates, 0, 0);
    }

    /*
    private Color determinerCouleur(Tache t) {
        if (projet.estTacheTerminee(t)) {
            return Color.web("#2ecc71"); // Vert
        }
        ListeDeTache liste = projet.trouverListeDeLaTache(t);
        if (liste != null && liste.getNom().equalsIgnoreCase("En Cours")) {
            return Color.web("#3498db"); // Bleu
        }
        return Color.web("#95a5a6"); // Gris
    }
     */

    private List<Tache> obtenirToutesLesTaches() {
        List<Tache> listeFinale = new ArrayList<>();
        for (ListeDeTache l : projet.getListes()) {
            listeFinale.addAll(l.getTaches());
        }
        return listeFinale;
    }

    private LocalDate calculerDateMin(List<Tache> taches) {
        LocalDate min = LocalDate.now();
        for (Tache t : taches) {
            try {
                if (t.getDateDebut() != null && !t.getDateDebut().isEmpty()) {
                    LocalDate d = LocalDate.parse(t.getDateDebut());
                    if (d.isBefore(min)) min = d;
                }
            } catch (Exception ignored) {}
        }
        return min.minusDays(2);
    }
}