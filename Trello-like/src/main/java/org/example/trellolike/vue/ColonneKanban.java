package org.example.trellolike.vue;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ColonneKanban extends VBox {
    public ColonneKanban(String titre) {
        this.setMinWidth(250);
        this.setStyle("-fx-background-color: white; -fx-padding: 10;");
        Label lbl = new Label(titre);
        lbl.setStyle("-fx-font-weight: bold;");
        this.getChildren().add(lbl);
    }

    public void ajouterCarte(CarteTache carte) {
        this.getChildren().add(carte);
    }
}