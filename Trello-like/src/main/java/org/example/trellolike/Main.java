package org.example.trellolike;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.trellolike.controlleur.ArchiveController;
import org.example.trellolike.controlleur.GanttController;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.vue.VueGantt;
import org.example.trellolike.vue.VueListe;
import org.example.trellolike.vue.VueTableau;
import org.example.trellolike.vue.VueArchives;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.trellolike.controlleur.CalendarController;
import org.example.trellolike.vue.VueCalendrier;

public class Main extends Application {
    /**
     * Layout principal de l'application
     */
    private Projet projet;
    /**
     * Layout principal de l'application
     */
    private KanbanController controller;
    /**
     * Layout principal de l'application
     */
    private BorderPane root;
    /**
     * Controller de la vue Gantt
     */
    private GanttController ganttController;
    /**
     * Controller de la vue Archives
     */
    private ArchiveController archiveController;
    /**
     * Controller de la vue Calendrier
     */
    private CalendarController calendarController;

    @Override
    public void start(Stage stage) {
        // 1. CHARGEMENT
        this.projet = Projet.getInstance();

        this.controller = new KanbanController(this.projet);
        this.ganttController = new GanttController(this.projet);
        this.archiveController = new ArchiveController(this.projet);
        this.calendarController = new CalendarController(this.projet);

        // 2. CRÉATION DU LAYOUT PRINCIPAL (BorderPane)
        this.root = new BorderPane();

        // 3. CRÉATION DU MENU (Barre de navigation)
        HBox menuBar = creerBarreDeNavigation();
        menuBar.getStyleClass().add("menu-bar-container");

        // On place le menu tout en haut
        root.setTop(menuBar);

        // 4. AFFICHAGE DE LA VUE PAR DÉFAUT (Le Kanban)
        changerVue("KANBAN");

        // 5. CONFIGURATION DE LA SCÈNE
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("SAE 3.01 - Gestionnaire de Tâches (" + projet.getNom() + ")");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Crée la barre de navigation avec les boutons pour changer de vue
     * @return La barre de navigation (HBox)
     */
    private HBox creerBarreDeNavigation() {
        HBox menu = new HBox(20);
        menu.setPadding(new Insets(15));
        menu.setAlignment(Pos.CENTER_LEFT);

        // --- Création des boutons ---
        Button btnKanban = new Button("Vue Kanban");
        Button btnListe = new Button("Vue Liste");
        Button btnGantt = new Button("Vue Gantt");
        Button btnArchives = new Button("Archives");
        Button btnCalendrier = new Button("Calendrier");

        //Images des boutons
        //Gantt
        Image img = new Image("/gantt.jpg");
        ImageView view = new ImageView(img);
        view.setFitHeight(40);
        view.setPreserveRatio(true);
        btnGantt.setGraphic(view);

        //Liste
        Image img2 = new Image("/list.png");
        ImageView view2 = new ImageView(img2);
        view2.setFitHeight(40);
        view2.setPreserveRatio(true);
        btnListe.setGraphic(view2);

        //Kanban
        Image img3 = new Image("/kanban.png");
        ImageView view3 = new ImageView(img3);
        view3.setFitHeight(40);
        view3.setPreserveRatio(true);
        btnKanban.setGraphic(view3);

        //Archives
        Image img4 = new Image("/archive.jpg");
        ImageView view4 = new ImageView(img4);
        view4.setFitHeight(40);
        view4.setPreserveRatio(true);
        btnArchives.setGraphic(view4);

        //Calendrier
        Image img5 = new Image("/calendrier.jpg"); // ou une autre icône
        ImageView view5 = new ImageView(img5);
        view5.setFitHeight(40);
        view5.setPreserveRatio(true);
        btnCalendrier.setGraphic(view5);


        // --- Actions des boutons ---
        btnKanban.setOnAction(e -> changerVue("KANBAN"));
        btnListe.setOnAction(e -> changerVue("LISTE"));
        btnGantt.setOnAction(e -> changerVue("GANTT"));
        btnArchives.setOnAction(e -> changerVue("ARCHIVES"));
        btnCalendrier.setOnAction(e -> changerVue("CALENDRIER"));


        // --- Ajout style bouton ---
        btnKanban.getStyleClass().add("nav-button");
        btnGantt.getStyleClass().add("nav-button");
        btnListe.getStyleClass().add("nav-button");
        btnArchives.getStyleClass().add("nav-button");
        btnCalendrier.getStyleClass().add("nav-button");

        menu.getChildren().addAll(btnKanban, btnListe, btnGantt, btnArchives, btnCalendrier);
        return menu;
    }

    /**
     * Change la vue affichée au centre de l'application
     * @param typeVue Le type de vue à afficher ("KANBAN", "LISTE", "STATS")
     */
    private void changerVue(String typeVue) {
        switch (typeVue) {
            case "KANBAN":
                VueTableau vueKanban = new VueTableau(this.projet, this.controller);
                root.setCenter(vueKanban);
                break;

            case "LISTE":
                VueListe vueListe = new VueListe(this.projet);
                root.setCenter(vueListe);
                break;

            case "GANTT":
                VueGantt vueGantt = new VueGantt(this.projet, this.ganttController);
                root.setCenter(vueGantt);
                break;

            case "ARCHIVES":
                VueArchives vueArchives = new VueArchives(this.projet, this.archiveController);
                root.setCenter(vueArchives);
                break;

            case "CALENDRIER":
                VueCalendrier vueCalendrier = new VueCalendrier(this.projet, this.calendarController);
                root.setCenter(vueCalendrier);
                break;
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        if (this.projet != null) this.projet.sauvegarderGlobalement();
    }
}