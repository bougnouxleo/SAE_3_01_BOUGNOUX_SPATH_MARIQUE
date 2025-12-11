package org.example.trellolike.vue;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import org.example.trellolike.Projet;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class ColonneKanban extends VBox {

    private ListeDeTache listeAssociee; // Le Modèle de cette colonne
    private Projet projetRef;           // Pour appeler la méthode deplacerTache

    public ColonneKanban(ListeDeTache liste, Projet projet) {
        this.listeAssociee = liste;
        this.projetRef = projet;

        // 1. Mise en forme de la colonne
        this.setSpacing(10);
        this.setMinWidth(250);
        this.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5;");

        // 2. En-tête (Nom de la liste)
        Label titre = new Label(liste.getNom());
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        this.getChildren().add(titre);

        // 3. Remplissage des tâches
        remplirTaches();

        // 4. Activation du Drag & Drop (Réception)
        configurerDragAndDrop();
    }

    private void remplirTaches() {
        // On parcourt les tâches contenues DANS cette liste spécifique
        for (Tache t : this.listeAssociee.getTaches()) {
            // On crée la carte graphique
            CarteTache carte = new CarteTache(t, this.listeAssociee);
            this.getChildren().add(carte);
        }
    }

    private void configurerDragAndDrop() {
        // Accepter le survol si on traîne quelque chose
        this.setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Gérer le lâcher (DROP)
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                // On récupère l'ID de la tâche
                int idTache = Integer.parseInt(db.getString());
                Tache tache = Tache.findById(idTache);

                // --- POINT CLÉ : Appel de votre méthode avec les 2 listes ---

                // Note : Il faut retrouver la liste source.
                // Soit la tâche connait sa liste (tache.getListeParente()),
                // soit on doit la chercher dans le projet.
                ListeDeTache listeSource = projetRef.trouverListeDeLaTache(tache);

                try {
                    // Appel au contrôleur/modèle
                    projetRef.deplacerTache(tache, listeSource, this.listeAssociee);
                    success = true;
                } catch (Exception e) {
                    // Gestion des erreurs (Tâche bloquée, etc.)
                    Alert alerte = new Alert(Alert.AlertType.WARNING, e.getMessage());
                    alerte.show();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}