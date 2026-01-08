package org.example.trellolike.log;

public class Journal {

    /**
     * Nom du fichier de journalisation
     */
    private static final String NOM_FICHIER = "journal.log";


    /**
     * Ajoute une entrée au journal avec un message et un horodatage
     * @param message Le message à enregistrer
     */
    public static void log(String message) {
        try (java.io.FileWriter fw = new java.io.FileWriter(NOM_FICHIER, true);
             java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
             java.io.PrintWriter out = new java.io.PrintWriter(bw)) {
            out.println(java.time.LocalDateTime.now() + " - " + message);
        } catch (java.io.IOException e) {
            System.err.println("Erreur lors de l'écriture du log : " + e.getMessage());
        }
    }
}
