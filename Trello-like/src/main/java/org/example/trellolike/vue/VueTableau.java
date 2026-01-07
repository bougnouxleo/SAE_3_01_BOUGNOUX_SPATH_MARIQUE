package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class VueTableau extends ScrollPane implements Observateur {

    private Projet projet;
    private KanbanController controller;
    private Button btnAjouterListe;

    private HBox conteneurColonnes;

    public VueTableau(Projet projet, KanbanController controller) {
        this.projet = projet;
        this.controller = controller;

        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setPannable(true);
        this.setStyle("-fx-background-color: #f4f4f4;");

        this.conteneurColonnes = new HBox();
        this.conteneurColonnes.setSpacing(20);
        this.conteneurColonnes.setPadding(new Insets(20));
        this.conteneurColonnes.setAlignment(Pos.TOP_LEFT);
        //this.conteneurColonnes.setStyle("-fx-background-color: #f4f4f4;");
        this.conteneurColonnes.getStyleClass().add("kanban-view");

        this.setContent(conteneurColonnes);

        this.btnAjouterListe = new Button("+ Ajouter une liste");
        this.btnAjouterListe.setMinWidth(200);
        //this.btnAjouterListe.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-font-size: 14px; -fx-cursor: hand;");
        this.btnAjouterListe.getStyleClass().add("btn-add");

        this.btnAjouterListe.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle Liste");
            dialog.setHeaderText(null);
            dialog.setContentText("Nom:");
            dialog.showAndWait().ifPresent(nom -> controller.traiterAjoutListe(nom.trim()));
        });

        this.projet.enregistrerObservateur(this);
        this.actualiser(projet);
    }

    @Override
    public void actualiser(Sujet s) {
        if (!(s instanceof Projet)) return;

        this.conteneurColonnes.getChildren().clear();

        for (ListeDeTache liste : projet.getListeDeTaches()) {
            ColonneKanban colonneGraphique = new ColonneKanban(liste, this.controller);
            colonneGraphique.getStyleClass().add("kanban-column");

            HBox.setHgrow(colonneGraphique, Priority.ALWAYS);

            configurerEvenementsColonne(colonneGraphique, liste);

            for (Tache t : liste.getTaches()) {
                CarteTache carteGraphique = new CarteTache(t);
                carteGraphique.getStyleClass().add("task-card");
                configurerEvenementsCarte(carteGraphique, t);
                colonneGraphique.ajouterCarte(carteGraphique);
            }

            this.conteneurColonnes.getChildren().add(colonneGraphique);
        }

        this.conteneurColonnes.getChildren().add(btnAjouterListe);
    }

    // Méthode privée à l'intérieur de VueTableau
    private void afficherDialogCreationEtiquette(Tache tacheCible) {
        // --- 1. Partie purement GRAPHIQUE (La Vue gère les pixels) ---
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Étiquette");
        dialog.setHeaderText("Ajouter une étiquette à : " + tacheCible.getNom());

        ButtonType btnValider = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNom = new TextField();
        ColorPicker colorPicker = new ColorPicker(Color.RED);

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Couleur :"), 0, 1);
        grid.add(colorPicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // --- 2. Partie LIEN AVEC CONTROLEUR ---
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                // Conversion Couleur -> String Hex (C'est du travail de Vue)
                Color c = colorPicker.getValue();
                String hex = String.format("#%02X%02X%02X",
                        (int)(c.getRed() * 255),
                        (int)(c.getGreen() * 255),
                        (int)(c.getBlue() * 255));

                // APPEL AU CONTROLEUR (On délègue le travail)
                controller.traiterAjoutEtiquette(tacheCible, txtNom.getText(), hex);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void configurerEvenementsCarte(CarteTache carte, Tache t) {
        carte.setOnDragDetected(e -> {
            if (controller.verifierDroitDeplacer(t)) {
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(t.getId()));
                db.setContent(content);
            }
            e.consume();
        });
        carte.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) controller.traiterOuvertureDetail(t);
        });
        // Clic Droit pour gérer les étiquettes
        ContextMenu contextMenu = new ContextMenu();

        MenuItem itemAjoutEtiquette = new MenuItem("Ajouter une étiquette...");
        itemAjoutEtiquette.setOnAction(event -> {
            // Ouvre une boite de dialogue pour créer l'étiquette
            afficherDialogCreationEtiquette(t);
        });

        contextMenu.getItems().add(itemAjoutEtiquette);

        // Attacher le menu à la carte
        carte.setOnContextMenuRequested(e ->
                contextMenu.show(carte, e.getScreenX(), e.getScreenY())
        );
    }

    private void configurerEvenementsColonne(ColonneKanban col, ListeDeTache listeAssociee) {
        col.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        });
        col.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int id = Integer.parseInt(db.getString());
                controller.traiterDepotTache(id, listeAssociee);
                success = true;
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }
}