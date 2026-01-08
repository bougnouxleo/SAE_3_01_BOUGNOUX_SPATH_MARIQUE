package org.example.trellolike.vue.Kanban;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import org.example.trellolike.Etiquette.Etiquette;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.vue.Etiquette.BadgeEtiquette;

import java.util.function.Consumer;

/**
 * Composante graphique
 */
public class CarteTache extends VBox {
    private Consumer<Etiquette> actionSuppression;

    public CarteTache(Tache t) {
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: white;-fx-border-color: black; -fx-effect: dropshadow('...');");

        this.setSpacing(5); // Espace entre les éléments

        // 1. Zone des étiquettes (en haut de la carte)
        if (!t.getEtiquettes().isEmpty()) {
            FlowPane zoneEtiquettes = new FlowPane();
            zoneEtiquettes.setHgap(5); // Espace horizontal entre badges
            zoneEtiquettes.setVgap(5); // Espace vertical

            for (Etiquette e : t.getEtiquettes()) {
                BadgeEtiquette badge = new BadgeEtiquette(e);

                // --- AJOUT DU CLIC DROIT SUR LE BADGE ---
                configurerMenuBadge(badge, e);

                zoneEtiquettes.getChildren().add(badge);
            }
            this.getChildren().add(zoneEtiquettes);
        }

        // 2. Nom de la tâche
        Label lblNom = new Label(t.getNom());
        this.getChildren().add(lblNom);

        // Logique purement visuelle (Présentation)
        if (t.estBloquee()) {
            this.setStyle("-fx-background-color: #eee; -fx-border-color: red;");
            this.getChildren().add(new Label("🔒"));
        }
    }
    /**
     * Configure le menu contextuel "Supprimer" sur un badge spécifique
     */
    private void configurerMenuBadge(BadgeEtiquette badge, Etiquette e) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemSupprimer = new MenuItem("Supprimer l'étiquette");

        itemSupprimer.setOnAction(event -> {
            // Si une action a été définie par la VueTableau, on l'exécute
            if (actionSuppression != null) {
                actionSuppression.accept(e);
            }
        });

        contextMenu.getItems().add(itemSupprimer);

        // Attache le menu au badge
        badge.setOnContextMenuRequested(event -> {
            contextMenu.show(badge, event.getScreenX(), event.getScreenY());
            event.consume(); // Empêche le menu de la carte de s'ouvrir aussi !
        });
    }

    /**
     * Setter pour permettre à VueTableau de définir l'action
     */
    public void setOnEtiquetteSupprimee(Consumer<Etiquette> action) {
        this.actionSuppression = action;
    }
}