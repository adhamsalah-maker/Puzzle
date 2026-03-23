package fr.ul.puzzle.model;

public class PartieSauvegardee {
    private final String nomPuzzle;
    private final String nomFichier;
    private final String cheminImage;
    private final String cheminDossierPuzzle;

    public PartieSauvegardee(String nomPuzzle, String nomFichier, String cheminImage, String cheminDossierPuzzle) {
        this.nomPuzzle = nomPuzzle;
        this.nomFichier = nomFichier;
        this.cheminImage = cheminImage;
        this.cheminDossierPuzzle = cheminDossierPuzzle;
    }

    public String getNomPuzzle() {
        return nomPuzzle;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public String getCheminDossierPuzzle() {
        return cheminDossierPuzzle;
    }
}