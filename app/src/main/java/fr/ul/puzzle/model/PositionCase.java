package fr.ul.puzzle.model;

public class PositionCase {
    private int ligne;
    private int colonne;

    public PositionCase(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public int getLigne() {
        return ligne;
    }

    public int getColonne() {
        return colonne;
    }
}