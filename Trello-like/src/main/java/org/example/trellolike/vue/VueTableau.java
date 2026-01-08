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

import java.util.ArrayList;
import java.util.List;

public class VueTableau extends ScrollPane implements Observateur {

    private Projet projet;
    private KanbanController controller;

    // Composants UI principaux
    private HBox conteneurColonnes;
    private Button btnAjouterListe;
    private Button btnTriDuree; // On le garde en attribut pour changer son texte

    public VueTableau(Projet projet, KanbanController controller) {
        this.projet = projet;
        this.controller = controller;

        // --- 1. BARRE D'OUTILS (Haut) ---
        HBox barreOutils = new HBox(15);
        barreOutils.setPadding(new Insets(15));
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        // Champ Recherche
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher...");
        searchField.setPrefWidth(200);

        // Filtre Priorité
        ComboBox<String> comboPriorite = new ComboBox<>();
        comboPriorite.getItems().addAll("Toutes les priorités", "Basse", "Moyenne", "Haute", "Urgente");
        comboPriorite.setValue("Toutes les priorités");

        // Listeners pour les filtres
        searchField.textProperty().addListener((obs, old, nouveau) ->
                controller.mettreAJourFiltres(nouveau, comboPriorite.getValue()));

        comboPriorite.valueProperty().addListener((obs, old, nouveau) ->
                controller.mettreAJourFiltres(searchField.getText(), comboPriorite.getValue()));

        // Bouton Tri Durée
        this.btnTriDuree = new Button("Tri Durée : -");
        btnTriDuree.setStyle("-fx-background-color: #f0f0f0; -fx-cursor: hand;");
        btnTriDuree.setOnAction(e -> {
            controller.mettreAJourTriDuree();
            // Le texte du bouton sera mis à jour dans actualiser()
        });

        barreOutils.getChildren().addAll(
                new Label("🔍"), searchField,
                new Label("Priorité :"), comboPriorite,
                btnTriDuree
        );

        // --- 2. CONFIGURATION SCROLLPANE ---
        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setPannable(true); // Permet de glisser avec la souris
        this.setStyle("-fx-background-color: #f4f4f4;");

        // --- 3. ZONE PRINCIPALE (Colonnes) ---
        this.conteneurColonnes = new HBox();
        this.conteneurColonnes.setSpacing(20);
        this.conteneurColonnes.setPadding(new Insets(20));
        this.conteneurColonnes.setAlignment(Pos.TOP_LEFT);

        // Layout global
        VBox layoutPrincipal = new VBox();
        layoutPrincipal.getChildren().addAll(barreOutils, conteneurColonnes);
        this.setContent(layoutPrincipal);

        // --- 4. BOUTON AJOUTER LISTE ---
        this.btnAjouterListe = new Button("+ Ajouter une liste");
        this.btnAjouterListe.setMinWidth(250);
        this.btnAjouterListe.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #555; -fx-background-radius: 5;");

        this.btnAjouterListe.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouvelle Liste");
            dialog.setHeaderText(null);
            dialog.setContentText("Nom de la liste:");
            dialog.showAndWait().ifPresent(nom -> controller.traiterAjoutListe(nom.trim()));
        });

        // --- 5. INITIALISATION ---
        this.projet.enregistrerObservateur(this);
        this.actualiser(projet);
    }

    @Override
    public void actualiser(Sujet s) {
        if (!(s instanceof Projet)) return;

        // 1. Mise à jour du texte du bouton Tri (Feedback visuel)
        int etatTri = controller.getEtatTriDuree();
        if (etatTri == 0) btnTriDuree.setText("Tri Durée : Aucun");
        else if (etatTri == 1) btnTriDuree.setText("Tri Durée : Croissant (↑)");
        else btnTriDuree.setText("Tri Durée : Décroissant (↓)");

        // 2. Nettoyage de l'interface
        this.conteneurColonnes.getChildren().clear();

        // 3. Reconstruction des colonnes
        List<ListeDeTache> lesListes = projet.getListeDeTaches();

        // Boucle avec index 'i' pour gérer le Drag & Drop des colonnes
        for (int i = 0; i < lesListes.size(); i++) {
            ListeDeTache liste = lesListes.get(i);

            // Création de la colonne graphique (supposons que ColonneKanban existe)
            ColonneKanban colonneGraphique = new ColonneKanban(liste, this.controller);
            HBox.setHgrow(colonneGraphique, Priority.ALWAYS); // Prend la place dispo

            // CONFIGURATION DRAG & DROP GLOBAL (Tâche + Colonne)
            configurerDragAndDropGlobal(colonneGraphique, liste, i);

            // A. Filtrage des tâches
            List<Tache> tachesVisibles = new ArrayList<>();
            for (Tache t : liste.getTaches()) {
                if (controller.doitAfficherTache(t)) {
                    tachesVisibles.add(t);
                }
            }

            // B. Tri des tâches
            controller.trierTaches(tachesVisibles);

            // C. Affichage des cartes
            for (Tache t : tachesVisibles) {
                CarteTache carteGraphique = new CarteTache(t);

                // Callback pour la suppression d'étiquette
                carteGraphique.setOnEtiquetteSupprimee(etiquetteASupprimer -> {
                    controller.traiterSuppressionEtiquette(t, etiquetteASupprimer);
                });

                configurerEvenementsCarte(carteGraphique, t);
                colonneGraphique.ajouterCarte(carteGraphique);
            }

            this.conteneurColonnes.getChildren().add(colonneGraphique);
        }

        // Ajout du bouton "+" à la fin
        this.conteneurColonnes.getChildren().add(btnAjouterListe);
    }

    /**
     * Gère TOUT le Drag & Drop sur une colonne (Tâches ET Colonnes)
     */
    private void configurerDragAndDropGlobal(ColonneKanban col, ListeDeTache listeModel, int indexColonne) {

        // 1. DÉPART DU DRAG (Si on déplace la colonne elle-même)
        col.setOnDragDetected(event -> {
            // Si la cible est une carte, on laisse la carte gérer son drag
            if (event.getTarget() instanceof CarteTache) return;

            Dragboard db = col.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // Marqueur spécifique pour les colonnes
            content.putString("LISTE:" + indexColonne);
            db.setContent(content);
            event.consume();
        });

        // 2. SURVOL
        col.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // 3. LÂCHER (DROP)
        col.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String data = db.getString();

                if (data.startsWith("LISTE:")) {
                    // --- CAS A : Déplacement de COLONNE ---
                    try {
                        int indexSource = Integer.parseInt(data.split(":")[1]);
                        controller.traiterDeplacementListe(indexSource, indexColonne);
                        success = true;
                    } catch (Exception e) { System.err.println("Erreur drop colonne"); }

                } else if (data.startsWith("TASK:")) {
                    // --- CAS B : Déplacement de TÂCHE ---
                    try {
                        int idTache = Integer.parseInt(data.split(":")[1]);
                        controller.traiterDepotTache(idTache, listeModel);
                        success = true;
                    } catch (Exception e) { System.err.println("Erreur drop tache"); }
                }
                // Fallback pour compatibilité (si juste ID envoyé)
                else {
                    try {
                        int idTache = Integer.parseInt(data);
                        controller.traiterDepotTache(idTache, listeModel);
                        success = true;
                    } catch (NumberFormatException e) { }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Configure les événements spécifiques à une Carte (Tâche)
     */
    private void configurerEvenementsCarte(CarteTache carte, Tache t) {
        // Drag Start
        carte.setOnDragDetected(e -> {
            Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // On ajoute le préfixe TASK: pour différencier des colonnes
            content.putString("TASK:" + t.getId());
            db.setContent(content);
            e.consume();
        });

        // Double Click -> Détails
        carte.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) controller.traiterOuvertureDetail(t);
        });

        // Clic Droit -> Menu Contextuel
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemAjoutEtiquette = new MenuItem("Ajouter une étiquette...");
        itemAjoutEtiquette.setOnAction(event -> afficherDialogCreationEtiquette(t));
        contextMenu.getItems().add(itemAjoutEtiquette);

        carte.setOnContextMenuRequested(e ->
                contextMenu.show(carte, e.getScreenX(), e.getScreenY())
        );
    }

    /**
     * Boîte de dialogue pour créer une étiquette
     */
    private void afficherDialogCreationEtiquette(Tache tacheCible) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Étiquette");
        dialog.setHeaderText("Pour : " + tacheCible.getNom());

        ButtonType btnValider = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField txtNom = new TextField();
        ColorPicker colorPicker = new ColorPicker(Color.RED);

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Couleur :"), 0, 1);
        grid.add(colorPicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnValider) {
                Color c = colorPicker.getValue();
                String hex = String.format("#%02X%02X%02X",
                        (int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255));
                controller.traiterAjoutEtiquette(tacheCible, txtNom.getText(), hex);
            }
            return null;
        });

        dialog.showAndWait();
    }
}