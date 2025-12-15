package org.example.trellolike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.trellolike.controlleur.KanbanController;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.tache.TacheSimple;
import org.example.trellolike.vue.VueTableau;

public class Main extends Application {

    private Projet projet;

    @Override
    public void start(Stage stage) {
        // 1. CHARGEMENT DU MODÈLE (PERSISTANCE XML)
        // On essaye de récupérer la sauvegarde. Si elle n'existe pas, ça crée un projet vide.
        // Assurez-vous d'avoir implémenté la méthode statique dans Projet ou GestionPersistance
        this.projet = Projet.getInstance();

        // 2. JEU DE DONNÉES DE TEST (Seulement si nouveau projet)
        // Cela permet de tester visuellement sans devoir tout créer à la main à chaque fois
        if (this.projet.getListes().isEmpty()) {
            initialiserDonneesDeTest();
        }

        // 3. INITIALISATION DU CONTROLEUR
        // Le contrôleur a besoin du modèle pour effectuer les actions (déplacer, etc.)
        KanbanController controller = new KanbanController(this.projet);

        // 4. INITIALISATION DE LA VUE (OBSERVATEUR)
        // La vue a besoin du modèle (pour s'abonner) et du contrôleur (pour les actions)
        VueTableau root = new VueTableau(this.projet, controller);

        // 5. CONFIGURATION DE LA FENÊTRE
        // On utilise 'root' (notre VueTableau qui hérite de HBox) directement comme racine
        Scene scene = new Scene(root, 1024, 768); // Taille plus confortable pour un Kanban

        // Ajout d'une feuille de style CSS (Optionnel mais recommandé pour le visuel des cartes)
        // scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("SAE 3.01 - Gestionnaire de Tâches (" + projet.getNom() + ")");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Crée des données bidons pour tester les fonctionnalités :
     * - Colonnes
     * - Tâches simples
     * - Tâche bloquée (Dépendance)
     */
    private void initialiserDonneesDeTest() {
        System.out.println("Création du jeu de données de test...");

        // A. Création des Listes (Colonnes)
        ListeDeTache todo = new ListeDeTache("À Faire");
        ListeDeTache doing = new ListeDeTache("En Cours");
        ListeDeTache done = new ListeDeTache("Terminé");

        // Ajout au projet
        projet.ajouterListe(todo);
        projet.ajouterListe(doing);
        projet.ajouterListe(done);

        // B. Création de Tâches
        Tache t1 = new TacheSimple("Configurer Git",null,null,null,0);
        Tache t2 = new TacheSimple("Faire la maquette Figma",null,null,null,0);
        Tache t3 = new TacheSimple("Coder le Modèle Java",null,null,null,0);


        // D. Ajout des tâches dans les listes
        done.ajouterTache(t1);      // Celle-ci est finie
        todo.ajouterTache(t2);
        todo.ajouterTache(t3);
        // E. Sauvegarde initiale
        projet.sauvegarderGlobalement();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        // Sauvegarde automatique à la fermeture de l'application
        if (this.projet != null) {
            this.projet.sauvegarderGlobalement();
        }
    }
}