package fr.ul.puzzle.model;

public class Placement {

    private Piece piece;
    private int ligne;
    private int colonne;

    public Placement(Piece piece, int ligne, int colonne) {
        this.piece = piece;
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getLigne() {
        return ligne;
    }

    public int getColonne() {
        return colonne;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }
}