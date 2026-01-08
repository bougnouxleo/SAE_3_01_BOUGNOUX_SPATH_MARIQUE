package org.example.trellolike.controlleur;

import org.example.trellolike.Projet;
import org.example.trellolike.tache.Tache;

public class CalendarController {
    private Projet projet;

    public CalendarController(Projet projet) {
        this.projet = projet;
    }

    public void traiterOuvertureDetail(Tache t) {
        System.out.println("Détail de : " + t.getNom());
    }
}