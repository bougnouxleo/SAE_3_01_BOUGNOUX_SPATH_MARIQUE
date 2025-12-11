package org.example.trellolike.vue;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;

public class VueTableau extends HBox implements Observateur {

    private Projet projetCourant;

    public VueTableau() {
        // Configuration visuelle globale (espacement entre les colonnes)
        this.setSpacing(20);
        this.setStyle("-fx-padding: 20; -fx-background-color: #f4f4f4;");
    }

    @Override
    public void actualiser(Sujet s) {
        // 1. Récupération du modèle
        if (!(s instanceof Projet)) return;
        this.projetCourant = (Projet) s;

        // 2. NETTOYAGE TOTAL
        // On supprime toutes les colonnes visuelles existantes pour éviter les doublons
        this.getChildren().clear();

        // 3. RECONSTRUCTION DYNAMIQUE
        // On parcourt les listes du modèle (ex: "A faire", "En cours", "Finies")
        for (ListeDeTache listeModele : projetCourant.getListeDeTaches()) { // Supposons getListes()

            // On crée une colonne visuelle pour chaque liste du modèle
            // On lui passe la liste (pour qu'elle sache quelles tâches afficher)
            // et le projet (pour qu'elle puisse appeler deplacerTache plus tard)
            ColonneKanban colonneVisuelle = new ColonneKanban(listeModele, projetCourant);

            // On l'ajoute à la vue
            this.getChildren().add(colonneVisuelle);
        }
    }
}
