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
import org.example.trellolike.Journal;
import org.example.trellolike.Projet;
import org.example.trellolike.tache.ListeDeTache;
import org.example.trellolike.tache.Tache;
import org.example.trellolike.tache.TacheComposite;
import org.example.trellolike.tache.TacheSimple;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KanbanController {
    /**
     * Le projet associé au Kanban
     */
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
     * État du tri par durée
     */
    private int etatTriDuree = 0; // 0 = pas de tri, 1 = croissant, 2 = décroissant

    /**
     * Constructeur du KanbanController
     * @param projet le projet associé
     */
    public KanbanController(Projet projet) {
        this.projet = projet;
    }

    /**
     * Gère l'ajout d'une nouvelle tâche dans une liste donnée.
     * @param nom le nom de la tâche à ajouter
     * @param listeDest la liste de tâches destination
     */
    public void traiterAjoutTache(String nom, String description, LocalDate dateDebut, LocalDate dateFin, ListeDeTache listeDest, List<Tache> lesDependances, boolean estComposite, int dureeEstimee,String priorite) {
        if (nom == null || nom.trim().isEmpty()) return;

        // On initialise avec les dates choisies par défaut
        LocalDate debutFinal = dateDebut;
        LocalDate finFinal = dateFin;

        // Calcul automatique si des dépendances existent
        if (lesDependances != null && !lesDependances.isEmpty()) {
            LocalDate dateFinLaPlusTardive = null;

            for (Tache dep : lesDependances) {
                if (dep.getDateFin() != null && !dep.getDateFin().isEmpty()) {
                    LocalDate dFin = LocalDate.parse(dep.getDateFin());
                    // On cherche la date de fin la plus éloignée
                    if (dateFinLaPlusTardive == null || dFin.isAfter(dateFinLaPlusTardive)) {
                        dateFinLaPlusTardive = dFin;
                    }
                }
            }

            if (dateFinLaPlusTardive != null) {
                // La nouvelle tâche commence là où la dépendance la plus longue s'arrête
                debutFinal = dateFinLaPlusTardive.plusDays(1);

                // Pour une tâche simple, on calcule la fin en ajoutant la durée saisie
                if (!estComposite) {
                    finFinal = debutFinal.plusDays(dureeEstimee);
                }
                // Si c'est un projet, on s'assure qu'il finit au moins le même jour que son début
                else if (finFinal.isBefore(debutFinal)) {
                    finFinal = debutFinal.plusDays(1);
                }
            }
        }

        // Conversion en String pour le constructeur
        String strDebut = (debutFinal != null) ? debutFinal.toString() : "";
        String strFin = (finFinal != null) ? finFinal.toString() : "";

        Tache nouvelleTache;

        if (estComposite) {
            nouvelleTache = new TacheComposite(nom, description, strDebut, strFin, priorite);
        } else {
            nouvelleTache = new TacheSimple(nom, description, strDebut, strFin, dureeEstimee, priorite);
        }

        // Gestion commune des dépendances (grâce à la classe mère Tache)
        if (lesDependances != null) {
            for (Tache dep : lesDependances) {
                nouvelleTache.ajouterDependance(dep);
            }
        }

        // Log + Ajout + Sauvegarde
        Journal.log("Ajout de la tâche '" + nom + "' dans la liste '" + listeDest.getNom() + "'");
        listeDest.ajouterTache(nouvelleTache);
        projet.sauvegarderGlobalement();
    }

    /**
     * Gère l'ajout d'une nouvelle liste de tâches au projet.
     * @param nomListe
     */
    public void traiterAjoutListe(String nomListe) {
        if (nomListe == null || nomListe.trim().isEmpty()) return;

        ListeDeTache nouvelleListe = new ListeDeTache(nomListe);
        Journal.log("Ajout de la liste de tâches '" + nomListe + "'");
        projet.ajouterListe(nouvelleListe);
        projet.sauvegarderGlobalement();
    }

    /**
     * Gère le renommage d'une liste de tâches
     * @param liste la liste à renommer
     * @param nouveauNom le nouveau nom
     */
    public void traiterRenommerListe(ListeDeTache liste, String nouveauNom) {
        liste.setNom(nouveauNom);
        Journal.log("Renommage de la liste de tâches en '" + nouveauNom + "'");
        projet.sauvegarderGlobalement();
    }


    /**
     * Reçoit les données brutes de la Vue et effectue l'action métier.
     */
    public void traiterAjoutEtiquette(Tache tache, String nom, String codeCouleurHex) {
        // 1. Logique métier : Création de l'objet
        Etiquette nouvelleEtiquette = new Etiquette(nom, codeCouleurHex);

        // 2. Modification du Modèle
        tache.ajouterEtiquette(nouvelleEtiquette);

        // Log
        Journal.log("Ajout de l'étiquette '" + nom + "' à la tâche '" + tache.getNom() + "'");

        // 3. Persistance (Sauvegarde XML)
        projet.sauvegarderGlobalement();
    }

    public void traiterSuppressionEtiquette(Tache tache, Etiquette etiquette) {
        // 1. Modif du modèle
        tache.retirerEtiquette(etiquette);

        // 2. Sauvegarde (Ecrit le fichier XML)
        Journal.log("Suppression de l'étiquette '" + etiquette.getNom() + "' de la tâche '" + tache.getNom() + "'");
        projet.sauvegarderGlobalement();
    }
    /**
     * Gère l'archivage d'une liste de tâches
     * @param liste la liste à archiver
     */
    public void traiterArchiverListeDeTaches(ListeDeTache liste) {
        Journal.log("Archivage de la liste de tâches '" + liste.getNom() + "'");
        projet.archiverListeDeTaches(liste);
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
            Journal.log("Mise à jour de la priorité de la tâche '" + t.getNom() + "' en '" + comboPrio.getValue() + "'");
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

                duree.setText("Durée estimée : " + t.getDureeTotale() + "J");

                Journal.log("Mise à jour des sous-tâches de la tâche composite '" + t.getNom() + "'");
                projet.sauvegarderGlobalement();
            });

            boxComposite.getChildren().addAll(lblSousTaches, listSelectionEnfants, btnValiderCompo);
        }

        Button btnSaveDesc = new Button("Sauvegarder Description");
        btnSaveDesc.setOnAction(e -> {
            t.setDescription(description.getText());
            Journal.log("Mise à jour de la description de la tâche '" + t.getNom() + "'");
            projet.sauvegarderGlobalement();
        });

        Button btnArchiver = new Button("Archiver la tâche");
        btnArchiver.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
        btnArchiver.setOnAction(e -> {
            projet.archiverTache(t);
            Journal.log("Archivage de la tâche '" + t.getNom() + "'");
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
     * Gère le dépôt d'une tâche dans une nouvelle liste.
     * @param idTache l'identifiant de la tâche à déplacer
     * @param listeDestination la liste de tâches destination
     */
    public void traiterDepotTache(int idTache, ListeDeTache listeDestination) {
        // CORRECTION MAJEURE : On demande au PROJET, pas à la classe statique
        // Cela garantit qu'on récupère l'objet qui contient bien les sous-tâches chargées
        Tache tache = projet.trouverTacheParId(idTache);

        if (tache == null) {
            System.err.println("Erreur : Tâche introuvable dans le projet pour ID " + idTache);
            return;
        }

        ListeDeTache listeSource = projet.trouverListeDeLaTache(tache);

        // Optimisation : Si on lâche au même endroit, on stop.
        if (listeSource != null && listeSource.equals(listeDestination)) return;

        // Sécurité : Si la source est null (cas rare), on ne peut pas déplacer
        if (listeSource == null) return;

        try {
            // Appel de la méthode récursive du projet
            projet.deplacerTache(tache, listeSource, listeDestination);

            // La sauvegarde est incluse dans deplacerTache, mais par sécurité :
            projet.sauvegarderGlobalement();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du déplacement : " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Met à jour les filtres de recherche pour les tâches.
     * @param texte
     * @param priorite
     */
    public void mettreAJourFiltres(String texte, String priorite) {
        this.filtreTexte = (texte != null) ? texte.toLowerCase() : "";
        this.filtrePriorite = (priorite != null) ? priorite : "Toutes les priorités";
        projet.sauvegarderGlobalement();
    }

    /**
     * Détermine si une tâche doit être affichée selon les filtres actuels.
     * @param t la tâche à vérifier
     * @return true si la tâche doit être affichée, false sinon
     */
    public boolean doitAfficherTache(Tache t) {
        String nom = (t.getNom() != null) ? t.getNom().toLowerCase() : "";
        String desc = (t.getDescription() != null) ? t.getDescription().toLowerCase() : "";
        String prio = (t.getPriorite() != null) ? t.getPriorite() : "Moyenne";

        boolean matchTexte = nom.contains(filtreTexte) || desc.contains(filtreTexte);
        boolean matchPriorite = filtrePriorite.equals("Toutes les priorités") || prio.equals(filtrePriorite);

        return matchTexte && matchPriorite;
    }

    /**
     * Traite le déplacement d'une colonne.
     */
    public void traiterDeplacementListe(int indexSource, int indexCible) {
        Journal.log("Déplacement de la liste de tâches de l'index " + indexSource + " vers l'index " + indexCible);
        projet.deplacerListe(indexSource, indexCible);
    }
    /**
     * Met à jour l'état du tri par durée.
     * 0 = pas de tri, 1 = croissant, 2 = décroissant
     */
    public void mettreAJourTriDuree() {
        this.etatTriDuree = (this.etatTriDuree + 1) % 3;
        projet.sauvegarderGlobalement();
    }

    /**
     * Getter état tri durée
     * @return l'état du tri durée
     */
    public int getEtatTriDuree() {
        return this.etatTriDuree;
    }
    /**
     * Trie une liste de tâches selon l'état du tri par durée.
     * @param liste la liste de tâches à trier
     */
    public void trierTaches(List<Tache> liste) {
        if (etatTriDuree == 1) { // Croissant
            liste.sort(Comparator.comparingInt(Tache::getDureeTotale));
        } else if (etatTriDuree == 2) { // Décroissant
            liste.sort((t1, t2) -> Integer.compare(t2.getDureeTotale(), t1.getDureeTotale()));
        }
    }

}