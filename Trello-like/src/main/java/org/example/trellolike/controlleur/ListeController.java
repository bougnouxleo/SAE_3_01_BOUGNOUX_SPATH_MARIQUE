package org.example.trellolike.controlleur;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.trellolike.Etiquette;
import org.example.trellolike.Projet;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.tache.TacheComposite;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class ListeController {

    private Projet projet;
    /**
     * Texte de filtre pour la recherche dans les tâches
     */
    private String filtreTexte = "";

    /**
     * Texte de filtre pour la priorité des tâches
     */
    private String filtrePriorite = "Toutes les priorités";

    /**
     * Constructeur du contrôleur de la vue Liste
     * @param projet
     */
    public ListeController(Projet projet) {
        this.projet = projet;
    }

    /**
     * Récupère toutes les tâches du projet et les groupe par jour.
     * @return Une Map où la clé est la Date et la valeur est la liste des tâches de ce jour.
     */
    public Map<LocalDate, List<Tache>> getTachesGroupeesParJour() {
        Map<LocalDate, List<Tache>> resultat = new TreeMap<>(); // TreeMap pour trier les dates

        // 1. On récupère TOUTES les tâches de TOUTES les listes (Aplatissement)
        List<Tache> toutesLesTaches = new ArrayList<>();
        for (ListeDeTache liste : projet.getListeDeTaches()) { // ou getListes()
            toutesLesTaches.addAll(liste.getTaches());
        }

        // 2. On trie et groupe par date
        for (Tache t : toutesLesTaches) {
            boolean matchTexte = t.getNom().toLowerCase().contains(filtreTexte) ||
                    t.getDescription().toLowerCase().contains(filtreTexte);

            boolean matchPriorite = filtrePriorite.equals("Toutes les priorités") ||
                    (t.getPriorite() != null && t.getPriorite().equals(filtrePriorite));

            if (matchTexte && matchPriorite) {
                LocalDate date = parserDate(t.getDateDebut()); // Supposons qu'on trie par date de début
                if (date != null) {
                    resultat.computeIfAbsent(date, k -> new ArrayList<>()).add(t);
                }
            }
        }
        return resultat;
    }

    /**
     * Helper pour transformer le String date de la Tache en LocalDate Java
     */
    private LocalDate parserDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            // Adaptez le pattern selon votre format de stockage (ex: "yyyy-MM-dd" ou "dd/MM/yyyy")
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Formate une date pour l'affichage (ex: "Lundi")
     */
    public String getNomJour(LocalDate date) {
        // Retourne "Lundi", "Mardi"... en français
        String jour = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        return jour.substring(0, 1).toUpperCase() + jour.substring(1); // Première lettre majuscule
    }
    /**
     * Méthode partagée avec KanbanController : Ajout d'étiquette + Sauvegarde
     */
    public void traiterAjoutEtiquette(Tache tache, String nom, String codeCouleurHex) {
        Etiquette nouvelleEtiquette = new Etiquette(nom, codeCouleurHex);
        tache.ajouterEtiquette(nouvelleEtiquette);
        projet.sauvegarderGlobalement();
    }
    /**
     * Méthode partagée avec KanbanController : Suppression d'étiquette + Sauvegarde
     */
    public void traiterSuppressionEtiquette(Tache tache, Etiquette etiquette) {
        // 1. Modif du modèle
        tache.retirerEtiquette(etiquette);

        // 2. Sauvegarde (Ecrit le fichier XML)
        projet.sauvegarderGlobalement();
    }
    /**
     * Gère l'ouverture de la fenêtre de détails d'une tâche.
     * @param t la tâche dont on veut afficher les détails
     */
    public void traiterOuvertureDetail(Tache t) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("Détails : " + t.getNom());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label titre = new Label(t.getNom());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label dates = new Label("Début : " + t.getDateDebut() + " | Fin : " + t.getDateFin());

        Label duree = new Label("Durée estimée : " + t.getDureeTotale() + "J");
        duree.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");

        Label lblPrioTitle = new Label("Priorité :");
        lblPrioTitle.setStyle("-fx-font-weight: bold;");

        ComboBox<String> comboPrio = new ComboBox<>();
        comboPrio.getItems().addAll("Basse", "Moyenne", "Haute","Urgente");

        // On récupère la priorité actuelle
        String prioriteActuelle = (t.getPriorite() != null) ? t.getPriorite() : "Moyenne";
        comboPrio.setValue(prioriteActuelle);

        // Mise à jour automatique lors du changement
        comboPrio.setOnAction(e -> {
            t.setPriorite(comboPrio.getValue());
            projet.sauvegarderGlobalement(); // Sauvegarde immédiate
        });

        // Horizontal Box pour aligner le label et la combo
        HBox boxPrio = new HBox(10, lblPrioTitle, comboPrio);
        boxPrio.setAlignment(Pos.CENTER_LEFT);

        Label lblDesc = new Label("Description :");
        TextArea description = new TextArea(t.getDescription());
        description.setEditable(true);
        description.setWrapText(true);
        description.setMaxHeight(100);

        VBox boxDependances = new VBox(5);
        List<Tache> tachesBloquantes = new ArrayList<>();

        for (Integer idDep : t.getIdsDependances()) {
            Tache dep = Tache.findById(idDep);
            if (dep != null && !projet.estTacheTerminee(dep)) {
                tachesBloquantes.add(dep);
            }
        }

        if (!tachesBloquantes.isEmpty()) {
            Label lblAlerte = new Label("⚠️ BLOQUÉE par " + tachesBloquantes.size() + " tâche(s) :");
            lblAlerte.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            boxDependances.getChildren().add(lblAlerte);

            for (Tache bloquant : tachesBloquantes) {
                Label lblNom = new Label(" • " + bloquant.getNom());
                lblNom.setStyle("-fx-text-fill: red; -fx-padding: 0 0 0 20;");

                boxDependances.getChildren().add(lblNom);
            }
        } else {
            Label lblOk = new Label("✅ Aucune dépendance bloquante.");
            lblOk.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            boxDependances.getChildren().add(lblOk);
        }

        VBox boxComposite = new VBox(5);

        if (t instanceof TacheComposite) {
            TacheComposite composite = (TacheComposite) t;

            Label lblSousTaches = new Label("Tâches dépendantes :");
            lblSousTaches.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

            ListView<Tache> listSelectionEnfants = new ListView<>();
            listSelectionEnfants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listSelectionEnfants.setMaxHeight(120);

            List<Tache> candidats = new ArrayList<>();
            for (ListeDeTache liste : projet.getListeDeTaches()) {
                candidats.addAll(liste.getTaches());
            }
            candidats.remove(t);

            listSelectionEnfants.getItems().addAll(candidats);

            for (Tache sousTache : composite.getSousTaches()) {
                listSelectionEnfants.getSelectionModel().select(sousTache);
            }

            Button btnValiderCompo = new Button("Mettre à jour les sous-tâches");
            btnValiderCompo.setOnAction(e -> {
                List<Tache> selection = listSelectionEnfants.getSelectionModel().getSelectedItems();
                composite.setSousTaches(new ArrayList<>(selection));

                duree.setText("Durée estimée : " + t.getDureeTotale() + "h");

                projet.sauvegarderGlobalement();
            });

            boxComposite.getChildren().addAll(lblSousTaches, listSelectionEnfants, btnValiderCompo);
        }

        Button btnSaveDesc = new Button("Sauvegarder Description");
        btnSaveDesc.setOnAction(e -> {
            t.setDescription(description.getText());
            projet.sauvegarderGlobalement();
        });

        Button btnArchiver = new Button("Archiver la tâche");
        btnArchiver.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
        btnArchiver.setOnAction(e -> {
            projet.archiverTache(t);
            detailStage.close();
        });

        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> detailStage.close());

        layout.getChildren().addAll(
                titre, dates, duree,
                boxPrio,
                boxDependances,
                boxComposite,
                lblDesc, description, btnSaveDesc,
                btnArchiver, btnFermer
        );

        Scene scene = new Scene(layout, 450, 750);
        detailStage.setScene(scene);
        detailStage.show();
    }

    /**
     * Met à jour les filtres de recherche pour les tâches.
     * @param texte
     * @param priorite
     */
    public void mettreAJourFiltres(String texte, String priorite) {
        this.filtreTexte = texte.toLowerCase();
        this.filtrePriorite = priorite;
        projet.sauvegarderGlobalement();
    }


}