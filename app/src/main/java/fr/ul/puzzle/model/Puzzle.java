package fr.ul.puzzle.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Puzzle {

    private String nom;
    private Bitmap imageOriginale;
    private int nbLignes;
    private int nbColonnes;
    private TypeDecoupage typeDecoupage;
    private List<Piece> pieces;

    public Puzzle(String nom, Bitmap imageOriginale, int nbLignes, int nbColonnes, TypeDecoupage typeDecoupage) {
        this.nom = nom;
        this.imageOriginale = imageOriginale;
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.typeDecoupage = typeDecoupage;
        this.pieces = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public Bitmap getImageOriginale() {
        return imageOriginale;
    }

    public int getNbLignes() {
        return nbLignes;
    }

    public int getNbColonnes() {
        return nbColonnes;
    }

    public TypeDecoupage getTypeDecoupage() {
        return typeDecoupage;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setImageOriginale(Bitmap imageOriginale) {
        this.imageOriginale = imageOriginale;
    }

    public void setNbLignes(int nbLignes) {
        this.nbLignes = nbLignes;
    }

    public void setNbColonnes(int nbColonnes) {
        this.nbColonnes = nbColonnes;
    }

    public void setTypeDecoupage(TypeDecoupage typeDecoupage) {
        this.typeDecoupage = typeDecoupage;
    }

    public void setPieces(List<Piece> pieces) {
        this.pieces = pieces;
    }

    public void ajouterPiece(Piece piece) {
        this.pieces.add(piece);
    }
}