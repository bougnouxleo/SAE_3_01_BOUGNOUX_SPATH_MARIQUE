package org.example.trellolike.vue;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class CarteTache extends VBox {

    private Tache tache;
    private ListeDeTache listeSource; // Utile pour savoir d'où on part

    public CarteTache(Tache tache, ListeDeTache listeSource) {
        this.tache = tache;
        this.listeSource = listeSource;

        // 1. STYLE VISUEL DE LA CARTE
        this.setPadding(new Insets(10));
        this.setSpacing(5);
        this.setMinWidth(200);
        this.setMaxWidth(250);

        // Style CSS "en dur" (l'idéal est un fichier .css à part)
        // Ombre portée + coins arrondis + fond blanc
        this.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        // 2. INDICATEUR DE COULEUR (ETIQUETTE)
        // Supposons que la tâche a une liste d'étiquettes, on prend la couleur de la première
        String couleurHex = "#3498db"; // Bleu par défaut
        if (!tache.getEtiquettes().isEmpty()) {
            couleurHex = tache.getEtiquettes().get(0).getCouleur();
        }
        Rectangle indicateur = new Rectangle(200, 5, Color.web(couleurHex));
        this.getChildren().add(indicateur);

        // 3. TITRE
        Label lblTitre = new Label(tache.getNom());
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblTitre.setWrapText(true); // Si le titre est long
        this.getChildren().add(lblTitre);

        // 4. RESPONSABLE & DATES
        HBox infos = new HBox(10);
        Label lblUser = new Label(tache.getUtilisateurAssigne().getPseudo());
        lblUser.setStyle("-fx-text-fill: grey; -fx-font-size: 10px;");
        infos.getChildren().add(lblUser);
        this.getChildren().add(infos);

//        // Si la tâche est bloquée, on change son aspect et on empêche le Drag
//        if (tache.estBloquee()) {
//            configurerVisuelBloque();
//        } else {
//            // Si elle n'est pas bloquée, on active le Drag & Drop
//            configurerDragAndDrop();
//        }

        this.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                afficherDetails(); // Ouvre une fenêtre Popup
            }
        });
    }

    /**
     * Change le visuel pour montrer que la tâche ne peut pas bouger
     */
    private void configurerVisuelBloque() {
        this.setStyle("-fx-background-color: #e0e0e0; " + // Gris
                "-fx-background-radius: 5; " +
                "-fx-border-color: red; " +
                "-fx-border-width: 1;");

        Label lblBloque = new Label("🔒 BLOQUÉE (Dépendance)");
        lblBloque.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 10px;");
        this.getChildren().add(lblBloque);
    }

    /**
     * Active la capacité à être déplacée
     */
    private void configurerDragAndDrop() {
        // Détection du début du glisser
        this.setOnDragDetected(event -> {
            // On autorise le mouvement
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);

            // On met l'ID de la tâche dans le "presse-papier" du drag
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(Tache.getId()));
            db.setContent(content);

            // Optionnel : Mettre une image fantôme sous la souris
            // db.setDragView(this.snapshot(null, null));

            event.consume();
        });

        // Effet visuel quand on termine (réussi ou raté)
        this.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                // Le déplacement a réussi, la vue Kanban va se rafraîchir via l'Observer
            }
        });
    }

    private void afficherDetails() {
        // Appelle une classe VueDetailTache (nouvelle fenêtre Stage ou Dialog)
        // VueDetailTache dialogue = new VueDetailTache(this.tache);
        // dialogue.show();
        System.out.println("Affichage des détails pour : " + tache.getNom());
    }
}