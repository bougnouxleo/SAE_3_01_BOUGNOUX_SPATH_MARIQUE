package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.example.trellolike.tache.Tache;

public class LigneTache extends HBox {

    public LigneTache(Tache t) {
        // Style global de la ligne
        this.setPadding(new Insets(10));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;"); // Bordure en bas seulement

        // 1. Checkbox (Statut fini ou non)
        CheckBox chkFini = new CheckBox();
        // 2. Nom de la tâche
        Label lblNom = new Label(t.getNom());
        lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        // 3. Espace flexible (pousse le reste à droite)
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 4. Date (Échéance)
        Label lblDate = new Label(t.getDateFin() != null ? t.getDateFin() : "--");
        lblDate.setPrefWidth(100);

        // 5. Étiquette (Type de travail - Simulation visuelle basée sur la photo)
        Label lblTag = new Label(t.getDureeTotale()+"J");
        lblTag.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-padding: 3 8; -fx-background-radius: 10;");

        // Assemblage
        this.getChildren().addAll(chkFini, lblNom, spacer, lblDate, lblTag);

        // Effet Hover (survol souris)
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;"));
    }
}