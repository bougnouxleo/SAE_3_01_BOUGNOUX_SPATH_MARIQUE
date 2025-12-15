package org.example.trellolike.tache;

import org.example.trellolike.Projet;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;

public class GestionPersistance {

    private static final String NOM_FICHIER = "sauvegarde_projet.xml";

    // Sauvegarde tout le projet d'un coup
    public static void sauvegarder(Projet projet) {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(NOM_FICHIER)))) {
            encoder.writeObject(projet);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Charge le projet au démarrage
    public static Projet charger() {
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(NOM_FICHIER)))) {
            return (Projet) decoder.readObject();
        } catch (FileNotFoundException e) {
            // Si le fichier n'existe pas (premier lancement), on renvoie un projet vide
            return new Projet("Nouveau Projet");
        }
    }
}