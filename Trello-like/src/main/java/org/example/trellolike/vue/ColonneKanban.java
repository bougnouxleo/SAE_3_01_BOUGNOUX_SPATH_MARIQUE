package org.example.trellolike.vue;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.trellolike.Projet;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.example.trellolike.tache.Tache;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;


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

        //bouton pour la modif de Liste
        Button btnOptions = new Button("...");
        btnOptions.setOnAction(e -> {

            ContextMenu menu = new ContextMenu();

            MenuItem itemRenommer = new MenuItem("Renommer");
            itemRenommer.setOnAction(ev -> {
                TextInputDialog dialog = new TextInputDialog(liste.getNom());
                dialog.setTitle("Renommer la liste");
                dialog.setHeaderText(null);
                dialog.setContentText("Nouveau nom:");
                dialog.showAndWait().ifPresent(nom -> {
                    if (!nom.trim().isEmpty()) {
                        controller.traiterRenommerListe(liste, nom.trim());
                    }
                });
            });

            MenuItem itemArchiver = new MenuItem("Archiver");
            itemArchiver.setOnAction(ev -> {
                controller.traiterArchiverListeDeTaches(liste);
            });

            menu.getItems().addAll(itemRenommer, itemArchiver);
            menu.show(btnOptions, javafx.geometry.Side.BOTTOM, 0, 0);
        });

        // apparence pour separation
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // new Hbox avec separation
        HBox header = new HBox();
        header.getChildren().addAll(lbl, spacer, btnOptions);
        this.getChildren().add(header);

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
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setHeaderText("Créer une tâche dans : " + listeModele.getNom());

        ButtonType btnTypeValider = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // --- Les Champs ---
        TextField txtNom = new TextField();
        txtNom.setPromptText("Nom de la tâche");

        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Description détaillée...");
        txtDesc.setPrefRowCount(3);

        DatePicker dateDebut = new DatePicker(LocalDate.now());
        DatePicker dateFin = new DatePicker();

        CheckBox chkComposite = new CheckBox("Est un projet (Tâche Composite)");

        Text txtDuree = new Text("0");

        dateDebut.valueProperty().addListener((obs, oldVal, newVal) ->
                mettreAJourDuree(dateDebut, dateFin, txtDuree, chkComposite));

        dateFin.valueProperty().addListener((obs, oldVal, newVal) ->
                mettreAJourDuree(dateDebut, dateFin, txtDuree, chkComposite));

        chkComposite.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            txtDuree.setDisable(isSelected);
            mettreAJourDuree(dateDebut, dateFin, txtDuree, chkComposite);
        });

        Label lblDep = new Label("Est bloquée par :");
        ListView<Tache> listeDependances = new ListView<>();
        listeDependances.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listeDependances.setMaxHeight(100);

        List<Tache> toutes = new ArrayList<>();
        for (ListeDeTache liste : Projet.getInstance().getListes()) {
            toutes.addAll(liste.getTaches());
        }
        listeDependances.getItems().addAll(toutes);

        //Mise en place de la grille
        grid.add(new Label("Nom :"), 0, 0);       grid.add(txtNom, 1, 0);
        grid.add(new Label("Desc :"), 0, 1);      grid.add(txtDesc, 1, 1);
        grid.add(new Label("Début :"), 0, 2);     grid.add(dateDebut, 1, 2);
        grid.add(new Label("Fin :"), 0, 3);       grid.add(dateFin, 1, 3);

        grid.add(new Label("Type :"), 0, 4);      grid.add(chkComposite, 1, 4);
        grid.add(new Label("Durée (J) :"), 0, 5); grid.add(txtDuree, 1, 5);

        grid.add(new Label("Bloqué par :"), 0, 6);grid.add(listeDependances, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(btnTypeValider);

        btnOk.addEventFilter(ActionEvent.ACTION, event -> {
            LocalDate debut = dateDebut.getValue();
            LocalDate fin = dateFin.getValue();

            // 1. Vérifier si les dates sont remplies
            if (debut == null || fin == null) {
                event.consume();
                afficherAlerte("Champs vides", "Veuillez renseigner les deux dates.");
                return;
            }

            // 2. Vérifier que le début n'est pas dans le passé
            if (debut.isBefore(LocalDate.now())) {
                event.consume();
                afficherAlerte("Date invalide", "La date de début ne peut pas être dans le passé !");
            }
            // 3. Vérifier que la fin est APRES le début
            else if (fin.isBefore(debut)) {
                event.consume();
                afficherAlerte("Incohérence", "La date de fin ne peut pas être avant la date de début !");
            }
        });


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeValider) {
                // Parsing de la durée
                int duree = 0;
                try {
                    if (!chkComposite.isSelected()) {
                        duree = Integer.parseInt(txtDuree.getText());
                    }
                } catch (NumberFormatException e) { duree = 0; }

                List<Tache> selection = listeDependances.getSelectionModel().getSelectedItems();

                controller.traiterAjoutTache(
                        txtNom.getText(),
                        txtDesc.getText(),
                        dateDebut.getValue(),
                        dateFin.getValue(),
                        listeModele,
                        new ArrayList<>(selection),
                        chkComposite.isSelected(), // On passe le booléen
                        duree // On passe la durée saisie
                );
                return true;
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Affiche une alerte avec le titre et le message donnés
     * @param titre
     * @param message
     */
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Met à jour le champ durée en fonction des dates et du type de tâche
     * @param debut
     * @param fin
     * @param txtDuree
     * @param chkComposite
     */
    private void mettreAJourDuree(DatePicker debut, DatePicker fin, Text txtDuree, CheckBox chkComposite) {
        if (chkComposite.isSelected()) {
            txtDuree.setText("Calculé auto.");
            return;
        }

        LocalDate d1 = debut.getValue();
        LocalDate d2 = fin.getValue();

        if (d1 != null && d2 != null) {
            long jours = java.time.temporal.ChronoUnit.DAYS.between(d1, d2);

            txtDuree.setText(String.valueOf(Math.max(0, jours)));
        } else {
            txtDuree.setText("0");
        }
    }
}