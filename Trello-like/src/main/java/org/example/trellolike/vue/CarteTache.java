package org.example.trellolike.vue;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.trellolike.tache.Tache;

/**
 * Composante graphique
 */
public class CarteTache extends VBox {
    public CarteTache(Tache t) {
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: white;-fx-border-color: black; -fx-effect: dropshadow('...');");

        this.getChildren().add(new Label(t.getNom()));

        // Logique purement visuelle (Présentation)
        if (t.estBloquee()) {
            this.setStyle("-fx-background-color: #eee; -fx-border-color: red;");
            this.getChildren().add(new Label("🔒"));
        }
    }
}