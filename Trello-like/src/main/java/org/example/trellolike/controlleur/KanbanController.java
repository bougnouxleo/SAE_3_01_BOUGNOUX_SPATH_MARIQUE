package org.example.trellolike.controlleur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.trellolike.Projet;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class KanbanController {

    private Projet projet;

    public KanbanController(Projet projet) {
        this.projet = projet;
    }

    // ... (méthode traiterDepotTache existante) ...

    /**
     * Vérifie si une tâche a le droit d'être déplacée.
     * Règle métier : Une tâche ne peut pas démarrer si elle dépend d'une tâche non finie.
     * [Source 42] du sujet.
     */
    public boolean verifierDroitDeplacer(Tache t) {
        // 1. Si la tâche est bloquée (dépendance non terminée)
        if (t.estBloquee()) {
            // On peut afficher une petite alerte ou juste empêcher le drag
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Action impossible");
            alert.setHeaderText("Tâche bloquée !");
            alert.setContentText("Vous ne pouvez pas déplacer '" + t.getNom() +
                    "' car elle dépend d'une tâche non terminée.");
            alert.show();

            return false; // Interdit le début du Drag & Drop
        }

        // 2. Si la tâche est archivée, on évite aussi de la bouger
        // (Optionnel selon votre logique)

        return true; // Autorise le mouvement
    }

    /**
     * Ouvre une fenêtre popup (Stage) pour afficher les détails.
     * [Source 45] : "visualisée après sélection avec la souris"
     */
    public void traiterOuvertureDetail(Tache t) {
        // Création d'une nouvelle fenêtre (Stage)
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre principale
        detailStage.setTitle("Détails : " + t.getNom());

        // --- Construction de la Vue Détail (simplifiée ici en code) ---
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        // 1. Titre
        Label titre = new Label(t.getNom());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 2. Description
        Label lblDesc = new Label("Description :");
        TextArea description = new TextArea(t.getDescription());
        description.setEditable(false); // Lecture seule pour l'instant
        description.setWrapText(true);
        description.setMaxHeight(100);

        // 3. Dates et Durée
        Label dates = new Label("Début : " + t.getDateDebut() + " | Fin : " + t.getDateFin());
        Label duree = new Label("Durée estimée : " + t.getDuree() + "h");

        // 4. Dépendances (Information critique)
        Label depInfo;
        if (t.estBloquee()) {
            depInfo = new Label("⚠️ BLOQUÉE par des dépendances non terminées.");
            depInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            depInfo = new Label("✅ Aucune dépendance bloquante.");
            depInfo.setStyle("-fx-text-fill: green;");
        }

        // 5. Bouton Archiver [Source 43]
        Button btnArchiver = new Button("Archiver la tâche");
        btnArchiver.setOnAction(e -> {
            // Action d'archivage (Changement d'état + Sauvegarde)
            // Note: Il faudrait idéalement une méthode 'archiverTache' dans Projet
            t.setArchivee(true); // Supposons un boolean ou un Etat.ARCHIVE
            projet.sauvegarderGlobalement();
            detailStage.close();
        });

        // Si la tâche n'est pas finie, on désactive souvent l'archivage
        // btnArchiver.setDisable(!t.estTerminee());

        // Bouton Fermer
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> detailStage.close());

        // Assemblage
        layout.getChildren().addAll(titre, dates, duree, depInfo, lblDesc, description, btnArchiver, btnFermer);

        Scene scene = new Scene(layout, 400, 500);
        detailStage.setScene(scene);
        detailStage.show();
    }

    /**
     * Gère l'événement de "Lâcher" (Drop) d'une tâche dans une nouvelle colonne.
     * @param idTache L'identifiant unique de la tâche déplacée.
     * @param listeDestination La liste (colonne) où la tâche a été lâchée.
     */
    public void traiterDepotTache(int idTache, ListeDeTache listeDestination) {
        // 1. Récupération de l'objet Tache depuis l'ID (recherche en mémoire)
        Tache tache = Tache.findById(idTache);

        if (tache == null) {
            System.err.println("Erreur : Tâche introuvable pour l'ID " + idTache);
            return;
        }

        // 2. Identification de la liste d'origine (Source)
        // On a besoin de savoir d'où elle vient pour la retirer de là-bas
        ListeDeTache listeSource = projet.trouverListeDeLaTache(tache);

        if (listeSource == null) {
            System.err.println("Erreur : Impossible de trouver la liste source de la tâche.");
            return;
        }

        // 3. Optimisation : Si on lâche dans la même colonne, on ne fait rien
        if (listeSource == listeDestination) {
            return;
        }

        // 4. Tentative de déplacement (Logique Métier + Sauvegarde)
        try {
            // A. On appelle le modèle pour effectuer le déplacement
            // C'est cette méthode qui vérifiera si la tâche est bloquée par une dépendance
            projet.deplacerTache(tache, listeSource, listeDestination);

            // B. Si aucune exception n'est levée, on sauvegarde le tout (XML)
            projet.sauvegarderGlobalement();

            // Note : Pas besoin d'appeler manuellement actualiser(),
            // car deplacerTache() ou sauvegarderGlobalement() déclenche notifierObservateurs()

        } catch (Exception e) {
            // C. Gestion des erreurs métier (Ex: Tâche bloquée)
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Déplacement impossible");
            alert.setHeaderText("Opération annulée");
            alert.setContentText(e.getMessage()); // Affiche "La tâche est bloquée..."
            alert.showAndWait();
        }
    }
}