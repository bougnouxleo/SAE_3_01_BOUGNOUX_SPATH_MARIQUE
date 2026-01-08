package org.example.trellolike.vue;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.ArchiveController;
import org.example.trellolike.tache.Tache;

import java.util.List;

/**
 * Vue affichant les tâches archivées du projet.
 * Style identique à VueListe - une seule liste contenant les tâches archivées.
 */
public class VueArchives extends ScrollPane implements Observateur {
    /**
     * Le projet associé
     */
    private Projet projet;
    /**
     * Conteneur principal vertical
     */
    private VBox conteneurPrincipal;
    /**
     * Le contrôleur des archives
     */
    private ArchiveController archiveController;

    public VueArchives(Projet projet,ArchiveController controller) {
        this.projet = projet;
        this.archiveController = controller;

        // Configuration du ScrollPane (identique à VueListe)
        this.setFitToWidth(true);
        this.setStyle("-fx-background-color: white;");

        // Conteneur interne vertical
        this.conteneurPrincipal = new VBox();
        this.conteneurPrincipal.setPadding(new Insets(20));
        this.conteneurPrincipal.setSpacing(20);
        this.setContent(conteneurPrincipal);

        this.projet.enregistrerObservateur(this);

        this.actualiser(projet);
    }

    /**
     * Actualise la vue avec les tâches archivées du projet
     * @param s le projet
     */
    @Override
    public void actualiser(Sujet s) {
        if (!(s instanceof Projet)) return;
        conteneurPrincipal.getChildren().clear();

        // Récupération des tâches archivées
        List<Tache> tachesArchivees = projet.getListeDesArchives();

        // Titre de la section (comme le titre du jour dans VueListe)
        Label titreSection = new Label("Archives");
        titreSection.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Conteneur pour les tâches archivées (comme listeDuJour dans VueListe)
        VBox listeArchives = new VBox();
        listeArchives.setStyle("-fx-background-color: white;");

        // Ajout de l'ombre via Java au lieu de CSS (évite le warning)
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetY(1);
        shadow.setColor(Color.rgb(0, 0, 0, 0.05));
        listeArchives.setEffect(shadow);

        if (tachesArchivees == null || tachesArchivees.isEmpty()) {
            // Message si aucune archive
            conteneurPrincipal.getChildren().add(new Label("Aucune tâche archivée."));
        } else {
            // Création des lignes avec LigneTache (exactement comme VueListe)
            for (Tache t : tachesArchivees) {
                LigneTache ligne = new LigneTache(t);
                listeArchives.getChildren().add(ligne);

                conteneurPrincipal.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) this.archiveController.traiterOuvertureDetail(t);
                });
            }

            // Ajout au conteneur principal
            conteneurPrincipal.getChildren().addAll(titreSection, listeArchives);
        };

    }
}