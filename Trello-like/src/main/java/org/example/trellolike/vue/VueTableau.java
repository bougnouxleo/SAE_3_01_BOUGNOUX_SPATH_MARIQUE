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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class VueTableau extends ScrollPane implements Observateur {
    /**
     * Le projet associé à cette vue
     */
    private Projet projet;
    /**
     * Le contrôleur associé à cette vue
     */
    private KanbanController controller;
    /**
     * Bouton pour ajouter une nouvelle liste
     */
    private Button btnAjouterListe;

    /**
     * Conteneur horizontal pour les colonnes du tableau Kanban
     */
    private HBox conteneurColonnes;

    /**
     * Constructeur de la vue tableau Kanban
     * @param projet Le projet à afficher
     * @param controller Le contrôleur associé
     */
    public VueTableau(Projet projet, KanbanController controller) {
        this.projet = projet;
        this.controller = controller;

        // --- Barre de recherche et filtres ---
        HBox barreOutils = new HBox(15);
        barreOutils.setPadding(new Insets(15));
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une tâche...");
        searchField.setPrefWidth(250);

        ComboBox<String> comboPriorite = new ComboBox<>();
        comboPriorite.getItems().addAll("Toutes les priorités", "Basse", "Moyenne", "Haute","Urgente");
        comboPriorite.setValue("Toutes les priorités");

        // Écouteurs pour mettre à jour le filtrage en temps réel
        searchField.textProperty().addListener((obs, old, nouveau) ->
                controller.mettreAJourFiltres(nouveau, comboPriorite.getValue()));

        comboPriorite.valueProperty().addListener((obs, old, nouveau) ->
                controller.mettreAJourFiltres(searchField.getText(), comboPriorite.getValue()));

        barreOutils.getChildren().addAll(new Label("🔍"), searchField, new Label("Priorité :"), comboPriorite);

        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setPannable(true);
        this.setStyle("-fx-background-color: #f4f4f4;");

        this.conteneurColonnes = new HBox();
        this.conteneurColonnes.setSpacing(20);
        this.conteneurColonnes.setPadding(new Insets(20));
        this.conteneurColonnes.setAlignment(Pos.TOP_LEFT);
        this.conteneurColonnes.getStyleClass().add("kanban-view");

        // Layout principal avec la barre d'outils en haut
        VBox layoutPrincipal = new VBox();
        layoutPrincipal.getChildren().addAll(barreOutils, conteneurColonnes);

        this.setContent(layoutPrincipal);

        this.btnAjouterListe = new Button("+ Ajouter une liste");
        this.btnAjouterListe.setMinWidth(200);
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

    /**
     * Mise à jour de l'affichage
     * @param s le projet
     */
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
                if (controller.doitAfficherTache(t)) {
                    CarteTache carteGraphique = new CarteTache(t);
                    carteGraphique.getStyleClass().add("task-card");

                    carteGraphique.setOnEtiquetteSupprimee(etiquetteASupprimer -> {
                        controller.traiterSuppressionEtiquette(t, etiquetteASupprimer);
                    });

                    configurerEvenementsCarte(carteGraphique, t);
                    colonneGraphique.ajouterCarte(carteGraphique);
                };
            }

            this.conteneurColonnes.getChildren().add(colonneGraphique);
        }

        this.conteneurColonnes.getChildren().add(btnAjouterListe);
    }

    /**
     * Affiche une boîte de dialogue pour créer une nouvelle étiquette
     * @param tacheCible La tâche à laquelle ajouter l'étiquette
     */
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

    /**
     * Configure les événements pour une carte de tâche
     * @param carte
     * @param t
     */
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

    /**
     * Configure les événements pour une colonne Kanban
     * @param col la colonne Kanban
     * @param listeAssociee la liste de tâches associée
     */
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