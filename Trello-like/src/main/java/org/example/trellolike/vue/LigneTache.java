package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.example.trellolike.Etiquette;
import org.example.trellolike.tache.Tache;

import java.util.function.Consumer;

public class LigneTache extends HBox {

    /**
     * actionSuppression permet la suppression d'étiquette
     */
    private Consumer<Etiquette> actionSuppression;

    /**
     * Constructeur de la classe LigneTache
     * @param t
     */
    public LigneTache(Tache t) {
        // Style global de la ligne (Style "Moderne" comme sur la photo)
        this.setPadding(new Insets(10));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        // 1. Checkbox (Statut fini)
        CheckBox chkFini = new CheckBox();

        // 2. Nom de la tâche
        Label lblNom = new Label(t.getNom());
        lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        // 3. Espaceur (Pousse le reste vers la droite)
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 4. ZONE ÉTIQUETTES
        // On utilise un FlowPane pour que les badges se mettent les uns à côté des autres
        FlowPane zoneEtiquettes = new FlowPane();
        zoneEtiquettes.setHgap(5); // Espace entre les badges
        zoneEtiquettes.setAlignment(Pos.CENTER_RIGHT); // Alignés vers la droite

        for (Etiquette e : t.getEtiquettes()) {
            BadgeEtiquette badge = new BadgeEtiquette(e);

            // CONFIGURATION DU MENU SUPPRIMER
            configurerMenuBadge(badge, e);

            zoneEtiquettes.getChildren().add(badge);
        }

        // 5. Date
        Label lblDate = new Label(t.getDateFin() != null ? t.getDateFin() : "--");
        lblDate.setPrefWidth(90);
        lblDate.setStyle("-fx-text-fill: #666;");

        // 6. Étiquette (Type de travail - Simulation visuelle basée sur la photo)
        Label lblTag = new Label(t.getDureeTotale()+"J");
        lblTag.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-padding: 3 8; -fx-background-radius: 10;");

        // Assemblage
        // Ordre : Checkbox | Nom | Spacer | Etiquettes | Date
        this.getChildren().addAll(chkFini, lblNom, spacer, zoneEtiquettes, lblDate,lblTag);

        // Effet Hover
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;"));
    }
    /**
     * Crée le menu contextuel sur le petit badge
     */
    private void configurerMenuBadge(BadgeEtiquette badge, Etiquette e) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemSupprimer = new MenuItem("Supprimer l'étiquette");

        itemSupprimer.setOnAction(event -> {
            if (actionSuppression != null) {
                actionSuppression.accept(e);
            }
        });

        contextMenu.getItems().add(itemSupprimer);

        // Clic Droit sur le badge
        badge.setOnContextMenuRequested(event -> {
            contextMenu.show(badge, event.getScreenX(), event.getScreenY());
            event.consume(); // Important : empêche le menu de la ligne de s'ouvrir
        });
    }

    /**
     * Setter appelé par VueListe
     */
    public void setOnEtiquetteSupprimee(Consumer<Etiquette> action) {
        this.actionSuppression = action;
    }
}