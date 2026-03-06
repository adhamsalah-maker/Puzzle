package fr.ul.puzzle.model;

import java.util.ArrayList;
import java.util.List;

public class Puzzle {

    private int id;
    private String nom;
    private String cheminImage;
    private int largeurImage;
    private int hauteurImage;
    private int nbLignes;
    private int nbColonnes;
    private TypeDecoupage typeDecoupage;
    private List<Piece> pieces;

    public Puzzle(int id, String nom, String cheminImage, int largeurImage, int hauteurImage,
                  int nbLignes, int nbColonnes, TypeDecoupage typeDecoupage) {

        this.id = id;
        this.nom = nom;
        this.cheminImage = cheminImage;
        this.largeurImage = largeurImage;
        this.hauteurImage = hauteurImage;
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.typeDecoupage = typeDecoupage;
        this.pieces = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public int getLargeurImage() {
        return largeurImage;
    }

    public int getHauteurImage() {
        return hauteurImage;
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

    public void ajouterPiece(Piece piece) {
        pieces.add(piece);
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCheminImage(String cheminImage) {
        this.cheminImage = cheminImage;
    }

    public void setLargeurImage(int largeurImage) {
        this.largeurImage = largeurImage;
    }

    public void setHauteurImage(int hauteurImage) {
        this.hauteurImage = hauteurImage;
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
}