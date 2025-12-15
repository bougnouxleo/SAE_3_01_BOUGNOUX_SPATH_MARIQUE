package org.example.trellolike.vue;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;

import java.util.Optional;

public class ColonneKanban extends VBox {
    /**
     * Conteneur des tâches à l'intérieur de la colonne
     */
    private VBox boxTaches; // Conteneur pour les cartes (séparé du bouton)
    /**
     * Modèle de la liste de tâches associée à cette colonne
     */
    private ListeDeTache listeModele;
    /**
     * Contrôleur pour gérer les interactions
     */
    private KanbanController controller;

    /**
     * Constructeur de la colonne Kanban
     * @param liste la liste de tâches associée
     * @param controller le contrôleur pour gérer les interactions
     */
    public ColonneKanban(ListeDeTache liste, KanbanController controller) {
        this.listeModele = liste;
        this.controller = controller;

        this.setMinWidth(250);
        this.setSpacing(10);
        this.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1px;");

        Label lbl = new Label(liste.getNom());
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        this.getChildren().add(lbl);

        this.boxTaches = new VBox();
        this.boxTaches.setSpacing(10);
        VBox.setVgrow(boxTaches, Priority.ALWAYS);
        this.getChildren().add(boxTaches);

        Button btnAjout = new Button("+ Ajouter une tâche");
        btnAjout.setMaxWidth(Double.MAX_VALUE);
        btnAjout.setOnAction(e -> demanderNouvelleTache());

        this.getChildren().add(btnAjout);
    }

    /**
     * Ajoute une carte de tâche à la colonne
     * @param carte la carte de tâche à ajouter
     */
    public void ajouterCarte(CarteTache carte) {
        this.boxTaches.getChildren().add(carte);
    }

    /**
     * Demande à l'utilisateur le nom d'une nouvelle tâche et informe le contrôleur
     */
    private void demanderNouvelleTache() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setHeaderText("Ajouter dans : " + listeModele.getNom());
        dialog.setContentText("Nom de la tâche :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nom -> {
            controller.traiterAjoutTache(nom, listeModele);
        });
    }
}