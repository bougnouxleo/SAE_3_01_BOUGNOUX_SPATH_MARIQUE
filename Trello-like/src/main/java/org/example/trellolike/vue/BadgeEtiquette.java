package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.example.trellolike.Etiquette;

public class BadgeEtiquette extends Label {

    /**
     * Constructeur de la classe BadgeEtiquette
     * @param e
     */
    public BadgeEtiquette(Etiquette e) {
        super(e.getNom());

        // Calcul de la couleur de texte (Blanc ou Noir) selon la couleur de fond
        String bgHex = e.getCodeCouleur();
        String textFill = estCouleurFoncee(bgHex) ? "white" : "black";

        this.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;",
                bgHex, textFill
        ));

        this.setPadding(new Insets(2, 5, 2, 5));
    }

    // Petite astuce pour savoir si le texte doit être blanc ou noir
    private boolean estCouleurFoncee(String hexColor) {
        Color c = Color.web(hexColor);
        // Formule de luminosité
        double brightness = c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114;
        return brightness < 0.5;
    }
}