package fr.ul.puzzle.model;

import android.graphics.Bitmap;

public class Piece {

    private int id;
    private int ligneCorrecte;
    private int colonneCorrecte;
    private int rotation;
    private boolean placee;
    private Bitmap image;

    public Piece(int id, int ligneCorrecte, int colonneCorrecte, Bitmap image) {
        this.id = id;
        this.ligneCorrecte = ligneCorrecte;
        this.colonneCorrecte = colonneCorrecte;
        this.image = image;
        this.rotation = 0;
        this.placee = false;
    }

    public int getId() {
        return id;
    }

    public int getLigneCorrecte() {
        return ligneCorrecte;
    }

    public int getColonneCorrecte() {
        return colonneCorrecte;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public boolean estPlacee() {
        return placee;
    }

    public void setPlacee(boolean placee) {
        this.placee = placee;
    }

    public Bitmap getImage() {
        return image;
    }
}