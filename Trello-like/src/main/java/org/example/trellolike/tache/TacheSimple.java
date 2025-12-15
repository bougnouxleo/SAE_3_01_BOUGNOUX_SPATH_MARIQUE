package org.example.trellolike.tache;

public class TacheSimple extends Tache {

    /**
     * Duree de la tache
     * IMPORTANT : On retire 'final' pour permettre au XMLDecoder de modifier la valeur après création.
     */
    private int duree;

    /**
     * CONSTRUCTEUR VIDE (OBLIGATOIRE POUR XML)
     * Java crée d'abord new TacheSimple(), puis appelle setDuree().
     */
    public TacheSimple() {
        super(); // Appelle le constructeur vide de la classe mère Tache
    }

    /**
     * Constructeur complet (celui que vous utilisez dans le Main)
     */
    public TacheSimple(String nom, String description, String dateDebut, String dateFin, int duree){
        // Attention : assurez-vous que Tache a aussi un constructeur adapté
        super(nom, description, dateDebut, dateFin);
        this.duree = duree;
    }

    /**
     * SETTER (OBLIGATOIRE POUR XML)
     * Sans ça, la durée sera sauvegardée à 0 lors du rechargement.
     */
    public void setDuree(int duree) {
        this.duree = duree;
    }

    /**
     * GETTER (Utilisé par XMLEncoder pour sauvegarder)
     * Note: J'ajoute getDuree() standard pour la sérialisation.
     */
    public int getDuree() {
        return duree;
    }

    /**
     * Methode métier du pattern Composite
     */
    @Override
    public int getDureeTotale(){
        return duree;
    }

    @Override
    public void afficherDetail(){
        System.out.println("Tâche simple : " + getNom() + " | Durée : " + duree);
    }
}