package org.example.trellolike.vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.example.trellolike.Projet;
import org.example.trellolike.Sujet;
import org.example.trellolike.controlleur.GanttController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.control.CheckBox;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Bounds;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class VueGantt extends ScrollPane implements Observateur {
    /**
     * Le projet associé à la vue Gantt
     */
    private Projet projet;
    /**
     * Le contrôleur associé à la vue Gantt
     */
    private GanttController controller;
    /**
     * La grille principale pour afficher les tâches
     */
    private GridPane grid;
    /**
     * Le canevas pour dessiner les flèches de dépendance
     */
    private Pane overlayPane;
    /**
     * Map pour mémoriser les nœuds des tâches
     */
    private Map<Integer, StackPane> taskNodes = new HashMap<>();
    /**
     * Checkbox pour afficher/masquer les dépendances
     */
    private CheckBox chkAfficherDependances;


    private final int LARGEUR_JOUR = 40;
    private final int HAUTEUR_BARRE = 30;

    public VueGantt(Projet projet, GanttController controller) {
        this.projet = projet;
        this.controller = controller;
        this.projet.enregistrerObservateur(this);

        // Conteneur principal
        VBox conteneurPrincipal = new VBox();

        // Barre d'outils
        HBox toolBar = new HBox(15);
        toolBar.setPadding(new Insets(10));
        toolBar.setAlignment(Pos.CENTER_LEFT);

        // Checkbox pour afficher/masquer les dépendances
        chkAfficherDependances = new CheckBox("Afficher les dépendances");
        chkAfficherDependances.setSelected(controller.isAfficherDependances());
        chkAfficherDependances.setOnAction(e -> {;
            controller.gererDependances(chkAfficherDependances.isSelected());
        });

        toolBar.getChildren().add(chkAfficherDependances);

        this.grid = new GridPane();
        this.grid.setHgap(0);
        this.grid.setVgap(10);
        this.grid.setPadding(new Insets(20));
        this.grid.setStyle("-fx-background-color: white;");

        // Canevas pour les flèches de dépendance
        this.overlayPane = new Pane();
        this.overlayPane.setMouseTransparent(true);

        // Superposition des 2
        StackPane stackDiagramme = new StackPane(grid, overlayPane);
        stackDiagramme.setAlignment(Pos.TOP_LEFT);

        conteneurPrincipal.getChildren().addAll(toolBar, stackDiagramme);

        this.setContent(conteneurPrincipal);
        this.setFitToWidth(true);
        this.setPannable(true);

        actualiser(projet);
    }

    /**
     * Méthode pour actualiser la vue Gantt
     * @param s le projet
     */
    @Override
    public void actualiser(Sujet s) {
        grid.getChildren().clear();
        overlayPane.getChildren().clear();
        taskNodes.clear();

        List<Tache> toutesLesTaches = this.projet.obtenirToutesLesTaches();
        if (toutesLesTaches.isEmpty()) {
            grid.add(new Label("Aucune tâche à afficher."), 0, 0);
            return;
        }

        LocalDate dateDebutProjet = calculerDateMin(toutesLesTaches);
        creerEnTete(dateDebutProjet);

        int indexLigne = 1;
        for (Tache t : toutesLesTaches) {
            if (t.getDateDebut() == null || t.getDateFin() == null || t.getDateDebut().isEmpty()) {
                continue;
            }

            try {
                LocalDate debut = LocalDate.parse(t.getDateDebut());
                LocalDate fin = LocalDate.parse(t.getDateFin());

                // Calcul des décalages
                long decalageJours = ChronoUnit.DAYS.between(dateDebutProjet, debut);
                long dureeJours = ChronoUnit.DAYS.between(debut, fin) + 1;

                // Sécurité : si la durée est négative ou nulle à cause d'une erreur de saisie
                if (dureeJours <= 0) {
                    dureeJours = 1; // On affiche au moins une barre d'un jour
                }
                //Création de la barre (Fond)
                Rectangle barre = new Rectangle(dureeJours * LARGEUR_JOUR, HAUTEUR_BARRE);
                barre.setArcHeight(10);
                barre.setArcWidth(10);

                //Création du Label (Texte sur la barre)
                Label lblNom = new Label(t.getNom());
                lblNom.getStyleClass().add("gantt-task-label");
                lblNom.setMaxWidth(dureeJours * LARGEUR_JOUR - 10); // Empêche le texte de déborder

                //Superposition dans un StackPane
                StackPane stack = new StackPane();
                stack.getChildren().addAll(barre, lblNom);
                stack.setAlignment(Pos.CENTER);// Centre le texte sur la barre

                //Mémoriser le noeud pour tracer les flèches plus tard
                taskNodes.put(t.getId(), stack);

                // Application des styles selon le statut
                appliquerStyleStatut(stack,t);

                // Effet de survol
                stack.setOnMouseEntered(e -> stack.setOpacity(0.8));
                stack.setOnMouseExited(e -> stack.setOpacity(1.0));

                // Double clic pour voir les détails
                stack.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) controller.traiterOuvertureDetail(t);
                });

                // Positionnement avec le décalage temporel
                HBox ligneTemps = new HBox();
                Region spacer = new Region();
                spacer.setMinWidth(decalageJours * LARGEUR_JOUR);
                ligneTemps.getChildren().addAll(spacer, stack);

                // On ajoute tout dans la colonne 0
                grid.add(ligneTemps, 0, indexLigne);

                indexLigne++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Tracer les dépendances si la checkbox est cochée
        if (controller.isAfficherDependances()) {
            javafx.application.Platform.runLater(this::dessinerFlecheDependances);
        }
    }

    /**
     * Méthode pour dessiner les flèches de dépendance
     */
    private void dessinerFlecheDependances(){
        overlayPane.getChildren().clear();
        grid.applyCss();
        grid.layout();
        for (Tache t : projet.obtenirToutesLesTaches()) {
            StackPane sourceNode = taskNodes.get(t.getId()); // La tâche bloquée
            if (sourceNode == null) continue;

            for (Integer idDep : t.getIdsDependances()) {
                StackPane targetNode = taskNodes.get(idDep); // La tâche bloquante
                if (targetNode == null) continue;

                if (sourceNode.getScene() == null || targetNode.getScene() == null) continue;

                // On récupère les limites (Bounds) des deux barres dans la Scène
                Bounds boundsBloquant = targetNode.localToScene(targetNode.getBoundsInLocal());
                Bounds boundsBloque = sourceNode.localToScene(sourceNode.getBoundsInLocal());

                //On convertit ces positions SCÈNE vers le système LOCAL de l'overlayPane
                Bounds bSource = overlayPane.sceneToLocal(boundsBloquant);
                Bounds bCible = overlayPane.sceneToLocal(boundsBloque);

                // Point de départ : Milieu du bord DROIT de la tâche bloquante
                double startX = bSource.getMaxX();
                double startY = bSource.getMinY() + bSource.getHeight() / 2;

                // Point d'arrivée : Milieu du bord GAUCHE de la tâche bloquée
                double endX = bCible.getMinX();
                double endY = bCible.getMinY() + bCible.getHeight() / 2;

                // Calcul des points pour la flèche
                double longueurFleche = 8.0;
                double dx = endX - startX;
                double dy = endY - startY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // On calcule le point d'arrêt de la ligne (8 pixels avant la pointe)
                double lineEndX = endX;
                double lineEndY = endY;

                if (distance > longueurFleche) {
                    lineEndX = endX - (longueurFleche * dx / distance);
                    lineEndY = endY - (longueurFleche * dy / distance);
                }

                // Création de la ligne s'arrêtant au nouveau point
                Line line = new Line(startX, startY, lineEndX, lineEndY);
                line.getStyleClass().add("dependency-line");

                // Création de la flèche
                Polygon arrowHead = new Polygon();

                // Calcul de l'angle pour orienter la pointe de la flèche
                double angle = Math.atan2(dy, dx);
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);

                arrowHead.getPoints().addAll(
                        endX, endY,                                     // Pointe
                        endX - 6 * cos + 3 * sin, endY - 8 * sin - 5 * cos, // Haut base
                        endX - 6 * cos - 3 * sin, endY - 8 * sin + 5 * cos  // Bas base
                );

                arrowHead.getStyleClass().add("dependency-arrow");

                overlayPane.getChildren().addAll(line, arrowHead);
            }
        }
    }

    /**
     * Méthode pour appliquer le style selon le statut de la tâche
     * @param t la tâche
     * @param stack le nœud graphique de la tâche
     */
    private void appliquerStyleStatut(StackPane stack, Tache t) {
        if (projet.estTacheTerminee(t)) {
            stack.getStyleClass().add("status-finished"); //Vert clair
        } else {
            ListeDeTache liste = projet.trouverListeDeLaTache(t);
            if (liste != null && liste.getNom().equalsIgnoreCase("En Cours")) {
                stack.getStyleClass().add("status-in-progress"); //Bleu clair
            } else {
                stack.getStyleClass().add("status-planned"); //Gris clair
            }
        }
    }

    /**
     * Méthode pour créer l'en-tête des dates
     * @param dateMin la date minimale
     */
    private void creerEnTete(LocalDate dateMin) {
        HBox bandeauDates = new HBox();
        for (int i = 0; i < 50; i++) {
            LocalDate jourActuel = dateMin.plusDays(i);
            Label lblJour = new Label(jourActuel.getDayOfMonth() + "/" + jourActuel.getMonthValue());
            lblJour.setMinWidth(LARGEUR_JOUR);
            lblJour.setPrefWidth(LARGEUR_JOUR);
            lblJour.setStyle("-fx-border-color: #ecf0f1; -fx-alignment: center; -fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
            bandeauDates.getChildren().add(lblJour);
        }
        grid.add(bandeauDates, 0, 0);
    }


    /**
     * Méthode pour calculer la date minimale parmi les tâches
     * @param taches la liste des tâches
     * @return la date minimale
     */
    private LocalDate calculerDateMin(List<Tache> taches) {
        LocalDate min = LocalDate.now();
        for (Tache t : taches) {
            try {
                if (t.getDateDebut() != null && !t.getDateDebut().isEmpty()) {
                    LocalDate d = LocalDate.parse(t.getDateDebut());
                    if (d.isBefore(min)) min = d;
                }
            } catch (Exception ignored) {}
        }
        return min.minusDays(2);
    }
}