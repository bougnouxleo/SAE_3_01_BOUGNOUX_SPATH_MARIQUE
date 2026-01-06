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
import org.example.trellolike.tache.Tache;

import java.util.List;

/**
 * Vue affichant les tâches archivées du projet.
 * Style identique à VueListe - une seule liste contenant les tâches archivées.
 */
public class VueArchives extends ScrollPane implements Observateur {

    private Projet projet;
    private VBox conteneurPrincipal;

    public VueArchives(Projet projet) {
        this.projet = projet;

        // Configuration du ScrollPane (identique à VueListe)
        this.setFitToWidth(true);
        this.setStyle("-fx-background-color: white;");

        // Conteneur interne vertical
        this.conteneurPrincipal = new VBox();
        this.conteneurPrincipal.setPadding(new Insets(20));
        this.conteneurPrincipal.setSpacing(20);
        this.setContent(conteneurPrincipal);

        // Abonnement au pattern Observer
        this.projet.enregistrerObservateur(this);

        // Premier affichage
        this.actualiser(projet);
    }

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
            }

            // Ajout au conteneur principal
            conteneurPrincipal.getChildren().addAll(titreSection, listeArchives);
        };
    }
}