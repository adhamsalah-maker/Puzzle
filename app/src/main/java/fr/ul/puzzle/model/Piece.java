package fr.ul.puzzle.model;

public class Piece {

    private int id;
    private int ligneCorrecte;
    private int colonneCorrecte;
    private int rotationCourante;
    private int rotationCible;
    private boolean placee;
    private float xCourant;
    private float yCourant;
    private String cheminImage;

    public Piece(int id, int ligneCorrecte, int colonneCorrecte, int rotationCible, String cheminImage) {
        this.id = id;
        this.ligneCorrecte = ligneCorrecte;
        this.colonneCorrecte = colonneCorrecte;
        this.rotationCible = rotationCible;
        this.cheminImage = cheminImage;
        this.rotationCourante = 0;
        this.placee = false;
        this.xCourant = 0;
        this.yCourant = 0;
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

    public int getRotationCourante() {
        return rotationCourante;
    }

    public int getRotationCible() {
        return rotationCible;
    }

    public boolean estPlacee() {
        return placee;
    }

    public float getXCourant() {
        return xCourant;
    }

    public float getYCourant() {
        return yCourant;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public void setRotationCourante(int rotationCourante) {
        this.rotationCourante = rotationCourante;
    }

    public void setRotationCible(int rotationCible) {
        this.rotationCible = rotationCible;
    }

    public void setPlacee(boolean placee) {
        this.placee = placee;
    }

    public void setXCourant(float xCourant) {
        this.xCourant = xCourant;
    }

    public void setYCourant(float yCourant) {
        this.yCourant = yCourant;
    }

    public void setCheminImage(String cheminImage) {
        this.cheminImage = cheminImage;
    }
}