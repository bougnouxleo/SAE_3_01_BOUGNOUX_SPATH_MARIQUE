package org.example.trellolike;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.tache.TacheSimple;
import org.example.trellolike.vue.VueTableau;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    @Override
    public void start(Stage stage) {
        // 1. CHARGEMENT
        this.projet = Projet.getInstance();
        if (this.projet.getListes().isEmpty()) {
            initialiserDonneesDeTest();
        }
        this.controller = new KanbanController(this.projet);

        // 2. CRÉATION DU LAYOUT PRINCIPAL (BorderPane)
        this.root = new BorderPane();

        // 3. CRÉATION DU MENU (Barre de navigation)
        HBox menuBar = creerBarreDeNavigation();

        // On place le menu tout en haut
        root.setTop(menuBar);

        // 4. AFFICHAGE DE LA VUE PAR DÉFAUT (Le Kanban)
        changerVue("KANBAN");

        // 5. CONFIGURATION DE LA SCÈNE
        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("SAE 3.01 - Gestionnaire de Tâches (" + projet.getNom() + ")");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Crée la barre de menu avec les 3 boutons
     */
    private HBox creerBarreDeNavigation() {
        HBox menu = new HBox(20);
        menu.setPadding(new Insets(15));
        menu.setStyle("-fx-background-color: violet;");
        menu.setAlignment(Pos.CENTER_LEFT);

        // --- Création des boutons ---
        Button btnKanban = new Button("Vue Kanban");
        Button btnListe = new Button("Vue Liste");
        Button btnGantt = new Button("Vue Gantt");

        //Images des boutons
        //Gantt
        Image img = new Image("/gantt.jpg");
        ImageView view = new ImageView(img);
        view.setFitHeight(40);
        view.setPreserveRatio(true);

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

        // Style commun des boutons (Blanc sur gris)
        String styleBtn = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px; -fx-border-color: white; -fx-border-radius: 5;";
        btnKanban.setStyle(styleBtn);
        btnListe.setStyle(styleBtn);
        btnGantt.setStyle(styleBtn);
        btnGantt.setGraphic(view);

        // --- Actions des boutons ---
        btnKanban.setOnAction(e -> changerVue("KANBAN"));
        btnListe.setOnAction(e -> changerVue("LISTE"));
        btnGantt.setOnAction(e -> changerVue("STATS"));

        menu.getChildren().addAll(btnKanban, btnListe, btnGantt);
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
                //root.setCenter(creerVueListe());
                break;

            case "STATS":
                //root.setCenter(creerVueGantt());
                break;
        }
    }

    /**
     * Crée une vue liste bidon (à remplacer par la vraie vue liste)
     */
    private void initialiserDonneesDeTest() {
        System.out.println("Création du jeu de données de test...");
        ListeDeTache todo = new ListeDeTache("À Faire");
        ListeDeTache doing = new ListeDeTache("En Cours");
        ListeDeTache done = new ListeDeTache("Terminé");
        projet.ajouterListe(todo);
        projet.ajouterListe(doing);
        projet.ajouterListe(done);
        Tache t1 = new TacheSimple("Configurer Git",null,null,null,0);
        Tache t2 = new TacheSimple("Faire la maquette Figma",null,null,null,0);
        Tache t3 = new TacheSimple("Coder le Modèle Java",null,null,null,0);
        done.ajouterTache(t1);
        todo.ajouterTache(t2);
        todo.ajouterTache(t3);
        projet.sauvegarderGlobalement();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        if (this.projet != null) this.projet.sauvegarderGlobalement();
    }
}